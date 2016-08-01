package com.xfdingustc.snipe;

/**
 * Created by Xiaofei on 2015/8/17.
 */
public class VdbResponse<T> {
    public interface Listener<T> {
        void onResponse(T response);
    }

    public interface ErrorListener {
        void onErrorResponse(SnipeError error);
    }

    public static <T> VdbResponse<T> success(T result) {
        return new VdbResponse<T>(result);
    }

    public final T result;
    public final SnipeError error;

    public boolean isSuccess() {
        return error == null;
    }

    private VdbResponse(T result) {
        this.result = result;
        this.error = null;
    }

    private VdbResponse(SnipeError error) {
        this.result = null;
        this.error = error;
    }
}
