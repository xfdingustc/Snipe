package com.xfdingustc.snipe.control.events;

/**
 * Created by Xiaofei on 2016/5/10.
 */
public class NetworkEvent {
    public static final int NETWORK_EVENT_WHAT_CONNECTED = 0;
    public static final int NETWORK_EVENT_WHAT_ADDED = 1;

    private final int mWhat;
    private final Object mExtra1;
    private final Object mExtra2;

    public NetworkEvent(int what) {
        this(what, null, null);
    }

    public NetworkEvent(int what, Object extra) {
        this(what, extra, null);
    }

    public NetworkEvent(int what, Object extra1, Object extra2) {
        this.mWhat = what;
        this.mExtra1 = extra1;
        this.mExtra2 = extra2;
    }

    public int getWhat() {
        return mWhat;
    }

    public Object getExtra1() {
        return mExtra1;
    }

    public Object getExtra2() {
        return mExtra2;
    }
}
