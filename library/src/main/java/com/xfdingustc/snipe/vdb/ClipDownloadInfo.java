package com.xfdingustc.snipe.vdb;

import java.io.Serializable;

/**
 * Created by Xiaofei on 2015/8/28.
 */
public class ClipDownloadInfo {

    public static class StreamDownloadInfo implements Serializable {
        public int clipDate;
        public long clipTimeMs;
        public int lengthMs;
        public long size;
        public String url;
    }

    public final Clip.ID cid;
    public int opt;
    public final StreamDownloadInfo main = new StreamDownloadInfo();
    public final StreamDownloadInfo sub = new StreamDownloadInfo();
    public byte[] posterData;

    public ClipDownloadInfo(Clip.ID cid) {
        this.cid = cid;
    }
}
