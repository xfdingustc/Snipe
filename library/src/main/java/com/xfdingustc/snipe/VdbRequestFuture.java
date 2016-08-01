package com.xfdingustc.snipe;



import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Xiaofei on 2016/6/18.
 */
public class VdbRequestFuture<T> implements Future<T>, VdbResponse.Listener<T>, VdbResponse.ErrorListener {

    private VdbRequest<?> mVdbRequest;
    private boolean mResultReceived = false;
    private T mResult;
    private SnipeError mException;

    public static <E> VdbRequestFuture<E> newFuture() {
        return new VdbRequestFuture<E>();
    }

    private VdbRequestFuture() {

    }

    public void setRequest(VdbRequest<?> request) {
        mVdbRequest = request;
    }



    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        if (mVdbRequest == null) {
            return false;
        }

        if (!isDone()) {
            mVdbRequest.cancel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isCancelled() {
        if (mVdbRequest == null) {
            return false;
        }

        return mVdbRequest.isCanceled();
    }

    @Override
    public boolean isDone() {
        return mResultReceived || mException != null || isCancelled();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return doGet(null);
        } catch (TimeoutException e) {
            throw new AssertionError(e);
        }

    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(TimeUnit.MILLISECONDS.convert(timeout, unit));
    }

    private synchronized T doGet(Long timeoutMs) throws ExecutionException, InterruptedException, TimeoutException {
        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (mResultReceived) {
            return mResult;
        }

        if (timeoutMs == null) {
            wait(0);
        } else if (timeoutMs > 0) {
            wait(timeoutMs);
        }


        if (mException != null) {
            throw new ExecutionException(mException);
        }

        if (!mResultReceived) {
            throw new TimeoutException();
        }

        return mResult;
    }



    @Override
    public synchronized void onResponse(T response) {
        mResultReceived = true;
        mResult = response;
        notifyAll();
    }

    @Override
    public synchronized void onErrorResponse(SnipeError error) {
        mException = error;
        notifyAll();
    }
}
