package com.example.testapp.Widget;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.testapp.Util.DisplayUtil;

/**
 * Created by huangwei on 14-9-16.
 */

enum ChangeMode {
    L, T, R, B, LT, LB, RT, RB, MOVE
}

public class CutView extends View {

    private int width, height;
    private RectF frameRect, beforeRect;
    private Paint bgPaint, framePaint;
    private Path path;
    private PathEffect effects;
    private ChangeMode mode;

    private float downX, downY;
    private float moveX, moveY;
    /**
     * 图片显示范围
     */
    private RectF bounds;
    /**
     * 裁剪的最小范围
     */
    private int minSpace;
    /**
     * 球半径
     */
    private int radius;

    public CutView(Context context) {
        super(context);
        init();
    }

    public CutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        radius = DisplayUtil.dip2px(getContext(), 10);
        minSpace = radius * 3;
        path = new Path();

        effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        bgPaint = new Paint();
        bgPaint.setColor(Color.argb(150, 0, 0, 0));

        frameRect = new RectF(50, 50, 500, 500);

        framePaint = new Paint();
        framePaint.setAntiAlias(true);
        framePaint.setColor(Color.WHITE);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(6);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        if (bounds == null)
            bounds = new RectF(0, 0, width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawFrame(canvas);
    }

    private void drawBackground(Canvas canvas) {
        //上
        canvas.drawRect(0, 0, width, frameRect.top, bgPaint);
        //左
        canvas.drawRect(0, frameRect.top, frameRect.left, height, bgPaint);
        //下
        canvas.drawRect(frameRect.left, frameRect.bottom, width, height, bgPaint);
        //右
        canvas.drawRect(frameRect.right, frameRect.top, width, frameRect.bottom, bgPaint);
    }

    private void drawFrame(Canvas canvas) {

        framePaint.setPathEffect(null);
        framePaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(frameRect, framePaint);
        //四个球,顺时针
        framePaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(frameRect.left, frameRect.top, radius, framePaint);
        canvas.drawCircle(frameRect.right, frameRect.top, radius, framePaint);
        canvas.drawCircle(frameRect.left, frameRect.bottom, radius, framePaint);
        canvas.drawCircle(frameRect.right, frameRect.bottom, radius, framePaint);
        //横虚线
        PathEffect effects = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);
        framePaint.setPathEffect(effects);
        framePaint.setStyle(Paint.Style.STROKE);

        float incrementY = (frameRect.bottom - frameRect.top) / 3;
        float y1 = frameRect.top + incrementY;
        float y2 = y1 + incrementY;
        path.reset();
        path.moveTo(frameRect.left, y1);
        path.lineTo(frameRect.right, y1);
        canvas.drawPath(path, framePaint);

        path.reset();
        path.moveTo(frameRect.left, y2);
        path.lineTo(frameRect.right, y2);
        canvas.drawPath(path, framePaint);
        //竖虚线
        float incrementX = (frameRect.right - frameRect.left) / 3;
        float x1 = frameRect.left + incrementX;
        float x2 = x1 + incrementX;

        path.moveTo(x1, frameRect.top);
        path.lineTo(x1, frameRect.bottom);
        canvas.drawPath(path, framePaint);

        path.reset();
        path.moveTo(x2, frameRect.top);
        path.lineTo(x2, frameRect.bottom);
        canvas.drawPath(path, framePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beforeRect = new RectF(frameRect);
                downX = event.getX();
                downY = event.getY();
                mode = getChangeMode(downX, downY);
                Log.d("hwLog", "mode:" + (mode == null ? "null" : mode.toString()));
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();

                change(moveX - downX, moveY - downY);
                downX = moveX;
                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return true;
    }

    private void change(float increX, float increY) {
        if (mode == null)
            return;
        float temp = 0f;
        switch (mode) {
            case MOVE:
                if (frameRect.left + increX < bounds.left || frameRect.top + increY < bounds.top || frameRect.right + increX > bounds.right
                        || frameRect.bottom + increY > bounds.bottom)
                    return;
                frameRect.left = frameRect.left + increX;
                frameRect.top = frameRect.top + increY;
                frameRect.right = frameRect.right + increX;
                frameRect.bottom = frameRect.bottom + increY;
                break;
            case LT:
                temp = frameRect.left + increX;
                if (temp < frameRect.right - minSpace && temp >= bounds.left)
                    frameRect.left = temp;
                temp = frameRect.top + increY;
                if (temp < frameRect.bottom - minSpace && temp >= bounds.top)
                    frameRect.top = temp;
                break;
            case RT:
                temp = frameRect.right + increX;
                if (temp > frameRect.left + minSpace && temp <= bounds.right)
                    frameRect.right = temp;
                temp = frameRect.top + increY;
                if (temp < frameRect.bottom - minSpace && temp >= bounds.top)
                    frameRect.top = temp;
                break;
            case RB:
                temp = frameRect.right + increX;
                if (temp > frameRect.left + minSpace && temp <= bounds.right)
                    frameRect.right = temp;
                temp = frameRect.bottom + increY;
                if (temp > frameRect.top + minSpace && temp <= bounds.bottom)
                    frameRect.bottom = temp;
                break;
            case LB:
                temp = frameRect.left + increX;
                if (temp < frameRect.right - minSpace && temp >= bounds.left)
                    frameRect.left = temp;
                temp = frameRect.bottom + increY;
                if (temp > frameRect.top + minSpace && temp <= bounds.bottom)
                    frameRect.bottom = temp;
                break;
            case T:
                temp = frameRect.top + increY;
                if (temp < frameRect.bottom - minSpace && temp >= bounds.top)
                    frameRect.top = temp;
                break;
            case R:
                temp = frameRect.right + increX;
                if (temp > frameRect.left + minSpace && temp <= bounds.right)
                    frameRect.right = temp;
                break;
            case B:
                temp = frameRect.bottom + increY;
                if (temp > frameRect.top + minSpace && temp <= bounds.bottom)
                    frameRect.bottom = temp;
                break;
            case L:
                temp = frameRect.left + increX;
                if (temp < frameRect.right - minSpace && temp >= bounds.left)
                    frameRect.left = temp;
                break;
            default:
                return;
        }
        invalidate();
    }

    private ChangeMode getChangeMode(float x, float y) {   //框内
        if (x > frameRect.left + radius && x < frameRect.right - radius && y > frameRect.top + radius && y < frameRect.bottom - radius)
            mode = ChangeMode.MOVE;
            //四个球,顺时针
        else if (x > frameRect.left - radius && x < frameRect.left + radius && y > frameRect.top - radius && y < frameRect.top + radius)
            mode = ChangeMode.LT;
        else if (x > frameRect.right - radius && x < frameRect.right + radius && y > frameRect.top - radius && y < frameRect.top + radius)
            mode = ChangeMode.RT;
        else if (x > frameRect.right - radius && x < frameRect.right + radius && y > frameRect.bottom - radius && y < frameRect.bottom + radius)
            mode = ChangeMode.RB;
        else if (x > frameRect.left - radius && x < frameRect.left + radius && y > frameRect.bottom - radius && y < frameRect.bottom + radius)
            mode = ChangeMode.LB;
            //四条线
        else if (x > frameRect.left + radius && x < frameRect.right - radius && y > frameRect.top - radius && y < frameRect.top + radius)
            mode = ChangeMode.T;
        else if (x > frameRect.right - radius && x < frameRect.right + radius && y > frameRect.top + radius && y < frameRect.bottom - radius)
            mode = ChangeMode.R;
        else if (x > frameRect.left + radius && x < frameRect.right - radius && y > frameRect.bottom - radius && y < frameRect.bottom + radius)
            mode = ChangeMode.B;
        else if (x > frameRect.left - radius && x < frameRect.left + radius && y > frameRect.top + radius && y < frameRect.bottom - radius)
            mode = ChangeMode.L;
        else
            mode = null;
        return mode;
    }


    public void setBounds(RectF bounds) {
        frameRect = new RectF(bounds);
        this.bounds = bounds;
        invalidate();
    }

    public RectF getCutBounds() {
        return new RectF(frameRect.left - bounds.left, frameRect.top - bounds.top, frameRect.right - bounds.left, frameRect.bottom - bounds.top);

    }

}
