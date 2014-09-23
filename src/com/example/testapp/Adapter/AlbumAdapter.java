package com.example.testapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.example.testapp.MApplication;
import com.example.testapp.MyActivity;
import com.example.testapp.R;
import com.example.testapp.Util.DisplayUtil;
import com.example.testapp.Util.PicUtil;
import com.example.testapp.Widget.MImageView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by huangwei on 14-9-18.
 */
public class AlbumAdapter extends BaseAdapter{

    private Context context;
    private List<String> imgList;
    private ImageLoader imageLoader;
    private int thumbSize;

    public AlbumAdapter(Context context,List<String> picList)
    {
        this.context = context;
        imgList = picList;
        this.imageLoader = MApplication.getImageLoader();
    }
    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return imgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.album_item,null);
            vh = new ViewHolder();
            vh.imageView = (ImageView) convertView.findViewById(R.id.album_item_img);
            convertView.setTag(vh);
        }else
           vh = (ViewHolder) convertView.getTag();
        imageLoader.displayImage(imgList.get(position),new ImageViewAware(vh.imageView));
        return convertView;
    }
}

class ViewHolder
{
    ImageView imageView;
}
