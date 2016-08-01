package com.xfdingustc.snipe.toolbox;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.SpaceInfo;


/**
 * Created by Xiaofei on 2016/5/5.
 */
public class GetSpaceInfoRequest extends VdbRequest<SpaceInfo> {
    private static final String TAG = GetSpaceInfoRequest.class.getSimpleName();
    public GetSpaceInfoRequest(VdbResponse.Listener<SpaceInfo> listener, VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
    }

    @Override
    protected VdbCommand createVdbCommand() {
        mVdbCommand = VdbCommand.Factory.createCmdGetSpaceInfo();
        return mVdbCommand;

    }

    @Override
    protected VdbResponse<SpaceInfo> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).d("response: " + response.getRetCode());
            return null;
        }

        SpaceInfo spaceInfo = new SpaceInfo();

        spaceInfo.total = response.readi64();
        spaceInfo.used = response.readi64();
        spaceInfo.marked = response.readi64();

        return VdbResponse.success(spaceInfo);
    }
}
