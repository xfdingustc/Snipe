package com.xfdingustc.snipe.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.OnClick;

/**
 * Created by Xiaofei on 2016/8/29.
 */
public class CameraConnectionTestActivity extends BaseActivity {

    @OnClick(R.id.btn_raw_data)
    public void onBtnRawDataItemClicked() {
        Intent intent = new Intent(this, RawDataItemTestActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_camera_connection);
    }
}
