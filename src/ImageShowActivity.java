//import android.app.Activity;
//import android.graphics.Matrix;
//import android.graphics.PointF;
//import android.os.Bundle;
//import android.util.FloatMath;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.ViewFlipper;
//import com.android.wisdom.R;
//import com.android.wisdom.base.widget.NetworkImageView;
//
///**
//* TODO: 图片缩小
//*/
//public class ImageShowActivity extends Activity implements View.OnTouchListener, GestureDetector.OnGestureListener {
//    public static final String EXTRA_KEY_IMAGE_URLs = "key-image-urls";
//
//    //private NetworkImageView imageView;
//    private GestureDetector mGestureDetector;
//    private ViewFlipper mViewFlipper;
//
//    private Matrix matrix=new Matrix();
//    private Matrix savedMatrix=new Matrix();
//
//    static final int NONE = 0;
//    static final int DRAG = 1;
//    static final int ZOOM = 2;
//    int mode = NONE;
//
//    // Remember some things for zooming
//    PointF start = new PointF();
//    PointF mid = new PointF();
//    float oldDist = 1f;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_post_image_show);
//
//        String[] imageUrls = getIntent().getStringArrayExtra(EXTRA_KEY_IMAGE_URLs);
//
//        mViewFlipper = (ViewFlipper) findViewById(R.id.image_view_flipper);
//        mGestureDetector = new GestureDetector(getApplicationContext(), this);
//
//        mViewFlipper.removeAllViews();
//        for (int i = 0; i < imageUrls.length; i++) { // 添加图片源
//            NetworkImageView iv = new NetworkImageView(this);
//            iv.setImageUrl(imageUrls[i]);
//            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            iv.setOnTouchListener(this);
//            iv.setLongClickable(true);
//            iv.setTag(i);
//            mViewFlipper.addView(iv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
//        }
//    }
//
//    @Override
//    public boolean onTouch(View view, MotionEvent event) {
//        ImageView imageView = (ImageView) view;
//
//        /* xml文件中设置图片的scaleType为fitCenter，图片居中显示；
//           当用户触摸时，将scaleType动态设置为matrix，以支持拖动与缩放
//        */
//        if (imageView.getScaleType() != ImageView.ScaleType.MATRIX) {
//            imageView.setScaleType(ImageView.ScaleType.MATRIX);
//        }
//
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                matrix.set(imageView.getImageMatrix());
//                savedMatrix.set(matrix);
//                start.set(event.getX(), event.getY());
//                mode = DRAG;
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:  //多点触控
//                oldDist = this.spacing(event);
//                if (oldDist > 10f) {
//                    savedMatrix.set(matrix);
//                    midPoint(mid, event);
//                    mode = ZOOM;
//                }
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                mode = NONE;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (mode == DRAG) {         // 图片拖动
//                    matrix.set(savedMatrix);
//                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
//                } else if (mode == ZOOM) {  // 图片缩放
//                    float newDist = spacing(event);
//                    if (newDist > 10) {
//                        matrix.set(savedMatrix);
//                        float scale = newDist / oldDist;
//                        matrix.postScale(scale, scale, mid.x, mid.y);
//                    }
//                }
//                break;
//        }
//
//        imageView.setImageMatrix(matrix);
//
//        return mGestureDetector.onTouchEvent(event);
//    }
//
//    private float spacing(MotionEvent event) {
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return FloatMath.sqrt(x * x + y * y);
//    }
//
//    private void midPoint(PointF point, MotionEvent event) {
//        float x = event.getX(0) + event.getX(1);
//        float y = event.getY(0) + event.getY(1);
//        point.set(x / 2, y / 2);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return mGestureDetector.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean onDown(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent motionEvent) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent motionEvent) {
//
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v2) {
//        if (e2.getX() - e1.getX() > 120) { // 从左向右滑动（左进右出）
//            Animation rInAnim = AnimationUtils.loadAnimation(this,
//                    R.anim.push_right_in); // 向右滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
//            Animation rOutAnim = AnimationUtils.loadAnimation(this,
//                    R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）
//
//            mViewFlipper.setInAnimation(rInAnim);
//            mViewFlipper.setOutAnimation(rOutAnim);
//
//            int index = (Integer)mViewFlipper.getCurrentView().getTag();
//            if (index > 0) {
//                mViewFlipper.showPrevious();
//            }
//
//            resetAllImages();
//            return true;
//        } else if (e2.getX() - e1.getX() < -120) { // 从右向左滑动（右进左出）
//            Animation lInAnim = AnimationUtils.loadAnimation(this,
//                    R.anim.push_left_in); // 向左滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
//            Animation lOutAnim = AnimationUtils.loadAnimation(this,
//                    R.anim.push_left_out); // 向左滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）
//
//            mViewFlipper.setInAnimation(lInAnim);
//            mViewFlipper.setOutAnimation(lOutAnim);
//
//            int index = (Integer)mViewFlipper.getCurrentView().getTag();
//            if (index < (mViewFlipper.getChildCount()-1)) {
//                mViewFlipper.showNext();
//            }
//
//            resetAllImages();
//            return true;
//        }
//        return true;
//    }
//
//    // 图片大小与位置复位
//    private void resetAllImages() {
//        for (int i=0; i<mViewFlipper.getChildCount(); i++) {
//            ImageView iv = (ImageView) mViewFlipper.getChildAt(i);
//            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        }
//    }
//}
