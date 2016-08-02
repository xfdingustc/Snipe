package com.xfdingustc.snipe.vdb;

// position in a clip
public class ClipPos {

    public static final int TYPE_POSTER = 0; // for clip poster
    public static final int TYPE_SLIDE = 1; // for clip slides
    public static final int TYPE_ANIMATION = 2; // for clip animation
    public static final int TYPE_PREVIEW = 3; // for fast preview in video list view

    public static final int F_IS_LAST = 0x80; // last point in the clip

    public String vdbId;
    public final Clip.ID cid;

    private final boolean mbIsLast;
    private final int mDate;
    private final int mType; // TYPE_POSTER etc.
    private long mClipTimeMs; // absolute time in the clip

    private long mRealTimeMs; // real time returned by server
    private int mDuration; // returned by server

    private boolean mIgnorable = false;

    public ClipPos(Clip clip) {
        this(clip, clip.getStartTimeMs(), ClipPos.TYPE_POSTER, false);
    }

    public ClipPos(Clip clip, long clipTimeMs) {
        this(clip, clipTimeMs, ClipPos.TYPE_POSTER, false);
    }

    public ClipPos(Clip clip, long clipTimeMs, int type, boolean bIsLast) {
        this(clip.getVdbId(), clip.cid, clip.getClipDate(), clipTimeMs, type, bIsLast);
    }

    public ClipPos(String vdbId, Clip.ID cid, long date, long timeMs, int type, boolean bIsLast) {
        this.vdbId = vdbId;
        this.cid = cid;
        this.mbIsLast = bIsLast;
        this.mDate = (int)(date / 1000);
        this.mType = type;
        this.mClipTimeMs = timeMs;
        this.mRealTimeMs = timeMs; // fixed later by server
        this.mDuration = 0; // fixed later by server
    }

    public final void setVdbId(String vdbId) {
        this.vdbId = vdbId;
    }

    public final int getType() {
        return mType;
    }

    public final boolean isDiscardable() {
        return mType == TYPE_ANIMATION || mType == TYPE_PREVIEW;
    }

    public final boolean isLast() {
        return mbIsLast;
    }

    public Clip.ID getClipId() {
        return cid;
    }

    public final int getClipDate() {
        return mDate;
    }

    public long getClipTimeMs() {
        return mClipTimeMs;
    }

    public void setClipTimeMs(long clipTimeMs) {
        mClipTimeMs = clipTimeMs;
    }

    public final long getRealTimeMs() {
        return mRealTimeMs;
    }

    public final int getDuration() {
        return mDuration;
    }

    // set value returned by server
    public void setRealTimeMs(long realTimeMs) {
        mRealTimeMs = realTimeMs;
    }

    // set value returned by server
    public void setDuration(int duration) {
        mDuration = duration;
    }

    public boolean getIgnorable() {
        return mIgnorable;
    }

    public void setIgnorable(boolean ignorable) {
        mIgnorable = ignorable;
    }

}
