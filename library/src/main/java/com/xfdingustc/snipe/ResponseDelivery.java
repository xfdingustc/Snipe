package com.xfdingustc.snipe;

/**
 * Created by Xiaofei on 2015/8/17.
 */
public interface ResponseDelivery {
    void postResponse(VdbRequest<?> vdbRequest, VdbResponse<?> vdbResponse);

    void postResponse(VdbRequest<?> vdbRequest, VdbResponse<?> vdbResponse, Runnable runnable);

    void postError(VdbRequest<?> vdbRequest, SnipeError error);
}
