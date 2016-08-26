package com.xfdingustc.snipe.control.events;



import com.xfdingustc.snipe.control.VdtCamera;
import com.xfdingustc.snipe.vdb.rawdata.RawDataItem;

import java.util.List;

/**
 * Created by Xiaofei on 2016/4/20.
 */
public class RawDataItemEvent {
    private final VdtCamera mVdtCamera;
    private final List<RawDataItem> mItemList;

    public RawDataItemEvent(VdtCamera camera, List<RawDataItem> item) {
        this.mVdtCamera = camera;
        this.mItemList = item;
    }

    public VdtCamera getCamera() {
        return mVdtCamera;
    }

    public List<RawDataItem> getRawDataItemList() {
        return mItemList;
    }
}
