package com.xfdingustc.snipe;

import java.io.IOException;

/**
 * Created by Xiaofei on 2015/8/18.
 */
public class BasicVdbSocket implements VdbSocket {
    private final static String TAG = BasicVdbSocket.class.getSimpleName();
    private final VdbConnection mConnection;

    public BasicVdbSocket(VdbConnection connection) {
        this.mConnection = connection;
    }

    @Override
    public void performRequest(VdbRequest<?> vdbRequest) throws SnipeError {
        try {
            VdbCommand vdbCommand = vdbRequest.createVdbCommand();
            vdbCommand.setSequence(vdbRequest.getSequence());
            mConnection.sendCommnd(vdbCommand);
        } catch (Exception e) {
            throw new SnipeError();
        }
    }

    @Override
    public VdbAcknowledge retrieveAcknowledge() throws IOException {
        return new VdbAcknowledge(0, mConnection);
    }
}
