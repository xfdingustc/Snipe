package com.xfdingustc.snipe.control.events;

/**
 * Created by Xiaofei on 2016/5/12.
 */
public class BluetoothEvent {
    public static final int BT_SCAN_COMPLETE = 0;
    public static final int BT_DEVICE_BIND_FINISHED = 1;
    public static final int BT_DEVICE_UNBIND_FINISHED = 2;
    public static final int BT_DEVICE_STATUS_CHANGED = 4;

    private final Object mExtra;
    private final int mWhat;

    public BluetoothEvent(int what) {
        this(what, null);
    }

    public BluetoothEvent(int what, Object extra) {
        this.mWhat = what;
        this.mExtra = extra;
    }

    public int getWhat() {
        return mWhat;
    }

    public Object getExtra() {
        return mExtra;
    }


}
