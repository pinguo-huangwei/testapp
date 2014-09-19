package com.example.testapp;

import android.app.Activity;
import android.os.Bundle;
import com.example.testapp.MApplication;

/**
 * Created by huangwei on 14-9-19.
 */
public class BaseActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MApplication.setCurActivity(this);
    }
}
