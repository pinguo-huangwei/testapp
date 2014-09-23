package com.example.testapp;

import android.app.Activity;
import android.app.Application;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;

import java.lang.ref.WeakReference;

/**
 * Created by huangwei on 14-9-18.
 */
public class MApplication extends Application {

    public static WeakReference<Activity> curActivity;

    private static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void setCurActivity(Activity activity)
    {
        curActivity = new WeakReference<Activity>(activity);
    }

    public static Activity getCurActivity()
    {
        if(curActivity!=null)
            return curActivity.get();
        return null;
    }

    public static ImageLoader getImageLoader()
    {
        if(imageLoader==null)
            imageLoader = initImageLoader();
        return imageLoader;
    }
    private static ImageLoader initImageLoader() {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.gg_album_photo_default)
                .cacheInMemory(true)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MApplication.getCurActivity())
//                .memoryCacheExtraOptions(size, size) // default = device screen dimensions
                .threadPoolSize(Runtime.getRuntime().availableProcessors()) // default
                .tasksProcessingOrder(QueueProcessingType.LIFO) // default
                .memoryCacheSizePercentage(30) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(displayImageOptions)
                .build();

        imageLoader.init(config);
        return imageLoader;
    }

}
