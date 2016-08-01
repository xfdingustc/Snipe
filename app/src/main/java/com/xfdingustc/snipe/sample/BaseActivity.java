package com.xfdingustc.snipe.sample;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.xfdingustc.snipe.VdbRequestQueue;

import butterknife.ButterKnife;

/**
 * Created by Xiaofei on 2016/8/1.
 */
public class BaseActivity extends AppCompatActivity {

    protected VdbRequestQueue mVdbRequestQueue;

    protected String TAG;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        TAG = this.getClass().getSimpleName();
    }
}
