package com.xfdingustc.snipe.sample;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.xfdingustc.snipe.VdbRequestQueue;
import com.xfdingustc.snipe.control.VdtCamera;
import com.xfdingustc.snipe.control.VdtCameraManager;

import butterknife.ButterKnife;

/**
 * Created by Xiaofei on 2016/8/1.
 */
public class BaseActivity extends AppCompatActivity {

    protected VdtCamera mVdtCamera;
    protected VdbRequestQueue mVdbRequestQueue;

    protected String TAG;

    protected void init() {
        mVdtCamera = VdtCameraManager.getManager().getCurrentCamera();
        mVdbRequestQueue = VdtCameraManager.getManager().getCurrentVdbRequestQueue();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        TAG = this.getClass().getSimpleName();
    }
}
