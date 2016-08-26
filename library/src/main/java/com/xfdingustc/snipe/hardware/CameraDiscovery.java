package com.xfdingustc.snipe.hardware;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import com.orhanobut.logger.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Xiaofei on 2016/3/10.
 */
public class CameraDiscovery {
    private static final String TAG = "CameraDiscovery";

    private static final String SERVICE_TYPE = "_ccam._tcp";

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    private static CameraDiscovery _INSTANCE = new CameraDiscovery();
    private AtomicBoolean mIsStarted = new AtomicBoolean(false);

    /**
     * Discovery the available cameras
     *
     * @param context
     * @param callback
     */
    public static void discoverCameras(Context context, final Callback callback) {
        _INSTANCE.discoverCamerasImpl(context, callback);
    }

    public static boolean isStarted() {
        return _INSTANCE.mIsStarted.get();
    }

    public static void stopDiscovery() {
        _INSTANCE.stopDiscoveryImpl();
    }

    private void discoverCamerasImpl(Context context, final Callback callback) {
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                mIsStarted.set(false);
                callback.onError(errorCode);
                Logger.t(TAG).d("onStartDiscoveryFailed: " + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Logger.t(TAG).d("onStopDiscoveryFailed: " + errorCode);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                mIsStarted.set(true);
//                Logger.t(TAG).d("onDiscoveryStarted: " + serviceType);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                mIsStarted.set(false);
//                Logger.t(TAG).d("onDiscoveryStopped: " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                mIsStarted.set(true);
//                Logger.t(TAG).d("onServiceFound");
                mNsdManager.resolveService(serviceInfo, createResolveListener(callback));
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                mIsStarted.set(false);
//                Logger.t(TAG).d("onServiceLost");
            }
        };
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);

    }

    private NsdManager.ResolveListener createResolveListener(final Callback callback) {
        return new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Logger.t(TAG).d("onResolveFailed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                callback.onCameraFound(serviceInfo);
            }
        };
    }


    public void stopDiscoveryImpl() {
        if (mNsdManager != null && mIsStarted.get()) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
    }

    public interface Callback {
        void onCameraFound(NsdServiceInfo cameraService);

        void onError(int errorCode);
    }
}
