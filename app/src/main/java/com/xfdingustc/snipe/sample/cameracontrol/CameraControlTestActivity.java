package com.xfdingustc.snipe.sample.cameracontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xfdingustc.snipe.sample.BaseActivity;
import com.xfdingustc.snipe.sample.R;

import butterknife.OnClick;

/**
 * Created by Xiaofei on 2016/8/29.
 */
public class CameraControlTestActivity extends BaseActivity {

    @OnClick(R.id.btn_raw_data)
    public void onBtnRawDataItemClicked() {
        Intent intent = new Intent(this, RawDataItemTestActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_bt)
    public void onBtnBluetoothClicked() {
        Intent intent = new Intent(this, BluetoothTestActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_audio)
    public void onBtnAudioClicked() {
        Intent intent = new Intent(this, AudioTestActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        super.init();
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_camera_connection);
    }
}
