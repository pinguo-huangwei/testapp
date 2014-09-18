package com.example.testapp.Util;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * Created by huangwei on 14-9-16.
 */
public class MediaScanner {

    private MediaScannerConnection mediaScanConn = null;
    private MediaScannerClient client = null;
    private String filePath = null;
    private String fileType = null;
    private String[] filePaths = null;
    private OnMp3ScanCompleteListener listener;

    public MediaScanner(Context context) {
//创建MusicSannerClient
        if (client == null) {
            client = new MediaScannerClient();
        }
        if (mediaScanConn == null) {
            mediaScanConn = new MediaScannerConnection(context, client);
        }
    }

    public interface OnMp3ScanCompleteListener {
        public void onMp3ScanCompleted(String path, Uri uri);
    }

    public void setOnMp3ScanCompleteListener(OnMp3ScanCompleteListener listener) {
        this.listener = listener;
    }

    class MediaScannerClient implements MediaScannerConnection.MediaScannerConnectionClient {
        public void onMediaScannerConnected() {
            if (filePath != null) {
                mediaScanConn.scanFile(filePath, fileType);
            }
            if (filePaths != null) {
                for (String file : filePaths) {
                    mediaScanConn.scanFile(file, fileType);
                }
                filePath = null;
                fileType = null;
                filePaths = null;
            }
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.v("hwLog", "扫描结束path=" + path + " uri=" + uri);
            if (listener != null)
                listener.onMp3ScanCompleted(path, uri);
            mediaScanConn.disconnect();
        }
    }

    public void scanFile(String filepath, String fileType) {
        this.filePath = filepath;
        this.fileType = fileType;
//连接之后调用MusicSannerClient的onMediaScannerConnected()方法
        mediaScanConn.connect();
    }

    public void scanFile(String[] filepaths, String fileType) {
        this.filePaths = filepaths;
        this.fileType = fileType;
//连接之后调用MusicSannerClient的onMediaScannerConnected()方法
        mediaScanConn.connect();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
