package com.xfdingustc.snipe.sample;

import android.content.Intent;
import android.os.Bundle;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @OnClick(R.id.btn_image_test)
    public void onBtnImageTestClicked() {
        Intent intent = new Intent(this, ImageTestActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
