package com.xfdingustc.snipe.vdb;

/**
 * Created by Xiaofei on 2015/8/27.
 */
public class ClipSegment {
    private Clip mClip;
    private long mStartTimeMs;
    private long mEndTimeMs;

    public ClipSegment(Clip clip) {
        this.mClip = clip;
        this.mStartTimeMs = clip.getStartTimeMs();
        this.mEndTimeMs = clip.getStartTimeMs() + clip.getDurationMs();
    }

    public ClipSegment(Clip clip, long startTimeMs, long endTimeMs) {
        this.mClip = clip;
        this.mStartTimeMs = startTimeMs;
        this.mEndTimeMs = endTimeMs;
    }

    public Clip getClip() {
        return mClip;
    }

    public long getStartTimeMs() {
        return mStartTimeMs;
    }

    public int getDurationMs() {
        return (int)(mEndTimeMs - mStartTimeMs);
    }

    public long getEndTimeMs() {
        return mEndTimeMs;
    }

    public void setClip(Clip clip) {
        mClip = clip;
    }

    public void setStartTime(long startTimeMs) {
        mStartTimeMs = startTimeMs;
    }

    public void setEndTime(long endTimeMs) {
        mEndTimeMs = endTimeMs;
    }


    @Override
    public String toString() {
        return "Clip: " + mClip.toString() + " start time: " + mStartTimeMs + " end time: " + mEndTimeMs;
    }
}
