package com.example.testapp.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.*;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.example.testapp.Fragment.PicEditFragment;
import com.example.testapp.MApplication;
import com.example.testapp.R;
import com.example.testapp.Util.DisplayUtil;
import com.example.testapp.Util.PicUtil;
import com.example.testapp.Widget.MViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by huangwei on 14-9-19.
 */
public class PicPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, View.OnTouchListener {

    private Activity activity;
    public String[] picArray;
    public MViewPager mViewPager;

    private ImageLoader imageLoader;

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
    public int curPageIndex;

    private GestureDetector detector;

    Matrix mMatrix = new Matrix();

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private Matrix centerMatrix = new Matrix();

    static final int NONE = 0;
    static final int VIEWDRAG = 1;
    static final int ZOOM = 2;
    static final int PICDRAG = 3;
    int mode = NONE;

    private DisplayImageOptions bigPicDisplayOptions;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    //大图加载
    private boolean load_BigPic = true;
    private Bitmap bitmap_BigPic;
    private ImageView imageView_BigPic;
    private int index_BigPic = -1;

    public PicPagerAdapter(Activity activity, final MViewPager mViewPager, String[] picArray, final int initIndex) {
        this.activity = activity;
        this.picArray = picArray;
        this.mViewPager = mViewPager;
        imageLoader = MApplication.getImageLoader();
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
        final ImageView imageView = new ImageView(activity);
//        imageView.setImageBitmap(PicUtil.getDefaultBitmapFormPath(picArray[position]));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setTag(picArray[position]);
        imageLoader.displayImage("file://" + picArray[position], imageView);
        container.addView(imageView);
        setObjectForPosition(imageView, position);
        return imageView;
    }

    /**
     * 尝试加载原图，OutOfMemory则加载小图
     *
     * @param imageView
     * @param path
     */
    private boolean tryLoadBigPic(final ImageView imageView, String path) {

//        if(!load_BigPic)
//            return;

        if (curPageIndex == index_BigPic)
            return false;
        if (imageView == null)
            return false;
        final String uri = "file://" + path;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picArray[curPageIndex], options);
        if (options != null && options.outWidth != 0 && options.outHeight != 0) {
            if (bigPicDisplayOptions == null)
                bigPicDisplayOptions = new DisplayImageOptions.Builder().cacheInMemory(false).build();
            BigPicImageAware bigPicImageAware = new BigPicImageAware(imageView, options.outWidth, options.outHeight);

            Bitmap bitmap = imageLoader.loadImageSync(uri, new ImageSize(options.outWidth, options.outWidth, options.outHeight), bigPicDisplayOptions);
            if (bitmap != null) {
                imageView_BigPic = imageView;
                bitmap_BigPic = bitmap;

                index_BigPic = curPageIndex;
                imageView.setImageBitmap(bitmap);
            }
        }
        return true;

//            imageLoader.displayImage(uri, bigPicImageAware, bigPicDisplayOptions, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    if (failReason.getType() == FailReason.FailType.OUT_OF_MEMORY) {
//                        load_BigPic = false;
//                    }
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    imageView_BigPic = (ImageView) view;
//                    bitmap_BigPic = loadedImage;
//                    index_BigPic = curPageIndex;
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//
//                }
//            });

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
        if (index_BigPic != -1 && curPageIndex != index_BigPic) {
            //释放资源
            if (imageView_BigPic != null) {
                imageView_BigPic.setImageBitmap(null);
                if (index_BigPic >= 0 && index_BigPic < picArray.length)
                    imageLoader.displayImage("file://" + picArray[index_BigPic], imageView_BigPic);
            }
            bitmap_BigPic = null;
            index_BigPic = -1;

        }
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
                    if (imageView.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
                        //尝试加载大图
                        tryLoadBigPic(imageView, picArray[curPageIndex]);
                        centerMatrix.set(imageView.getImageMatrix());
                        matrix.set(imageView.getImageMatrix());

                    }

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
                if (mode == VIEWDRAG)
                {

                    return false;
                }
                // 缩放后的图片拖动
                else if (mode == PICDRAG)
                {
                    if (scrollPic(imageView, event.getX() - start.x, event.getY() - start.y)) {
                        return true;
                    }
                    else
                    {
                        mode = VIEWDRAG;
                        return true;
                    }
                }
               else if (mode == ZOOM) {  // 图片缩放
                    float newDist = spacing(event);
                    if (newDist > 10) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        //边界检查及还原
                        Rect bounds = imageView.getDrawable().getBounds();
                        RectF mapRect = new RectF(bounds);
                        centerMatrix.mapRect(mapRect);
                        float[] f = new float[9];
                        matrix.getValues(f);
                        float left = f[2];
                        float top = f[5];
                        float width = bounds.right * f[0];
                        float height = bounds.bottom * f[4];
                        float right = left + width;
                        float bottom = top + height;
                        if (width < mapRect.right - 10) {

                            imageView.setImageMatrix(centerMatrix);
                            matrix.set(centerMatrix);
                            mode = VIEWDRAG;
                            imageView.setScaleType(ScaleType.FIT_CENTER);
                            return false;
                        }
//                        else if(top > 0)
//                        {
//                            matrix.postTranslate(-top,0);
//                            return false;
//                        }
//                        else if(bottom < PicUtil.getScreenSize().y)
//                        {
//                            matrix.postTranslate(0,PicUtil.getScreenSize().y-bottom);
//                            return false;
//                        }

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

    /**
     * 图片放大后的拖动
     *
     * @param imageView
     * @param distanceX
     * @param distanceY
     * @return true为还在拖动，false为已到边界，可切换为拖动View
     */
    public boolean scrollPic(ImageView imageView, float distanceX, float distanceY) {
        Rect mapRect = imageView.getDrawable().getBounds();
        float[] savedf = new float[9];
        float[] f = new float[9];
        savedMatrix.getValues(savedf);

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
            if (width > screenWidth) {
                if (left < 0 && right > screenWidth)
                    return true;
                else {
                    if (left > 0.0f)
                        matrix.postTranslate(-left, 0);
                    else if (right <= screenWidth)
                        matrix.postTranslate(screenWidth - right, 0);
                    return false;
                }
            }
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
        if (adapter.mode == adapter.PICDRAG || adapter.mode == adapter.VIEWDRAG)
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

class BigPicImageAware extends ImageViewAware {
    private int width;
    private int height;

    public BigPicImageAware(ImageView imageView, int width, int height) {
        super(imageView);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth() {
        if (width <= 0)
            return super.getWidth();
        else
            return width;
    }

    @Override
    public int getHeight() {
        if (height <= 0)
            return super.getHeight();
        else
            return height;
    }
}

