package com.xfdingustc.snipe.vdb;

/**
 * Created by liushuwei on 16/6/28.
 */
public class VdbReadyInfo {
    //True means that SD card is ready, otherwise SD is missing
    private boolean mIsReady;

    public VdbReadyInfo(boolean mIsReady) {
        this.mIsReady = mIsReady;
    }

    public boolean getIsReady() {
        return mIsReady;
    }

}
