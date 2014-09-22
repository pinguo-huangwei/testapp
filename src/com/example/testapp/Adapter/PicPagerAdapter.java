package com.example.testapp.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import com.example.testapp.Fragment.PicEditFragment;
import com.example.testapp.R;
import com.example.testapp.Util.DisplayUtil;
import com.example.testapp.Util.PicUtil;
import com.example.testapp.Widget.MViewPager;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by huangwei on 14-9-19.
 */
public class PicPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, View.OnTouchListener {

    private Activity activity;
    public String[] picArray;
    public MViewPager mViewPager;

    /**
     * 最大的缩小比例
     */
    private static final float SCALE_MIN = 0.5f;
    /**
     * 保存position与对于的View
     */
    private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<Integer, View>();

    private int padding;
    //初始化时显示第几项
    private int initIndex;
    public int curPageIndex;

    private GestureDetector detector;
    private ScaleGestureDetector scaleDetector;

    private float newDist;
    Matrix mMatrix = new Matrix();
    private float[] beginValues;

    public ImageView scaledImageView;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix centerMatrix = new Matrix();

    static final int NONE = 0;
    static final int VIEWDRAG = 1;
    static final int ZOOM = 2;
    static final int PICDRAG = 3;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    public PicPagerAdapter(Activity activity, final MViewPager mViewPager, String[] picArray, final int initIndex) {
        this.activity = activity;
        this.picArray = picArray;
        this.mViewPager = mViewPager;
        this.initIndex = initIndex;
        detector = new GestureDetector(activity, new MySimpleGestureListener(activity, this));
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOnTouchListener(this);
        padding = DisplayUtil.dip2px(activity, 16);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(initIndex, false);
            }
        });

    }

    @Override
    public int getCount() {
        return picArray == null ? 0 : picArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(PicUtil.getDefaultBitmapFormPath(picArray[position]));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(padding, padding, padding, padding);
//        imageView.setOnTouchListener(this);
        imageView.setTag(picArray[position]);
        container.addView(imageView);
        setObjectForPosition(imageView, position);
        return imageView;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //滑动特别小的距离时，我们认为没有动，可有可无的判断
        float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

        //获取左边的View
        View mLeft = findViewFromObject(position);
        //获取右边的View
        View mRight = findViewFromObject(position + 1);

        // 添加切换动画效果
        animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);

    }

    public void setObjectForPosition(View view, int position) {
        mChildrenViews.put(position, view);
    }

    /**
     * 通过过位置获得对应的View
     *
     * @param position
     * @return
     */
    public View findViewFromObject(int position) {
        return mChildrenViews.get(position);
    }

    private boolean isSmall(float positionOffset) {
        return Math.abs(positionOffset) < 0.0001;
    }

    protected void animateStack(View left, View right, float effectOffset,
                                int positionOffsetPixels) {
        if (right != null) {
            /**
             * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.5~1.0，即从一半到最大
             * 如果手指从左到右的滑动（切换到前一个）：1.0~0.5，即从最大到一半
             */

            float mScale = (1 - SCALE_MIN) * effectOffset + SCALE_MIN;

            float mAlpha = effectOffset;            /**
             * x偏移量： 如果手指从右到左的滑动（切换到后一个）：0-720 如果手指从左到右的滑动（切换到前一个）：720-0
             */
            float mTrans = -mViewPager.getWidth() - mViewPager.getPageMargin() + positionOffsetPixels;
            right.setScaleX(mScale);
            right.setScaleY(mScale);
            right.setTranslationX(mTrans);
            right.setAlpha(mAlpha);
//            ViewHelper.setScaleX(right, mScale);
//            ViewHelper.setScaleY(right, mScale);
//            ViewHelper.setTranslationX(right, mTrans);
        }
        if (left != null) {
            left.bringToFront();
        }
    }

    @Override
    public void onPageSelected(int position) {
        curPageIndex = position;
        resetImageViews();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 重置被缩放的view
     */
    private void resetImageViews() {
        if (curPageIndex - 1 >= 0) {
            ImageView pre = (ImageView) mViewPager.findViewWithTag(picArray[curPageIndex - 1]);
            if (pre != null)
                pre.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        if (curPageIndex + 1 < picArray.length) {
            ImageView next = (ImageView) mViewPager.findViewWithTag(picArray[curPageIndex + 1]);
            if (next != null)
                next.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView imageView = (ImageView) mViewPager.findViewWithTag(picArray[curPageIndex]);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                matrix.set(imageView.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = VIEWDRAG;
                if (imageView.getScaleType() == ImageView.ScaleType.MATRIX)
                    mode = PICDRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:  //多点触控
                oldDist = this.spacing(event);
                if (oldDist > 10f) {
                    if (imageView.getScaleType() == ImageView.ScaleType.FIT_CENTER)
                        centerMatrix.set(imageView.getImageMatrix());
                    imageView.setScaleType(ImageView.ScaleType.MATRIX);

                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == VIEWDRAG) {

                    break;
                }
                // 缩放后的图片拖动
                else if (mode == PICDRAG) {
                    if (scrollPic(imageView, event.getX() - start.x, event.getY() - start.y))
                        return true;
                } else if (mode == ZOOM) {  // 图片缩放
                    float newDist = spacing(event);
                    if (newDist > 10) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        //边界检查及还原
                        Rect mapRect = imageView.getDrawable().getBounds();
                        float[] f = new float[9];
                        matrix.getValues(f);
                        float left = f[2];
                        float top = f[5];
                        float width = mapRect.right * f[0];
                        float height = mapRect.bottom * f[4];
                        float right = left + width;
                        float bottom = top + height;
                        if (width < mapRect.right) {
//                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                            imageView.invalidate();
                        }
                    }
                }
                break;
        }

        imageView.setImageMatrix(matrix);

        return detector.onTouchEvent(event);

    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public boolean scrollPic(ImageView imageView, float distanceX, float distanceY) {
        Rect mapRect = imageView.getDrawable().getBounds();
        float[] savedf = new float[9];
        float[] f = new float[9];
        savedMatrix.getValues(savedf);


//        if (left + distanceX > 0 || right + distanceX < PicUtil.getScreenSize().x
//                || top + distanceY > 0 || bottom + distanceY < PicUtil.getScreenSize().y)
//            return false;

        matrix.set(savedMatrix);
        matrix.postTranslate(distanceX, distanceY);
        matrix.getValues(f);

        if (f[0] < savedf[0]) {
            matrix.set(savedMatrix);
            imageView.setImageMatrix(matrix);
        } else {
            float left = f[2];
            float top = f[5];
            float width = mapRect.right * f[0];
            float height = mapRect.bottom * f[4];
            float right = left + width;
            float bottom = top + height;

            int screenHeight = PicUtil.getScreenSize().y;
            if (height > screenHeight) {
                if (top > 0) {
                    matrix.postTranslate(0, -top);
                } else if (bottom < screenHeight) {
                    matrix.postTranslate(0, screenHeight - bottom);
                }
            }
            imageView.setImageMatrix(matrix);

            int screenWidth = PicUtil.getScreenSize().x;
            Log.v("hwLog", "width:" + width + " screenWidth:" + screenWidth);
            if (width > screenWidth) {
                if (left < 0 && right > screenWidth)
                    return true;
                else {
                    if (left >= 0)
                        matrix.postTranslate(-left, 0);
                    else if (right <= screenWidth)
                        matrix.postTranslate(screenWidth - right, 0);
                    return false;
                }
            }
//            Log.v("hwLog", "distanceX:" + distanceX);
//            Log.v("hwLog", "l:" + matrix.toString());
//            Log.v("hwLog", "l:" + left + " t:" + top + " r:" + right + " b:" + bottom);
        }


        return true;
    }

}

class MySimpleGestureListener extends GestureDetector.SimpleOnGestureListener {

    private Activity activity;
    private PicPagerAdapter adapter;

    public MySimpleGestureListener(Activity activity, PicPagerAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.v("hwLog", "onFling");
        if (adapter.mode == adapter.PICDRAG)
            return false;
        if (velocityX > 1000) {
            int pre = adapter.curPageIndex - 1;
            if (pre >= 0) {
                adapter.mViewPager.setCurrentItem(pre);
            }
        } else if (velocityX < -1000) {
            int next = adapter.curPageIndex + 1;
            if (next <= adapter.getCount()) {
                adapter.mViewPager.setCurrentItem(next);
            }
        } else
            return false;
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (adapter.mode == adapter.PICDRAG)
            return false;
        Fragment fragment = activity.getFragmentManager().findFragmentByTag("PicEditFragment");
        if (!(fragment instanceof PicEditFragment)) {
            PicEditFragment picFragment = new PicEditFragment();
            Bundle bundle = new Bundle();
            bundle.putString("path", adapter.picArray[adapter.curPageIndex]);
            picFragment.setArguments(bundle);


            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.main_picture_preview_layout, picFragment, "PicEditFragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return true;
    }

}
