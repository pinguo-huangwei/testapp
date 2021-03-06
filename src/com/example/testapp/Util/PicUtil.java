package com.example.testapp.Util;

import android.app.Activity;
import android.graphics.*;
import android.os.Environment;
import com.example.testapp.MApplication;
import com.example.testapp.R;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by huangwei on 14-9-17.
 */
public class PicUtil {
    /**
     * 变红
     *
     * @param bitmap
     * @return
     */
    public static Bitmap redder(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        bitmap = temp;
        int[] pixels = new int[height * width];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i++) {
            int red = Color.red(pixels[i]) + 50;
            if (red > 255)
                red = 255;
            pixels[i] = (red << 16) | pixels[i];
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;

    }

    /**
     * 将bitmap写入文件
     *
     * @param bitmap
     * @param path
     * @return
     */
    public static boolean bitmapToFile(Bitmap bitmap, String path) {
        boolean bool = false;
        if (path != null && bitmap != null) {
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(new File(path));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                fileOutputStream.write(baos.toByteArray());
                fileOutputStream.flush();
                bool = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null)
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return bool;
    }

    /**
     * 判断制定路径的文件是否图片
     *
     * @param path
     * @return
     */
    public static boolean isPic(String path) {
        //Todo ...
        return true;
    }

    public static Bitmap getThumbnailFormByteArray(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        int inSampleSize = 1;
        while (true) {
            if (width > options.outWidth / inSampleSize && height > options.outHeight / inSampleSize) {
                break;
            } else
                ++inSampleSize;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);


    }


//    public static ImageLoader getDefaultImageLoader() {
//        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.gg_album_photo_default)
//                .cacheInMemory(true)
//                .build();
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        MemoryCache memoryCache = null;
//        if (imageLoader.isInited()) {
//            memoryCache = imageLoader.getMemoryCache();
//            imageLoader.destroy();
//        }
//
//        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(MApplication.getCurActivity())
////                .memoryCacheExtraOptions(size, size) // default = device screen dimensions
//                .threadPoolSize(Runtime.getRuntime().availableProcessors()) // default
//                .tasksProcessingOrder(QueueProcessingType.LIFO) // default
//                .memoryCacheSizePercentage(30) // default
//                .imageDecoder(new BaseImageDecoder(false)) // default
//                .defaultDisplayImageOptions(displayImageOptions);
//        if(memoryCache!=null)
//            builder.memoryCache(memoryCache);
//        ImageLoaderConfiguration config = builder.build();
//
//        imageLoader.init(config);
//        return imageLoader;
//    }
//
//    public static ImageLoader getThumbnailImageLoader() {
//        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.gg_album_photo_default)
//                .cacheInMemory(true)
//                .build();
//        int size = DisplayUtil.dip2px(MApplication.getCurActivity(), 80);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        MemoryCache memoryCache = null;
//        if (imageLoader.isInited()) {
//            memoryCache = imageLoader.getMemoryCache();
//            imageLoader.destroy();
//        }
//
//        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(MApplication.getCurActivity())
//                .memoryCacheExtraOptions(size, size) // default = device screen dimensions
//                .threadPoolSize(Runtime.getRuntime().availableProcessors()) // default
//                .tasksProcessingOrder(QueueProcessingType.LIFO) // default
//                .memoryCacheSizePercentage(30) // default
//                .imageDecoder(new BaseImageDecoder(false)) // default
//                .defaultDisplayImageOptions(displayImageOptions);
//        if(memoryCache!=null)
//            builder.memoryCache(memoryCache);
//        ImageLoaderConfiguration config = builder.build();
//
//        imageLoader.init(config);
//        return imageLoader;
//    }

    public static Bitmap getThumbnailFormPath(String path, int width, int height) {
//        StringBuilder key = new StringBuilder(path);
//        key.append('w');
//        key.append(width);
//        key.append('h');
//        key.append(height);
//        key.append('_');
//
//        Bitmap bitmap = MApplication.imageLoader.getMemoryCache().get(key.toString());
//        if(bitmap !=null)
//            return bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int inSampleSize = 1;
        while (true) {
            if (width > options.outWidth / inSampleSize && height > options.outHeight / inSampleSize) {
                break;
            } else
                ++inSampleSize;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
//        MApplication.imageLoader.getMemoryCache().put(key.toString(),bitmap);
        return BitmapFactory.decodeFile(path, options);


    }

//    public static Bitmap getDefaultBitmapFormPath(String path)
//    {
//         Point point = getScreenSize();
//        return getThumbnailFormPath(path,point.x,point.y);
//    }

    public static File getPicDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), "/testpic");
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }

    public static Point getScreenSize() {
        Point point = new Point();
        Activity curActivity = MApplication.getCurActivity();
        if (curActivity != null)
            curActivity.getWindowManager().getDefaultDisplay().getSize(point);
        return point;
    }
}
