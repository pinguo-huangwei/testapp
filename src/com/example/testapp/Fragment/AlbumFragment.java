package com.example.testapp.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.example.testapp.Adapter.AlbumAdapter;
import com.example.testapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangwei on 14-9-16.
 */
public class AlbumFragment extends Fragment {
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

        File dir = new File(Environment.getExternalStorageDirectory() + "/testpic");

        if (dir.exists()) {
            List<String> list = new ArrayList<String>();
            String[] files = dir.list();
            for(int i=0;i<files.length;i++)
            {
                list.add("file://"+dir.getAbsolutePath()+File.separator+files[i]);
            }
            albumAdapter = new AlbumAdapter(activity, list);
            gridView.setAdapter(albumAdapter);
        }

    }
}
