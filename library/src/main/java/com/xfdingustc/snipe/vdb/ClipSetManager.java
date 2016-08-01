package com.xfdingustc.snipe.vdb;



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xiaofei on 2016/3/4.
 */
public class ClipSetManager {
    private Map<Integer, ClipSet> mClipSetMap;

    private ClipSetManager() {
        mClipSetMap = new ConcurrentHashMap<>();
    }


    public static final int CLIP_SET_TYPE_ENHANCE = 0x108;
    public static final int CLIP_SET_TYPE_MANUAL = 0x109;
    public static final int CLIP_SET_TYPE_ALLFOOTAGE = 0x10A;
    public static final int CLIP_SET_TYPE_SHARE = 0x10B;
    public static final int CLIP_SET_TYPE_ENHANCE_EDITING = 0x10C;
    public static final int CLIP_SET_TYPE_BOOKMARK = 0x10D;
    public static final int CLIP_SET_TYPE_TMP = 0x10E;

    private static volatile ClipSetManager CLIP_SET_MANAGER;

    public static ClipSetManager getManager() {
        if (CLIP_SET_MANAGER == null) {
            synchronized (ClipSetManager.class) {
                if (CLIP_SET_MANAGER == null) {
                    CLIP_SET_MANAGER = new ClipSetManager();
                }
            }
        }
        return CLIP_SET_MANAGER;
    }


    public void updateClipSet(int index, ClipSet clipSet) {
        mClipSetMap.put(index, clipSet);
    }


    public ClipSet getClipSet(int index) {
        return mClipSetMap.get(index);
    }
}
