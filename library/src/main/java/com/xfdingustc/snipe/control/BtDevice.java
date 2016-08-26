package com.xfdingustc.snipe.control;


import android.util.Log;

/**
 * Created by Xiaofei on 2016/5/11.
 */
public class BtDevice {
    private static final String TAG = BtDevice.class.getSimpleName();
    public static final int BT_DEVICE_STATE_UNKNOWN = -1;
    public static final int BT_DEVICE_STATE_OFF = 0;
    public static final int BT_DEVICE_STATE_ON = 1;
    public static final int BT_DEVICE_STATE_BUSY = 2;
    public static final int BT_DEVICE_STATE_WAIT = 3;

    public static final int BT_DEVICE_TYPE_OBD = 0;
    public static final int BT_DEVICE_TYPE_REMOTE_CTR = 1;
    public static final int BT_DEVICE_TYPE_OTHER = -1;



    private int mState = BT_DEVICE_STATE_UNKNOWN;
    private String mMac = "";
    private String mName = "";

    private final int mType;
    private final String mTypeName;

    public BtDevice(int type) {
        this.mType = type;
        this.mTypeName = "";
    }

    public BtDevice(int type, String typeName) {
        this.mType = type;
        this.mTypeName = typeName;
    }


    public void setDevState(int state, String mac, String name) {
        if (mState != state || !mMac.equals(mac) || !mName.equals(name)) {
            Log.d(TAG, "setDevState: " + mTypeName + ", " + state + ", " + mac + ", " + name);
            mState = state;
            mMac = mac;
            mName = name;
        }
    }

    public void setState(int state) {
        mState = state;
    }

    public int getType() {
        return mType;
    }

    public int getState() {
        return mState;
    }

    public String getName() {
        return mName;
    }

    public String getMac() {
        return mMac;
    }


}
