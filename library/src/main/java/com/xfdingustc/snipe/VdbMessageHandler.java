package com.xfdingustc.snipe;

/**
 * Created by Richard on 12/28/15.
 */
public abstract class VdbMessageHandler<T> extends VdbRequest<T> {

    private int mMessageCode;

    public VdbMessageHandler(int messageCode, VdbResponse.Listener<T> listener, VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        mMessageCode = messageCode;
        setIsMessageHandler(true);
    }

    @Override
    protected VdbCommand createVdbCommand() {
        return null;
    }

    public int getMessageCode() {
        return mMessageCode;
    }

    public void unregister() {
        VdbRequestQueue requestQueue = getRequestQueue();
        if (requestQueue != null) {
            requestQueue.unregisterMessageHandler(mMessageCode);
        }
    }
}
