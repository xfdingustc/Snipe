package com.xfdingustc.snipe.toolbox;

import android.os.Bundle;

import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.rawdata.GpsData;
import com.xfdingustc.snipe.vdb.rawdata.IioData;
import com.xfdingustc.snipe.vdb.rawdata.ObdData;
import com.xfdingustc.snipe.vdb.rawdata.RawDataBlock;
import com.xfdingustc.snipe.vdb.rawdata.RawDataItem;


/**
 * Created by Xiaofei on 2015/9/11.
 */
public class RawDataBlockRequest extends VdbRequest<RawDataBlock> {
    private static final String TAG = RawDataBlockRequest.class.getSimpleName();
    private final Clip.ID mCid;
    private final int mDataType;
    private final long mClipTimeMs;
    private final int mDuration;

    public static final String PARAM_CLIP_TIME = "clip.time.ms";
    public static final String PARAM_CLIP_LENGTH = "clip.length.ms";
    public static final String PARAM_DATA_TYPE = "raw.data.type";

    public RawDataBlockRequest(Clip.ID cid, Bundle params,
                               VdbResponse.Listener<RawDataBlock> listener,
                               VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        this.mCid = cid;
        this.mDataType = params.getInt(PARAM_DATA_TYPE, RawDataItem.DATA_TYPE_NONE);
        mClipTimeMs = params.getLong(PARAM_CLIP_TIME, 0);
        mDuration = params.getInt(PARAM_CLIP_LENGTH, 0);
    }

    @Override
    protected VdbCommand createVdbCommand() {
        mVdbCommand = VdbCommand.Factory.createCmdGetRawDataBlock(mCid, true, mDataType, mClipTimeMs, mDuration);
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<RawDataBlock> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
//            Logger.t(TAG).d("response: " + response.getRetCode());
            return null;
        }

        int clipType = response.readi32();
        int clipId = response.readi32();
        Clip.ID cid = new Clip.ID(clipType, clipId, null);
        RawDataBlock.RawDataBlockHeader header = new RawDataBlock.RawDataBlockHeader(cid);
        header.mClipDate = response.readi32();
        header.mDataType = response.readi32();
        header.mRequestedTimeMs = response.readi64();
        header.mNumItems = response.readi32();
        header.mDataSize = response.readi32();


        RawDataBlock block = new RawDataBlock(header);

        int numItems = block.header.mNumItems;
        block.timeOffsetMs = new int[numItems];
        block.dataSize = new int[numItems];

        for (int i = 0; i < numItems; i++) {
            block.timeOffsetMs[i] = response.readi32();
            block.dataSize[i] = response.readi32();
        }


        for (int i = 0; i < numItems; i++) {
            RawDataItem item = new RawDataItem(header.mDataType, block.timeOffsetMs[i] + header.mRequestedTimeMs);

            byte[] data = response.readByteArray(block.dataSize[i]);
            if (header.mDataType == RawDataItem.DATA_TYPE_OBD) {
                item.data = ObdData.fromBinary(data);
            } else if (header.mDataType == RawDataItem.DATA_TYPE_IIO) {
                item.data = IioData.fromBinary(data);
            } else if (header.mDataType == RawDataItem.DATA_TYPE_GPS) {
                item.data = GpsData.fromBinary(data);
            }

            block.addRawDataItem(item);
        }


        return VdbResponse.success(block);
    }
}
