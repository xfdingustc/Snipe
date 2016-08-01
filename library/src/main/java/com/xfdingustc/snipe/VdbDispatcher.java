package com.xfdingustc.snipe;

import android.os.Process;
import android.os.SystemClock;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Xiaofei on 2015/8/18.
 */
public class VdbDispatcher extends Thread {
    private final static String TAG = VdbDispatcher.class.getSimpleName();
    private final BlockingQueue<VdbRequest<?>> mQueue;
    private final VdbSocket mVdbSocket;
    private final ResponseDelivery mDelivery;

    private volatile boolean mQuit = false;

    public VdbDispatcher(BlockingQueue<VdbRequest<?>> queue, VdbSocket vdbSocket,
                         ResponseDelivery delivery) {
        super("VdbDispatcher");
        this.mQueue = queue;
        this.mVdbSocket = vdbSocket;
        this.mDelivery = delivery;
    }


    public void quit() {
        mQuit = true;
        interrupt();
    }


    @Override
    public void run() {
        //super.run();
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            long startTimeMs = SystemClock.elapsedRealtime();
            VdbRequest<?> vdbRequest;

            try {
                vdbRequest = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                vdbRequest.addMarker("com.waylens.hachi.library.vdb-queue-take");

                if (vdbRequest.isCanceled()) {
                    vdbRequest.finish("com.waylens.hachi.library.vdb-discard-cancelled", true);
                    continue;
                }
                // Perform the network request
                mVdbSocket.performRequest(vdbRequest);
            } catch (SnipeError snipeError) {
                snipeError.printStackTrace();
                parseAndDeliverVdbError(vdbRequest, snipeError);
            }

        }
    }

    private void parseAndDeliverVdbError(VdbRequest<?> vdbRequest, SnipeError snipeError) {
        snipeError = vdbRequest.parseVdbError(snipeError);
        mDelivery.postError(vdbRequest, snipeError);
    }
}
