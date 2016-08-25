package com.xfdingustc.snipe.vdb.rawdata;


import com.xfdingustc.snipe.vdb.Clip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaofei on 2015/9/11.
 */
public class RawDataBlock {

    public static final byte F_RAW_DATA_GPS = (1 << RawDataItem.DATA_TYPE_GPS);
    public static final byte F_RAW_DATA_ACC = (1 << RawDataItem.DATA_TYPE_IIO);
    public static final byte F_RAW_DATA_ODB = (1 << RawDataItem.DATA_TYPE_OBD);

    public final RawDataBlockHeader header;
    public int[] timeOffsetMs;
    public int[] dataSize;
    public byte[] data;

    private int mItemIndex = 0;

    public static class RawDataBlockHeader {
        public final Clip.ID cid;
        public int mClipDate;
        public int mDataType;
        public long mRequestedTimeMs;
        public int mNumItems;
        public int mDataSize;

        public RawDataBlockHeader(Clip.ID cid) {
            this.cid = cid;
        }
    }

    public static class DownloadRawDataBlock {
        public final RawDataBlockHeader header;
        public byte[] ack_data;

        public DownloadRawDataBlock(RawDataBlockHeader header) {
            this.header = header;
        }
    }


    private List<RawDataItem> mRawDataItems = new ArrayList<>();

    public RawDataBlock(RawDataBlockHeader header) {
        this.header = header;
    }

    public List<RawDataItem> getItemList() {
        return mRawDataItems;
    }

    public RawDataItem getRawDataItem(int index) {
        return mRawDataItems.get(index);
    }

    public void addRawDataItem(RawDataItem item) {
        mRawDataItems.add(item);
    }


    public RawDataItem getRawDataItemByTime(long timeMs) {
        for (int i = mItemIndex; i < mRawDataItems.size(); i++) {
            RawDataItem item = mRawDataItems.get(i);
            if (item.getPtsMs() <= timeMs) {
                mItemIndex++;
                return item;
            }
        }
        return null;
    }
}
