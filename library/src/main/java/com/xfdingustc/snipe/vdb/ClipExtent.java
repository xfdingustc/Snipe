package com.xfdingustc.snipe.vdb;

/**
 * Created by Richard on 9/15/15.
 */
public class ClipExtent {
    public final Clip.ID cid;

    public final Clip.ID realCid;

    public final Clip.ID bufferedCid;

    public long minClipStartTimeMs;

    public long maxClipEndTimeMs;

    public long clipStartTimeMs;

    public long clipEndTimeMs;

    public ClipExtent(Clip.ID cid, Clip.ID originalCid, Clip.ID bufferedCid) {
        this.cid = cid;
        this.realCid = originalCid;
        this.bufferedCid = bufferedCid;
    }
}
