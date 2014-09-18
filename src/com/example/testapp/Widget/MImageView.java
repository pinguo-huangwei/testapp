package com.example.testapp.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by huangwei on 14-9-16.
 */
public class MImageView extends ImageView {
    private Rect bounds;

    public MImageView(Context context) {
        super(context);
        init();
    }

    public MImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {

    }

}
