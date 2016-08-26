package com.xfdingustc.snipe.control.entities;


import com.xfdingustc.snipe.utils.ToStringUtils;

/**
 * Created by Xiaofei on 2016/3/21.
 */
public class NetworkItemBean {
    public String ssid;
    public String bssid;
    public String flags;
    public int frequency;
    public int signalLevel;
//    public boolean added;



    public static final int CONNECT_STATUS_NONE = 0;
    public static final int CONNECT_STATUS_SAVED = 1;
    public static final int CONNECT_STATUS_AUTHENTICATION = 2;
    public static final int CONNECT_STATUS_AUTHENTICATION_PROBLEM = 3;


    public int status = CONNECT_STATUS_NONE;


    @Override
    public String toString() {
        return ToStringUtils.getString(this);
    }
}
