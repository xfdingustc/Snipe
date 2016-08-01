package com.xfdingustc.snipe.sample;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.BasicVdbSocket;
import com.xfdingustc.snipe.VdbConnection;
import com.xfdingustc.snipe.VdbRequestQueue;
import com.xfdingustc.snipe.VdbSocket;

import java.io.IOException;

/**
 * Created by Xiaofei on 2016/8/1.
 */
public class Vdb {
    private static VdbRequestQueue mSharedRequestQueue = null;

    public static VdbRequestQueue getVdbRequestQueue() {
        if (mSharedRequestQueue == null) {
            VdbConnection vdbConnection = new VdbConnection("192.168.31.227");

            try {
                Logger.t("VDB").d("Try to connect vdb");
                vdbConnection.connect();
                VdbSocket vdbSocket = new BasicVdbSocket(vdbConnection);
                mSharedRequestQueue = new VdbRequestQueue(vdbSocket);
                mSharedRequestQueue.start();
                Logger.t("VDB").d("Request queue started");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return mSharedRequestQueue;
    }
}
