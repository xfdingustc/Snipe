package com.xfdingustc.snipe.toolbox;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbMessageHandler;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.rawdata.GpsData;
import com.xfdingustc.snipe.vdb.rawdata.IioData;
import com.xfdingustc.snipe.vdb.rawdata.ObdData;
import com.xfdingustc.snipe.vdb.rawdata.RawDataItem;

import java.util.ArrayList;
import java.util.List;



public class RawDataMsgHandler extends VdbMessageHandler<List<RawDataItem>> {
    public static String TAG = RawDataMsgHandler.class.getSimpleName();

    public RawDataMsgHandler(VdbResponse.Listener<List<RawDataItem>> listener,
                             VdbResponse.ErrorListener errorListener) {
        super(VdbCommand.Factory.MSG_RawData, listener, errorListener);
        rawDataItemList = new ArrayList<RawDataItem>(3);
        Logger.t(TAG).d("rawDataList size = " + rawDataItemList.size());
        for (int i = 0; i < 3; i++) {
            rawDataItemList.add(null);
        }
        Logger.t(TAG).d("rawDataList size = " + rawDataItemList.size());

    }

    public static int OBD_DATA = 0;
    public static int IIO_DATA = 1;
    public static int GPS_DATA = 2;

    private List<RawDataItem> rawDataItemList;
    int[] unchangedCount = new int[]{-1, -1, -1};
    int periodReached = 0;


    @Override
    protected VdbResponse<List<RawDataItem>> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            return null;
        }
        //Logger.t(TAG).d("parseVdbResponse");
        int dataType = response.readi32();
        byte[] data = response.readByteArray();
        List<RawDataItem> rawDataItemListTmp = new ArrayList<RawDataItem>();
        RawDataItem rawDataItem = new RawDataItem(dataType, 0);
        for (int i = 0; i < unchangedCount.length; i++) {
            if (unchangedCount[i] >= 0)
                unchangedCount[i]++;
        }
        switch (dataType) {
            case RawDataItem.DATA_TYPE_OBD:
                unchangedCount[OBD_DATA] = 0;
                rawDataItem.data = ObdData.fromBinary(data);
                if (rawDataItemList.get(OBD_DATA) != null) {
                    periodReached = 1;
                }
                rawDataItemList.set(OBD_DATA, rawDataItem);
                break;
            case RawDataItem.DATA_TYPE_IIO:
                unchangedCount[IIO_DATA] = 0;
                rawDataItem.data = IioData.fromBinary(data);
                if (rawDataItemList.get(IIO_DATA) != null) {
                    periodReached = 1;
                }
                rawDataItemList.set(IIO_DATA, rawDataItem);
                break;
            case RawDataItem.DATA_TYPE_GPS:
                unchangedCount[GPS_DATA] = 0;
                rawDataItem.data = GpsData.fromBinary(data);
                if (rawDataItemList.get(GPS_DATA) != null) {
                    periodReached = 1;
                }
                rawDataItemList.set(GPS_DATA, rawDataItem);
                break;
            default:
                return null;
        }
        for (int i = 0; i < unchangedCount.length; i++) {
            if (unchangedCount[i] > 300) {
                rawDataItemList.set(i, null);
                unchangedCount[i] = -1;
            }
        }


        if (periodReached != 0) {
            for (int i = 0; i < rawDataItemList.size(); i++) {
                if (rawDataItemList.get(i) != null)
                    rawDataItemListTmp.add(rawDataItemList.get(i));
            }
            periodReached = 0;
            //Logger.t(TAG).d("should show off");
        } else {
            Logger.t(TAG).d(unchangedCount[0] + "   " + unchangedCount[1] + "    " + unchangedCount[2]);
            return null;

        }
        return VdbResponse.success(rawDataItemListTmp);
    }
}
