package com.xfdingustc.snipe.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.SnipeError;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.toolbox.ClipSetExRequest;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipSet;

import java.util.ArrayList;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @OnClick(R.id.btn_image_test)
    public void onBtnImageTestClicked() {
        Intent intent = new Intent(this, ImageTestActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_vin_test)
    public void onBtnVinTestClicked() {
        ClipSetExRequest request = new ClipSetExRequest(Clip.TYPE_MARKED, ClipSetExRequest.FLAG_CLIP_EXTRA | ClipSetExRequest.FLAG_CLIP_DESC, 0, new VdbResponse.Listener<ClipSet>() {
            @Override
            public void onResponse(ClipSet response) {
                ArrayList<Clip> clipList = response.getClipList();
                String vin = null;
                for( Clip clip : clipList) {
                    Logger.t(TAG).d("Vin  = " + clip.getVin());
                    if (clip.getVin() != null) {
                        vin = clip.getVin();
                    }
                }
                Toast.makeText(MainActivity.this, "Get Inserted response\tvin = " + vin, Toast.LENGTH_SHORT).show();
            }
        }, new VdbResponse.ErrorListener() {
            @Override
            public void onErrorResponse(SnipeError error) {

            }
        });

        mVdbRequestQueue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVdbRequestQueue = Vdb.getVdbRequestQueue();
    }
}
