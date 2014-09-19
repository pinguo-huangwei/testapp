package com.example.testapp.Adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import com.example.testapp.Fragment.PicEditFragment;
import com.example.testapp.R;
import com.example.testapp.Util.DisplayUtil;
import com.example.testapp.Util.PicUtil;
import com.example.testapp.Widget.MViewPager;

/**
 * Created by huangwei on 14-9-19.
 */
public class PicPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private Activity activity;
    public String[] picArray;
    public MViewPager mViewPager;

    private int padding;

    public int curPageIndex;

    private GestureDetector detector;
    private ScaleGestureDetector scaleDetector;

    private float oldDist;
    private float newDist;
    Matrix mMatrix = new Matrix();
    /**
     * 保存缩放前的原始Matrix
     */
    Matrix fitMatrix = new Matrix();

    private ImageView scaledImageView;
    private boolean isScaling;

    public PicPagerAdapter(Activity activity, MViewPager mViewPager, String[] picArray) {
        this.activity = activity;
        this.picArray = picArray;
        this.mViewPager = mViewPager;
        detector = new GestureDetector(activity, new MySimpleGestureListener(activity, this));
        scaleDetector = new ScaleGestureDetector(activity, this);
        mViewPager.setOnPageChangeListener(this);

        padding = DisplayUtil.dip2px(activity, 16);
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
        imageView.setOnTouchListener(this);
        imageView.setTag(picArray[position]);
        container.addView(imageView);
        mViewPager.setObjectForPosition(imageView, position);
        return imageView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        curPageIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(scaledImageView!=null && state == ViewPager.SCROLL_STATE_IDLE)
        {
            scaledImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            scaledImageView = null;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int pointerCount = event.getPointerCount();
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            isScaling = false;
        }
        if (pointerCount == 1 && !isScaling) {
            detector.onTouchEvent(event);

        } else {
            isScaling = true;
            scaleDetector.onTouchEvent(event);
        }
        return true;

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.v("hwLog", "onScale");
        newDist = detector.getCurrentSpan();

        mMatrix.set(scaledImageView.getImageMatrix());
        //缩放比例
        float scale = newDist / oldDist;

//         scale = scale * imageView.getScaleX();
//        imageView.setScaleY(scale);
//        imageView.setScaleX(scale);

//        mMatrix.setScale(scale, scale,detector.getFocusX(),detector.getFocusY());
        mMatrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
        scaledImageView.setImageMatrix(mMatrix);


        oldDist = newDist;
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.v("hwLog", "onScaleBegin");
        oldDist = detector.getCurrentSpan();
        newDist = detector.getCurrentSpan();

        ImageView imageView = (ImageView) mViewPager.findViewWithTag(picArray[curPageIndex]);
        if (imageView == null)
            return false;
        scaledImageView = imageView;
        scaledImageView.setScaleType(ImageView.ScaleType.MATRIX);

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.v("hwLog", "onScaleEnd");
//        if(scaledImageView!=null)
//        {
//            float curScaleX = scaledImageView.getScaleX();
//            float curScaleY = scaledImageView.getScaleY();
//            if(curScaleX > 0.5)
//                return;
//            Animator animatorX = ObjectAnimator.ofFloat(scaledImageView,"scaleX",curScaleX,0.5f);
//            Animator animatorY = ObjectAnimator.ofFloat(scaledImageView,"scaleY",curScaleY,0.5f);
//            animatorX.setDuration(300);
//            animatorY.setDuration(300);
//            AnimatorSet set = new AnimatorSet();
//            set.play(animatorX).with(animatorY);
//            set.start();
//        }
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
        Log.v("hwLog", "fling,velocityX:" + velocityX + " velocityY:" + velocityY);
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
    public boolean onSingleTapConfirmed(MotionEvent e) {
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
