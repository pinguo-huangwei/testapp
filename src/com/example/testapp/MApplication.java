package com.example.testapp;

import android.app.Application;
import android.content.Context;
import com.example.testapp.Util.DisplayUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;

import java.io.File;

/**
 * Created by huangwei on 14-9-18.
 */
public class MApplication extends Application {
    public static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
    }


    private void initImageLoader()
    {
        File cacheDir = getExternalCacheDir();
        int size = DisplayUtil.dip2px(this, 80);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .memoryCacheExtraOptions(size, size) // default = device screen dimensions
                .threadPoolSize(Runtime.getRuntime().availableProcessors()) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSizePercentage(30) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }
}
