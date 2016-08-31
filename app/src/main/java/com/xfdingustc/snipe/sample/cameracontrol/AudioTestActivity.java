package com.xfdingustc.snipe.sample.cameracontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.xfdingustc.rxutils.library.SimpleSubscribe;
import com.xfdingustc.snipe.control.events.CameraStateChangeEvent;
import com.xfdingustc.snipe.sample.BaseActivity;
import com.xfdingustc.snipe.sample.R;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Xiaofei on 2016/8/31.
 */
public class AudioTestActivity extends BaseActivity {

    private Subscription mCameraStateChangedSubscription;

    @BindView(R.id.switch_microphone)
    Switch mSwitchMicrophone;

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
        setContentView(R.layout.activity_microphone);
        mSwitchMicrophone.setChecked(mVdtCamera.isMicEnabled());
        mSwitchMicrophone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mVdtCamera.setMicEnabled(b);
            }
        });

        mCameraStateChangedSubscription = mRxBus.toObserverable(CameraStateChangeEvent.class)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SimpleSubscribe<CameraStateChangeEvent>() {
                @Override
                public void onNext(CameraStateChangeEvent cameraStateChangeEvent) {
                    if (cameraStateChangeEvent.getWhat() == CameraStateChangeEvent.CAMEAR_STATE_MICROPHONE_STATUS_CHANGED) {
                        Toast.makeText(AudioTestActivity.this, "Microphone status changed to: " + mVdtCamera.isMicEnabled(), Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCameraStateChangedSubscription.isUnsubscribed()) {
            mCameraStateChangedSubscription.unsubscribe();
        }
    }
}
