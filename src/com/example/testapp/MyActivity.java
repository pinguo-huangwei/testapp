package com.example.testapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.example.testapp.Fragment.AlbumFragment;
import com.example.testapp.Util.MediaScanner;
import com.example.testapp.Util.PicUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class MyActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback {


    private SurfaceView surfaceView;
    private Button takePhothBtn;
    private ImageView albumImg;

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int orientation;

    private Bitmap thunbnailBitmap;
    private OrientationEventListener orientationEventListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        surfaceView = (SurfaceView) findViewById(R.id.main_surface);
        takePhothBtn = (Button) findViewById(R.id.main_takephoto);
        albumImg = (ImageView) findViewById(R.id.main_album);

        albumImg.setOnClickListener(this);

        albumImg.setScaleType(ImageView.ScaleType.FIT_XY);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        takePhothBtn.setOnClickListener(this);

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                MyActivity.this.orientation = orientation;
            }
        };

        setAlbumThumbnail();


    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        camera = Camera.open(0);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, int format, int width, int height) {
        try {
            camera.stopPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(parameters);
                        camera.setDisplayOrientation(90);
                        camera.startPreview();
                        camera.cancelAutoFocus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null)
            camera.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_takephoto:
                takePhoto();
                break;
            case R.id.main_album:
                AlbumFragment albumFragment = new AlbumFragment();
                FragmentManager fragmentManager = MyActivity.this.getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.main_picture_preview_layout, albumFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
                break;
        }

    }

    private void takePhoto() {
        takePhothBtn.setEnabled(false);
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) return;
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);
//        int ori = (orientation + 45) / 90 * 90;
//        int rotation = 0;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            rotation = (info.orientation - ori + 360) % 360;
//        } else {  // back-facing camera
//            rotation = (info.orientation + ori) % 360;
//        }
        Log.v("hwLog", "ori:" + orientation);
        int rotation = 0;
        if (orientation > 325 || orientation <= 45) {
            rotation = 90;
        } else if (orientation > 45 && orientation <= 135) {
            rotation = 180;
        } else if (orientation > 135 && orientation < 225) {
            rotation = 270;
        }
        Camera.Parameters mParameters = camera.getParameters();
        mParameters.setRotation(rotation);
        mParameters.setPictureFormat(ImageFormat.JPEG);
        camera.setParameters(mParameters);
        camera.takePicture(null, null, this);
    }



    private void setAlbumThumbnail() {

        albumImg.post(new Runnable() {
              @Override
              public void run() {
                  File dir = new File(Environment.getExternalStorageDirectory() + "/testpic");
                  if (dir.exists()) {
                      String[] list = dir.list();
                      if (list == null || list.length == 0)
                          return;
                      String path = null;
                      StringBuilder temp = new StringBuilder(dir.getAbsolutePath()+File.separator);
                      int start = temp.length();
                      for (int i = 0; i < list.length; i++) {
                          temp.append(list[i]);
                          if (PicUtil.isPic(temp.toString())) {
                              path = temp.toString();
                              break;
                          }
                          temp.delete(start,temp.length());
                      }
                      if (path == null)
                          return;

                      int width = albumImg.getMeasuredWidth();
                      int height = albumImg.getMeasuredHeight();
                      final Bitmap thumbnail = PicUtil.getThumbnailFormPath(path,width,height);
                      albumImg.setImageBitmap(thumbnail);
                  }
              }
          }
        );


    }


    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        camera.startPreview();
        File dir = new File(Environment.getExternalStorageDirectory() + "/testpic");
        if (!dir.exists())
            dir.mkdir();
        String name = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) + ".jpg";
        final File pic = new File(dir, name);
        FileOutputStream fileOutputStream = null;
        try {
            pic.createNewFile();
            fileOutputStream = new FileOutputStream(pic);
            fileOutputStream.write(data, 0, data.length);
            fileOutputStream.flush();

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
        MediaScanner scanner = new MediaScanner(MyActivity.this);
        scanner.scanFile(pic.getAbsolutePath(), "image/*");

        thunbnailBitmap = PicUtil.getThumbnailFormByteArray(data,albumImg.getMeasuredWidth(),albumImg.getMeasuredHeight());
        albumImg.setImageBitmap(thunbnailBitmap);
        takePhothBtn.setEnabled(true);


    }
}
