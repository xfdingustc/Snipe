package com.xfdingustc.snipe;

import java.io.IOException;

/**
 * Created by Xiaofei on 2015/8/18.
 */
public interface VdbSocket {
    void performRequest(VdbRequest<?> vdbRequest) throws SnipeError;

    VdbAcknowledge retrieveAcknowledge() throws IOException;
}
