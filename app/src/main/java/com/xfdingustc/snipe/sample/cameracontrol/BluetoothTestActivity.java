package com.xfdingustc.snipe.sample.cameracontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.xfdingustc.snipe.control.BtDevice;
import com.xfdingustc.snipe.sample.BaseActivity;
import com.xfdingustc.snipe.sample.R;

import butterknife.BindView;

/**
 * Created by Xiaofei on 2016/8/30.
 */
public class BluetoothTestActivity extends BaseActivity {

    @BindView(R.id.remote_status)
    TextView mRemoteStatus;

    @BindView(R.id.tv_bt_status)
    TextView mBtStatus;

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
        setContentView(R.layout.activity_bluetooth);

        BtDevice device = mVdtCamera.getRemoteCtrlDevice();
        Log.d(TAG, "remote status: " + device.getState());
        mRemoteStatus.setText(device.getState() == BtDevice.BT_DEVICE_STATE_ON ? "ON" : "OFF");



    }
}
