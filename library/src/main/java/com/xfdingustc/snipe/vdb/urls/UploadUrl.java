package com.xfdingustc.snipe.vdb.urls;



import com.xfdingustc.snipe.vdb.Clip;

import java.io.Serializable;

/**
 * Created by Richard on 11/19/15.
 */
public class UploadUrl extends VdbUrl implements Serializable{
    public boolean isPlayList;

    public Clip.ID cid;

    public long realTimeMs;

    public int uploadOpt;

    public UploadUrl(boolean isPlayList, Clip.ID cid, long realTimeMs, int lengthMs, int uploadOpt, String url) {
        this.isPlayList = isPlayList;
        this.cid = cid;
        this.realTimeMs = realTimeMs;
        this.lengthMs = lengthMs;
        this.uploadOpt = uploadOpt;
        this.url = url;
    }
}
