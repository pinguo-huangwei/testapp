package com.example.testapp.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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
}
