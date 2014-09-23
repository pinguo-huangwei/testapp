package com.example.testapp.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.testapp.Adapter.PicPagerAdapter;
import com.example.testapp.MApplication;
import com.example.testapp.R;
import com.example.testapp.Util.DisplayUtil;
import com.example.testapp.Util.PicUtil;
import com.example.testapp.Widget.CutView;
import com.example.testapp.Widget.MImageView;
import com.example.testapp.Widget.MViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by huangwei on 14-9-16.
 */
public class PicBrowserFragment extends BaseFragment{
    private Activity activity;
    private MViewPager mViewPager;

    private ImageLoader imageLoader;

    private String[] picList;

    private PicPagerAdapter adapter;

//    private GestureDetector gestureDetector;

    private int index;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageLoader = MApplication.getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(activity).inflate(R.layout.picbrowser_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//        imageSwitcher = (ImageSwitcher) view.findViewById(R.id.picbrowser_imageSwicher);
//
//        File dir = PicUtil.getPicDir();
//        picList = dir.list();
//
//        if(picList!=null && picList.length>0)
//        {
//            imageSwitcher.setFactory(this);
//            imageSwitcher.setOnTouchListener(this);
//
//
//            gestureDetector = new GestureDetector(activity,new MySimpleGestureListener());
//        }
        mViewPager = (MViewPager) view.findViewById(R.id.picbrowser_viewPager);

        Bundle bundle = getArguments();
        String path = bundle.getString("path");
        if(path.startsWith("file://"))
            path = path.substring("file://".length());
       File dir = PicUtil.getPicDir();
        int index = 0;
        if(dir!=null)
        {
            picList = dir.list();
            if(picList!=null && picList.length > 0)
            {
                StringBuilder stringBuilder = new StringBuilder(dir.getAbsolutePath()+File.separator);
                int start = stringBuilder.length();
                for(int i=0;i<picList.length;i++)
                {

                   picList[i] = stringBuilder.append(picList[i]).toString();
                    if(picList[i].equals(path))
                        index = i;
                   stringBuilder.delete(start,stringBuilder.length());
                }
                Log.v("hwLog","index:"+index);
                adapter = new PicPagerAdapter(activity,mViewPager,picList,index);
                mViewPager.setAdapter(adapter);


            }
        }

    }


//    @Override
//    public View makeView() {
//        ImageView imageView = new ImageView(activity);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        int padding = DisplayUtil.dip2px(activity,10);
//        imageView.setPadding(padding,padding,padding,padding);
//        Point point = PicUtil.getScreenSize(activity);
//        imageView.setImageBitmap(PicUtil.getThumbnailFormPath(PicUtil.getPicDir().getAbsolutePath()+File.separator+picList[0],point.x,point.y));
//        return imageView;
//    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//
//        gestureDetector.onTouchEvent(event);
//        return true;
//    }

//    class MySimpleGestureListener extends GestureDetector.SimpleOnGestureListener{
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.v("hwLog","onScroll,disX:"+distanceX+"  disY:"+distanceY);
//            return true;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.v("hwLog","fling,velocityX:"+velocityX+" velocityY:"+velocityY);
//
//            return true;
//        }
//    }
}
