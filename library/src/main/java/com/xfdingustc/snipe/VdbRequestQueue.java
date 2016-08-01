package com.xfdingustc.snipe;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * VdbRequestQueue
 * Created by Xiaofei on 2015/8/17.
 */
public class VdbRequestQueue {
    private static final String TAG = VdbRequestQueue.class.getSimpleName();

    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 1;

    private static final int MAX_PENDING_REQUEST_COUNT = 4;


    //private final Set<VdbRequest<?>> mCurrentVdbRequests = new HashSet<VdbRequest<?>>();
    final ConcurrentHashMap<Integer, VdbRequest<?>> mCurrentVdbRequests = new ConcurrentHashMap<>();

    final ConcurrentHashMap<Integer, VdbMessageHandler<?>> mMessageHandlers = new ConcurrentHashMap<>();

    private final PriorityBlockingQueue<VdbRequest<?>> mVideoDatabaseQueue = new
        PriorityBlockingQueue<>();

    private final PriorityBlockingQueue<VdbRequest<?>> mWaitingQueue = new PriorityBlockingQueue<>();

    private final CircularQueue<VdbRequest<?>> mIgnorableRequestQueue = new CircularQueue<>(1);

    private AtomicInteger mPendingRequestCount = new AtomicInteger();

    private final VdbSocket mVdbSocket;
    private final ResponseDelivery mDelivery;

    private VdbDispatcher[] mVdbDispatchers;
    private VdbResponseDispatcher[] mVdbResponseDispatchers;

    public VdbRequestQueue(VdbSocket vdbSocket) {
        this(vdbSocket, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public VdbRequestQueue(VdbSocket vdbSocket, int threadPoolSize) {
        this(vdbSocket, threadPoolSize,
            new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    public VdbRequestQueue(VdbSocket vdbSocket, int threadPoolSize, ResponseDelivery delivery) {
        mVdbSocket = vdbSocket;
        mVdbDispatchers = new VdbDispatcher[threadPoolSize];
        mVdbResponseDispatchers = new VdbResponseDispatcher[threadPoolSize];
        mDelivery = delivery;
    }


    public void start() {
        stop();

        for (int i = 0; i < mVdbDispatchers.length; i++) {
            VdbDispatcher vdbDispatcher = new VdbDispatcher(mVideoDatabaseQueue, mVdbSocket, mDelivery);
            mVdbDispatchers[i] = vdbDispatcher;
            vdbDispatcher.start();
            VdbResponseDispatcher responseDispatcher = new VdbResponseDispatcher(mCurrentVdbRequests, mMessageHandlers, mVdbSocket, mDelivery);
            mVdbResponseDispatchers[i] = responseDispatcher;
            responseDispatcher.start();
        }
    }

    public void stop() {

        for (int i = 0; i < mVdbDispatchers.length; i++) {
            if (mVdbDispatchers[i] != null) {
                mVdbDispatchers[i].quit();
            }
        }
//        if (mVdbReceiver != null) {
//            mVdbReceiver.quit();
//        }
    }

    public <T> void registerMessageHandler(VdbMessageHandler<T> messageHandler) {
        messageHandler.setRequestQueue(this);
        mMessageHandlers.put(messageHandler.getMessageCode(), messageHandler);
    }

    public VdbMessageHandler<?> unregisterMessageHandler(int msgCode) {
        return mMessageHandlers.remove(msgCode);
    }

    public <T> VdbRequest<T> add(VdbRequest<T> vdbRequest) {
        return add(vdbRequest, false);
    }

    private <T> VdbRequest<T> add(VdbRequest<T> vdbRequest, boolean isPendingRequest) {
        if (vdbRequest.isIgnorable()) {
            if (mPendingRequestCount.get() >= MAX_PENDING_REQUEST_COUNT) {
                if (!isPendingRequest) {
                    mIgnorableRequestQueue.offer(vdbRequest);
                }
                return vdbRequest;
            } else {
                int count = mPendingRequestCount.incrementAndGet();
                //Log.e("test", "Pending Count1: " + count);
            }
        }

        vdbRequest.setRequestQueue(this);
        mCurrentVdbRequests.put(vdbRequest.getSequence(), vdbRequest);
        vdbRequest.addMarker("add-to-queue");
        if (mCurrentVdbRequests.size() >= MAX_PENDING_REQUEST_COUNT) {
            mWaitingQueue.add(vdbRequest);
        } else {
            mVideoDatabaseQueue.add(vdbRequest);
        }
        return vdbRequest;
    }

    <T> void finish(VdbRequest<T> vdbRequest) {
        mCurrentVdbRequests.remove(vdbRequest.getSequence());
        if (mVideoDatabaseQueue.size() < MAX_PENDING_REQUEST_COUNT && mWaitingQueue.size() > 0) {
            VdbRequest request = mWaitingQueue.poll();
            mVideoDatabaseQueue.add(request);
        } 

        if (vdbRequest.isIgnorable()) {
            int count = mPendingRequestCount.decrementAndGet();
            //Log.e("test", "Pending Count2: " + count);
            VdbRequest request = mIgnorableRequestQueue.poll();
            if (request != null) {
                add(request, true);
            }
        }
    }

    public void cancelAll() {
        for (VdbRequest<?> request : mCurrentVdbRequests.values()) {
            request.cancel();
        }
    }

    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(VdbRequest<?> request) {
                if (request.getTag() == null) {
                    return false;
                }
                return request.getTag().equals(tag);
            }
        });
    }

    public void cancelAll(RequestFilter filter) {
        for (VdbRequest<?> request : mCurrentVdbRequests.values()) {
            if (filter.apply(request)) {
                request.cancel();
            }
        }

    }

    public interface RequestFilter {
        boolean apply(VdbRequest<?> request);
    }
}
