package com.xfdingustc.snipe.sample.cameracontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xfdingustc.rxutils.library.RxBus;
import com.xfdingustc.rxutils.library.SimpleSubscribe;
import com.xfdingustc.snipe.control.BtDevice;
import com.xfdingustc.snipe.control.events.CameraStateChangeEvent;
import com.xfdingustc.snipe.sample.BaseActivity;
import com.xfdingustc.snipe.sample.R;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Xiaofei on 2016/8/30.
 */
public class BluetoothTestActivity extends BaseActivity {



    private Subscription mBtDeviceStateChangeSubscription;

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

    @Override
    protected void onStart() {
        super.onStart();
        mBtDeviceStateChangeSubscription = mRxBus.toObserverable(CameraStateChangeEvent.class)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(new SimpleSubscribe<CameraStateChangeEvent>() {

                @Override
                public void onNext(CameraStateChangeEvent cameraStateChangeEvent) {
                    Logger.t(TAG).d("on camera state changed: " + cameraStateChangeEvent.getWhat());
                }
            });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (!mBtDeviceStateChangeSubscription.isUnsubscribed()) {
            mBtDeviceStateChangeSubscription.unsubscribe();
        }
    }
}
