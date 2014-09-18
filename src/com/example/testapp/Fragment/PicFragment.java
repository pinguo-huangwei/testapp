package com.example.testapp.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.testapp.Util.PicUtil;
import com.example.testapp.R;
import com.example.testapp.Widget.CutView;
import com.example.testapp.Widget.MImageView;

/**
 * Created by huangwei on 14-9-16.
 */
public class PicFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private MImageView imageView;
    private CutView cutView;

    private Button cutBtn;
    private Button applyBtn;
    private Button lighterBtn;

    private float sX, sY;
    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(activity).inflate(R.layout.pic_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        imageView = (MImageView) view.findViewById(R.id.pic_imageView);
        cutBtn = (Button) view.findViewById(R.id.pic_cut);
        applyBtn = (Button) view.findViewById(R.id.pic_apply);
        cutView = (CutView) view.findViewById(R.id.pic_cutview);
        lighterBtn = (Button) view.findViewById(R.id.pic_redder);

        cutView.setVisibility(View.GONE);

        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        cutBtn.setOnClickListener(this);
        applyBtn.setOnClickListener(this);
        lighterBtn.setOnClickListener(this);

        String path = getArguments().getString("path");
        if (path != null) {
//            Toast.makeText(activity, "存储于:" + path, Toast.LENGTH_LONG).show();
            bitmap = BitmapFactory.decodeFile(path);
            setBitmap(bitmap);
        }


    }

    public void setBitmap(final Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.post(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    //获得ImageView中Image的真实宽高，
                    int dw = imageView.getDrawable().getBounds().width();
                    int dh = imageView.getDrawable().getBounds().height();

                    //获得ImageView中Image的变换矩阵
                    Matrix m = imageView.getImageMatrix();
                    float[] values = new float[10];
                    m.getValues(values);

                    //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
                    sX = values[0];
                    sY = values[4];

                    //计算Image在屏幕上实际绘制的宽高
                    int cw = (int) (dw * sX);
                    int ch = (int) (dh * sY);

                    int width = imageView.getWidth();
                    int height = imageView.getHeight();

                    RectF bounds = new RectF();
                    int margin = ((FrameLayout.LayoutParams) imageView.getLayoutParams()).leftMargin;
                    bounds.left = (width - cw) / 2f + margin;
                    bounds.right = width - bounds.left + margin;
                    bounds.top = (height - ch) / 2f + margin;
                    bounds.bottom = height - bounds.top + margin;
                    cutView.setBounds(bounds);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pic_cut:
                startCut();
                break;
            case R.id.pic_apply:
                save();
                break;
            case R.id.pic_redder:
                bitmap = PicUtil.redder(bitmap);
                setBitmap(bitmap);
                break;
            default:
                break;
        }
    }

    private void startCut() {
        cutView.setVisibility(View.VISIBLE);
        imageView.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                //获得ImageView中Image的真实宽高，
                int dw = imageView.getDrawable().getBounds().width();
                int dh = imageView.getDrawable().getBounds().height();

                //获得ImageView中Image的变换矩阵
                Matrix m = imageView.getImageMatrix();
                float[] values = new float[10];
                m.getValues(values);

                //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
                sX = values[0];
                sY = values[4];

                //计算Image在屏幕上实际绘制的宽高
                int cw = (int) (dw * sX);
                int ch = (int) (dh * sY);

                int width = imageView.getWidth();
                int height = imageView.getHeight();

                RectF bounds = new RectF();
                int margin = ((FrameLayout.LayoutParams) imageView.getLayoutParams()).leftMargin;
                bounds.left = (width - cw) / 2f + margin;
                bounds.right = width - bounds.left + margin;
                bounds.top = (height - ch) / 2f + margin;
                bounds.bottom = height - bounds.top + margin;
                cutView.setBounds(bounds);
            }
        });
    }

    private void save() {

        RectF cutRect = cutView.getCutBounds();
        cutRect.left /= sX;
        cutRect.right /= sX;
        cutRect.top /= sY;
        cutRect.bottom /= sY;
        Log.v("hwLog", "cutRect:" + cutRect.toShortString());
        bitmap = Bitmap.createBitmap(bitmap, (int) cutRect.left, (int) cutRect.top, (int) (cutRect.right - cutRect.left), (int) (cutRect.bottom - cutRect.top));
        setBitmap(bitmap);
        cutView.setVisibility(View.GONE);

    }
}
