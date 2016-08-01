package com.xfdingustc.snipe.vdb;

/**
 * Created by Richard on 12/28/15.
 */
public class ClipActionInfo {

    public static final int CLIP_ACTION_CREATED = 1;
    public static final int CLIP_ACTION_CHANGED = 2;
    public static final int CLIP_ACTION_FINISHED = 3;
    public static final int CLIP_ACTION_INSERTED = 4;
    public static final int CLIP_ACTION_MOVED = 5;

    public final Clip clip;
    public final boolean isLive;
    public final int action;

    public MarkLiveInfo markLiveInfo;

    public ClipActionInfo(int action, boolean isLive, Clip clip) {
        this.action = action;
        this.isLive = isLive;
        this.clip = clip;
    }

    @Override
    public String toString() {
        String actionStr;
        switch (action) {
            case CLIP_ACTION_CREATED:
                actionStr = "CLIP_ACTION_CREATED";
                break;
            case CLIP_ACTION_CHANGED:
                actionStr = "CLIP_ACTION_CHANGED";
                break;
            case CLIP_ACTION_FINISHED:
                actionStr = "CLIP_ACTION_FINISHED";
                break;
            case CLIP_ACTION_INSERTED:
                actionStr = "CLIP_ACTION_INSERTED";
                break;
            case CLIP_ACTION_MOVED:
                actionStr = "CLIP_ACTION_MOVED";
                break;
            default:
                actionStr = "Clip_ACTION_UNKNOWN";
        }
        String str = String.format("ClipActionInfo: action[%s], isLive[%b], clip[%d]", actionStr, isLive, clip.cid.subType);
        if (markLiveInfo != null) {
            str = str + String.format("; MarkLive:delay[%d], before[%d], after[%d]",
                    markLiveInfo.delay_ms, markLiveInfo.before_live_ms, markLiveInfo.after_live_ms);
        }
        return str;
    }

    public static class MarkLiveInfo {
        public int flags;
        public int delay_ms;
        public int before_live_ms;
        public int after_live_ms;
    }
}
