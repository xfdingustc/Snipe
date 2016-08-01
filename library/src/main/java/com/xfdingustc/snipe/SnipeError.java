package com.xfdingustc.snipe;

/**
 * Created by Xiaofei on 2015/8/17.
 */
public class SnipeError extends Exception {
    public SnipeError() {
        super();
    }

    public SnipeError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public SnipeError(Throwable cause) {
        super(cause);
    }
}
