package com.xfdingustc.snipe.sample;

import android.app.Application;
import android.net.nsd.NsdServiceInfo;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.control.VdtCamera;
import com.xfdingustc.snipe.control.VdtCameraManager;
import com.xfdingustc.snipe.hardware.CameraDiscovery;
import com.xfdingustc.snipe.hardware.DeviceScanner;

/**
 * Created by Xiaofei on 2016/8/1.
 */
public class SnipeSampleApplication extends Application {
    private static final String TAG = SnipeSampleApplication.class.getSimpleName();

    private DeviceScanner mScanner;

    @Override
    public void onCreate() {
        super.onCreate();
        CameraDiscovery.discoverCameras(this, new CameraDiscovery.Callback() {
            @Override
            public void onCameraFound(NsdServiceInfo cameraService) {
                String serviceName = cameraService.getServiceName();
                boolean bIsPcServer = serviceName.equals("Vidit Studio");
                final VdtCamera.ServiceInfo serviceInfo = new VdtCamera.ServiceInfo(
                    cameraService.getHost(),
                    cameraService.getPort(),
                    "", serviceName, bIsPcServer);
                VdtCameraManager.getManager().connectCamera(serviceInfo, "CameraDiscovery");

            }

            @Override
            public void onError(int errorCode) {
                Logger.t(TAG).e("errorCode: " + errorCode);
            }
        });

        startDeviceScanner();
    }

    public void startDeviceScanner() {
        if (mScanner != null) {
            mScanner.stopWork();
        }
        mScanner = new DeviceScanner(this);
        mScanner.startWork();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CameraDiscovery.stopDiscovery();
        mScanner.stopWork();
    }
}
