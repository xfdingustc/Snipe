package com.xfdingustc.snipe.toolbox;


import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.ClipActionInfo;

/**
 * Created by Richard on 12/28/15.
 */
public class MarkLiveMsgHandler extends ClipInfoMsgHandler {

    public MarkLiveMsgHandler(VdbResponse.Listener<ClipActionInfo> listener, VdbResponse.ErrorListener errorListener) {
        super(VdbCommand.Factory.VDB_MSG_MarkLiveClipInfo, listener, errorListener);
    }

    @Override
    protected VdbResponse<ClipActionInfo> parseVdbResponse(VdbAcknowledge response) {
        VdbResponse<ClipActionInfo> vdbResponse = super.parseVdbResponse(response);

        ClipActionInfo.MarkLiveInfo info = new ClipActionInfo.MarkLiveInfo();
        info.flags = response.readi32(); // flags, not used
        info.delay_ms = response.readi32();
        info.before_live_ms = response.readi32();
        info.after_live_ms = response.readi32();

        vdbResponse.result.markLiveInfo = info;

        return VdbResponse.success(vdbResponse.result);
    }
}
