package com.xfdingustc.snipe;

/**
 * Created by Xiaofei on 2016/6/28.
 */
public interface RetryPolicy {
    int getCurrentTimeout();

    int getCurrentRetryCount();

    void retry(SnipeError error) throws SnipeError;
}
