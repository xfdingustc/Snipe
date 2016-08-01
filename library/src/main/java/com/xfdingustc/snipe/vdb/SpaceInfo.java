package com.xfdingustc.snipe.vdb;


import com.xfdingustc.snipe.utils.ToStringUtils;

/**
 * Created by Xiaofei on 2016/5/5.
 */
public class SpaceInfo {
    public long total;
    public long used;
    public long marked;

    @Override
    public String toString() {
        return ToStringUtils.getString(this);
    }
}
