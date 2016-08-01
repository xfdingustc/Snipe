package com.xfdingustc.snipe.vdb;

import java.util.ArrayList;

public class ClipSet {
    private final int mClipType;
    public static final long U32_MASK = 0x0FFFFFFFFL;

    private Clip.ID liveClipId;

    private ArrayList<Clip> mClipList = new ArrayList<>();


    public ClipSet(int type) {
        this.mClipType = type;
        liveClipId = null;

    }

    public int getType() {
        return mClipType;
    }

    public int getCount() {
        return mClipList.size();
    }


    public void set(ClipSet other) {
        this.liveClipId = other.liveClipId;
        this.mClipList = other.mClipList;
    }

    @Override
    public String toString() {
        String ret = "";
        for (Clip clip : mClipList) {
            ret += clip.toString();
            ret += "\n";
        }

        return ret;
    }

    public void clear() {
        mClipList.clear();
    }


    public Clip getClip(int index) {
        return index < 0 || index >= mClipList.size() ? null : mClipList.get(index);
    }

    public ArrayList<Clip> getClipList() {
        return mClipList;
    }

    public void addClip(Clip clip) {
        mClipList.add(clip);
    }

    public void setLiveClipId(Clip.ID liveClipId) {
        this.liveClipId = liveClipId;
    }


    public void insertClipById(Clip clip) {
        long clipId = (long) clip.cid.subType & U32_MASK;
        int i = 0;
        for (Clip tmp : mClipList) {
            if (clipId < ((long) tmp.cid.subType & U32_MASK))
                break;
            i++;
        }
        mClipList.add(i, clip);
    }


    public void insertClipByIndex(Clip clip) {
        int i;
        for (i = 0; i < mClipList.size(); i++) {
            if (clip.index <= i)
                break;
        }
        mClipList.add(i, clip);

        for (; i < mClipList.size(); i++) {
            clip = mClipList.get(i);
            clip.index = i;
        }
    }


    public boolean removeClip(Clip.ID cid) {
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);
            if (clip.cid.equals(cid)) {
                mClipList.remove(i);
                return true;
            }
        }
        return false;
    }

    public void remove(int position) {
        mClipList.remove(position);
    }


    public boolean clipChanged(Clip clip, boolean isLive, boolean bFinished) {
        int i = 0;
        for (i = 0; i < mClipList.size(); i++) {
            Clip tmp = mClipList.get(i);
            if (tmp.cid.equals(clip.cid)) {
                mClipList.set(i, clip);
                if (isLive) {
                    liveClipId = bFinished ? null : clip.cid;
                }
                return true;
            }
        }
        return false;
    }


    public int findClipIndex(Clip.ID cid) {
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);
            if (clip.cid.equals(cid))
                return i;
        }
        return -1;
    }


    public Clip findClip(Clip.ID cid) {
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);
            if (clip.cid.equals(cid))
                return clip;
        }
        return null;
    }


    public boolean isLiveClip(Clip clip) {
        return liveClipId != null && liveClipId.equals(clip.cid);
    }


    public boolean moveClip(Clip.ID cid, int clipIndex) {
        if (clipIndex < 0 || clipIndex >= mClipList.size())
            return false;

        int index = findClipIndex(cid);
        if (index < clipIndex) {
            Clip clip = mClipList.get(index);
            for (int i = index; i < clipIndex; i++) {
                Clip tmp = mClipList.get(i + 1);
                tmp.index = i;
                mClipList.set(i, tmp);
            }
            clip.index = clipIndex;
            mClipList.set(clipIndex, clip);
            return true;
        }

        if (index > clipIndex) {
            Clip clip = mClipList.get(index);
            for (int i = index; i > clipIndex; i--) {
                Clip tmp = mClipList.get(i - 1);
                tmp.index = i;
                mClipList.set(i, tmp);
            }
            clip.index = clipIndex;
            mClipList.set(clipIndex, clip);
            return true;
        }

        return false;
    }


    public int getTotalLengthMs() {
        int totalLengthMs = 0;
        for (Clip clip : mClipList) {
            totalLengthMs += clip.getDurationMs();
        }
        return totalLengthMs;
    }

    public int getTotalSelectedLengthMs() {
        int total = 0;
        for (Clip clip : mClipList) {
            total += clip.editInfo.getSelectedLength();
        }
        return total;
    }

    public int getClipIndexByTimePosition(int position) {
        int total = 0;
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);

            if (position < total + clip.editInfo.getSelectedLength()) {

                return i;
            }

            total += clip.editInfo.getSelectedLength();
        }

        return -1;
    }

    public ClipPos findClipPosByTimePosition(int position) {
        int total = 0;
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);

            if (position < total + clip.editInfo.getSelectedLength()) {
                ClipPos clipPos = new ClipPos(clip, position - total + clip
                    .editInfo.selectedStartValue, ClipPos.TYPE_POSTER, false);
                return clipPos;
            }

            total += clip.editInfo.getSelectedLength();
        }

        return null;
    }


    public ClipPos getClipPosByClipSetPos(ClipSetPos clipSetPos) {
        if (clipSetPos == null) {
            return null;
        }
        Clip clip = mClipList.get(clipSetPos.getClipIndex());
        return new ClipPos(clip, clipSetPos.getClipTimeMs());
    }


    public ClipSetPos getClipSetPosByTimeOffset(long timeOffset) {
        int total = 0;
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);

            if (timeOffset < total + clip.editInfo.getSelectedLength()) {
                return new ClipSetPos(i, clip.editInfo.selectedStartValue + timeOffset - total);

            }

            total += clip.editInfo.getSelectedLength();
        }

        return null;
    }

    public long getTimeOffsetByClipSetPos(ClipSetPos clipSetPos) {
        if (clipSetPos == null) {
            return 0;
        }
        long total = 0;
        for (int i = 0; i < mClipList.size(); i++) {
            Clip clip = mClipList.get(i);

            if (i == clipSetPos.getClipIndex()) {
                total += clipSetPos.getClipTimeMs() - clip.editInfo.selectedStartValue;
                break;
            }

            total += clip.editInfo.getSelectedLength();
        }

        return total;
    }


}
