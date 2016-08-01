package com.xfdingustc.snipe;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Xiaofei on 2015/8/17.
 */
public abstract class VdbRequest<T> implements Comparable<VdbRequest<T>> {

    private static AtomicInteger mSequenceGenerator = new AtomicInteger(1111);

    protected int mMethod;
    private VdbResponse.Listener<T> mListener;

    private VdbResponse.ErrorListener mErrorListener;

    private Integer mSequence;

    private boolean mCanceled = false;
    private boolean mResponseDelivered = false;

    private VdbRequestQueue mVdbRequestQueue;

    protected VdbCommand mVdbCommand;

    private boolean mIsIgnorable;

    private Object mTag;
    private boolean mIsMessageHandler;

    private RetryPolicy mRetryPolicy;

    public VdbRequest(int method, VdbResponse.Listener<T> listener, VdbResponse.ErrorListener errorListener) {
        this.mMethod = method;
        this.mListener = listener;
        this.mErrorListener = errorListener;
        mSequence = mSequenceGenerator.incrementAndGet();
        setRetryPolicy(new DefaultRetryPolicy());
    }

    private VdbRequest<?> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public void addMarker(String tag) {
        // TODO: implement me
    }

    public VdbRequest<T> setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public Object getTag() {
        return mTag;
    }

    public void cancel() {
        mCanceled = true;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }


    protected void finish(final String tag, boolean shouldClean) {
        if (mVdbRequestQueue != null) {
            mVdbRequestQueue.finish(this);
        }
        if (shouldClean && !mIsMessageHandler) {
            clean();
        }
    }

    public VdbRequest<?> setRequestQueue(VdbRequestQueue vdbRequestQueue) {
        mVdbRequestQueue = vdbRequestQueue;
        return this;
    }

    public VdbRequestQueue getRequestQueue() {
        return mVdbRequestQueue;
    }

    public Integer getSequence() {
        return mSequence;
    }

    public void markDelivered() {
        if (!mIsMessageHandler) {
            mResponseDelivered = true;
        }
    }

    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }


    abstract protected VdbCommand createVdbCommand();

    public VdbCommand getVdbCommand() {
        return mVdbCommand;
    }

    abstract protected VdbResponse<T> parseVdbResponse(VdbAcknowledge response);

    protected SnipeError parseVdbError(SnipeError snipeError) {
        return snipeError;
    }


    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
        if (!mIsMessageHandler) {
            clean();
        }
    }


    final protected void deliverError(SnipeError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
        if (!mIsMessageHandler) {
            clean();
        }
    }

    @Override
    public int compareTo(VdbRequest<T> another) {
        Priority left = this.getPriority();
        Priority right = another.getPriority();

        return left == right ? this.mSequence - another.mSequence : right.ordinal() - left.ordinal();

    }

    public boolean isMessageHandler() {
        return mIsMessageHandler;
    }

    public void setIsMessageHandler(boolean isMessageHandler) {
        mIsMessageHandler = isMessageHandler;
    }

    public boolean isIgnorable() {
        return mIsIgnorable;
    }

    public void setIgnorable(boolean isIgnorable) {
        mIsIgnorable = isIgnorable;
    }

    void clean() {
        mListener = null;
        mErrorListener = null;
        mVdbCommand = null;
    }
}
