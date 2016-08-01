package com.xfdingustc.snipe.toolbox;


import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbMessageHandler;
import com.xfdingustc.snipe.VdbResponse;

/**
 * Created by laina on 16/6/28.
 */
public class VdbUnmountedMsgHandler extends VdbMessageHandler<Object> {
    public VdbUnmountedMsgHandler(VdbResponse.Listener<Object> listener,
                                  VdbResponse.ErrorListener errorListener) {
        super(VdbCommand.Factory.MSG_VdbUnmounted, listener, errorListener);
    }

    @Override
    protected VdbResponse<Object> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            return null;
        }
        Object object = new Object();
        return VdbResponse.success(object);
    }
}
