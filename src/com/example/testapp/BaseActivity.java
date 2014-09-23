package com.example.testapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.example.testapp.MApplication;

/**
 * Created by huangwei on 14-9-19.
 */
public class BaseActivity extends Activity implements FragmentManager.OnBackStackChangedListener {
    private ViewGroup fragmentRootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MApplication.setCurActivity(this);
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public void onBackStackChanged() {
    	Log.v("hwLog", "onBackStackChanged");
        int count = getFragmentManager().getBackStackEntryCount();
        Log.v("hwLog","count:"+getFragmentManager().getBackStackEntryCount());
        if(fragmentRootView==null)
            fragmentRootView = (ViewGroup) findViewById(R.id.main_picture_preview_layout);
        if(fragmentRootView!=null)
        {
            if(count == 0)
            {
                fragmentRootView.setVisibility(View.GONE);
            }
            else if(count > 0 )
            {
                fragmentRootView.setVisibility(View.VISIBLE);
            }
        }

    }
}
