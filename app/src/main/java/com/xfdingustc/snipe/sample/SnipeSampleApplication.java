package com.xfdingustc.snipe.sample;

import android.app.Application;

/**
 * Created by Xiaofei on 2016/8/1.
 */
public class SnipeSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Vdb.getVdbRequestQueue();
            }
        }).start();
    }
}
