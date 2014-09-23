package com.example.testapp.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import com.example.testapp.Adapter.AlbumAdapter;
import com.example.testapp.MApplication;
import com.example.testapp.R;
import com.example.testapp.Util.PicUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangwei on 14-9-16.
 */
public class AlbumFragment extends BaseFragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    private Activity activity;
    private GridView gridView;

    private AlbumAdapter albumAdapter;

    private List<String> list;

    private ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        imageLoader = MApplication.getImageLoader();
        File dir = PicUtil.getPicDir();
        list = new ArrayList<String>();
        if (dir.exists()) {
            String[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                list.add("file://" + dir.getAbsolutePath() + File.separator + files[i]);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(activity).inflate(R.layout.album_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        gridView = (GridView) view.findViewById(R.id.album_grid);


        albumAdapter = new AlbumAdapter(activity, list);
        gridView.setAdapter(albumAdapter);

        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String path = (String) albumAdapter.getItem(position);
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("PicEditFragment");
        if (!(fragment instanceof PicEditFragment)) {
            //            PicEditFragment picFragment = new PicEditFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("path", path);
//            picFragment.setArguments(bundle);
//
//
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//            transaction.add(R.id.main_picture_preview_layout, picFragment, "PicEditFragment");
//            transaction.addToBackStack(null);
//            transaction.commit();

            PicBrowserFragment picFragment = new PicBrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            picFragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.main_picture_preview_layout, picFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            int first = view.getFirstVisiblePosition();
            int countInScreen = view.getChildCount();
//            preLoading(first + countInScreen, first + countInScreen*3);
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void preLoading(int start, int end) {
        //预读
        for (int i = start; i < end && i < list.size(); i++) {
            imageLoader.loadImage(list.get(i), null);
        }
    }
}
