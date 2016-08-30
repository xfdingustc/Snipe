package com.xfdingustc.snipe.control.events;


import com.xfdingustc.snipe.control.VdtCamera;

/**
 * Created by Xiaofei on 2016/4/19.
 */
public class CameraStateChangeEvent {
    private final int mWhat;
    private final VdtCamera mVdtCamera;
    private final Object mExtra;

    public static final int CAMERA_STATE_INFO = 0;
    public static final int CAMERA_STATE_REC = 1;
    public static final int CAMERA_STATE_REC_DURATION = 2;
    public static final int CAMERA_STATE_REC_ERROR = 3;
    public static final int CAMERA_STATE_BT_DEVICE_STATUS_CHANGED = 4;


    public CameraStateChangeEvent(int what, VdtCamera camera) {
        this(what, camera, null);
    }

    public CameraStateChangeEvent(int what, VdtCamera camera, Object extra) {
        this.mWhat = what;
        this.mVdtCamera = camera;
        this.mExtra = extra;
    }

    public int getWhat() {
        return mWhat;
    }

    public VdtCamera getCamera() {
        return mVdtCamera;
    }

    public Object getExtra() {
        return mExtra;
    }
}
