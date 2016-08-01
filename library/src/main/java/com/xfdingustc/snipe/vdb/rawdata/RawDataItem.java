package com.xfdingustc.snipe.vdb.rawdata;


/**
 * Created by Xiaofei on 2016/1/6.
 */
public class RawDataItem {
    public static final int DATA_TYPE_NONE = 0;
    public static final int DATA_TYPE_GPS = 1;
    public static final int DATA_TYPE_IIO = 2;
    public static final int DATA_TYPE_OBD = 3;
    public static final int DATA_TYPE_WEATHER = 4;


    private final int mType;
    private long mPtsMs;
    public Object data;

    public RawDataItem(int type, long ptsMs) {
        this.mType = type;
        this.mPtsMs = ptsMs;
    }

    public RawDataItem(RawDataItem other) {
        this.mType = other.mType;
        this.mPtsMs = other.mPtsMs;
        this.data = other.data;
    }


    public int getType() {
        return mType;
    }

    public long getPtsMs() {
        return mPtsMs;
    }

    public void setPtsMs(long pts) {
        mPtsMs = pts;
    }

}
