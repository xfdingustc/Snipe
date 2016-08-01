package com.xfdingustc.snipe;


import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Created by Xiaofei on 2015/8/17.
 */
public class ExecutorDelivery implements ResponseDelivery {


    private final Executor mResponsePoster;

    public ExecutorDelivery(final Handler handler) {
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };

    }

    @Override
    public void postResponse(VdbRequest<?> vdbRequest, VdbResponse<?> vdbResponse) {
        postResponse(vdbRequest, vdbResponse, null);
    }

    @Override
    public void postResponse(VdbRequest<?> vdbRequest, VdbResponse<?> vdbResponse, Runnable runnable) {
        vdbRequest.markDelivered();
        vdbRequest.addMarker("post-response");
        mResponsePoster.execute(new ResponseDeliveryRunnable(vdbRequest, vdbResponse, runnable));
    }

    @Override
    public void postError(VdbRequest<?> vdbRequest, SnipeError error) {
        postResponse(vdbRequest, null, null);
    }

    private class ResponseDeliveryRunnable implements Runnable {

        private final VdbRequest mRequest;
        private final VdbResponse mResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(VdbRequest vdbRequest, VdbResponse vdbResponse, Runnable
            runnable) {
            mRequest = vdbRequest;
            mResponse = vdbResponse;
            mRunnable = runnable;
        }

        @Override
        public void run() {
            if (mRequest.isCanceled()) {
                mRequest.finish("canceled-at-delivery", true);
                return;
            } else {
                mRequest.finish("finish-at-delivery", false);
            }


            if (mResponse != null && mResponse.isSuccess()) {
                mRequest.deliverResponse(mResponse.result);
            } else {
                mRequest.deliverError(new SnipeError());
            }
        }
    }
}
