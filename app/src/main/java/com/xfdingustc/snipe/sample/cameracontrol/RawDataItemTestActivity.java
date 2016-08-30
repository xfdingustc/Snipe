package com.xfdingustc.snipe.sample.cameracontrol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.SnipeError;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.control.VdtCamera;
import com.xfdingustc.snipe.control.VdtCameraManager;
import com.xfdingustc.snipe.sample.BaseActivity;
import com.xfdingustc.snipe.sample.R;
import com.xfdingustc.snipe.toolbox.LiveRawDataRequest;
import com.xfdingustc.snipe.vdb.rawdata.RawDataBlock;
import com.xfdingustc.snipe.vdb.rawdata.RawDataItem;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Xiaofei on 2016/8/29.
 */
public class RawDataItemTestActivity extends BaseActivity {


    @BindView(R.id.raw_data_item)
    TextView mRawDataItemUpdate;

    @OnClick(R.id.btn_start)
    public void onBtnStartClicked() {
        if (mVdbRequestQueue != null) {
            LiveRawDataRequest request = new LiveRawDataRequest(RawDataBlock.F_RAW_DATA_GPS +
                RawDataBlock.F_RAW_DATA_ACC + RawDataBlock.F_RAW_DATA_ODB, new
                VdbResponse.Listener<Integer>() {
                    @Override
                    public void onResponse(Integer response) {
                    Logger.t(TAG).d("LiveRawDataResponse: " + response);
                    }
                }, new VdbResponse.ErrorListener() {
                @Override
                public void onErrorResponse(SnipeError error) {
                    Logger.t(TAG).e("LiveRawDataResponse ERROR", error);
                }
            });

            mVdbRequestQueue.add(request);
        }
    }

    @OnClick(R.id.btn_stop)
    public void onBtnStopClicked() {
        if (mVdbRequestQueue != null) {
            LiveRawDataRequest request = new LiveRawDataRequest(0, new
                VdbResponse.Listener<Integer>() {
                    @Override
                    public void onResponse(Integer response) {
                    Logger.t(TAG).d("LiveRawDataResponse: " + response);
                    }
                }, new VdbResponse.ErrorListener() {
                @Override
                public void onErrorResponse(SnipeError error) {
                    Logger.t(TAG).e("LiveRawDataResponse ERROR", error);
                }
            });
            mVdbRequestQueue.add(request);
        }
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
        setContentView(R.layout.activity_raw_data_item);

        mVdbRequestQueue = VdtCameraManager.getManager().getCurrentVdbRequestQueue();
        mVdtCamera.setOnRawDataItemUpdateListener(new VdtCamera.OnRawDataUpdateListener() {
            @Override
            public void OnRawDataUpdate(VdtCamera camera, List<RawDataItem> item) {
                mRawDataItemUpdate.append("Raw data item availabe: " + item.size() + " \n");
            }
        });
    }
}
