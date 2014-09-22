package com.example.testapp.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.example.testapp.Adapter.AlbumAdapter;
import com.example.testapp.R;
import com.example.testapp.Util.PicUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangwei on 14-9-16.
 */
public class AlbumFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Activity activity;
    private GridView gridView;

    private AlbumAdapter albumAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(activity).inflate(R.layout.album_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        gridView = (GridView) view.findViewById(R.id.album_grid);

        File dir = PicUtil.getPicDir();

        if (dir.exists()) {
            List<String> list = new ArrayList<String>();
            String[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                list.add("file://" + dir.getAbsolutePath() + File.separator + files[i]);
            }
            albumAdapter = new AlbumAdapter(activity, list);
            gridView.setAdapter(albumAdapter);
        }

        gridView.setOnItemClickListener(this);
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
}
