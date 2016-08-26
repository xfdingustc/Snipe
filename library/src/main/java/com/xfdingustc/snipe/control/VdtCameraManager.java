package com.xfdingustc.snipe.control;



import android.util.Log;


import com.xfdingustc.snipe.VdbRequestQueue;
import com.xfdingustc.snipe.control.events.CameraConnectionEvent;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class VdtCameraManager {
    private static final String TAG = VdtCameraManager.class.getSimpleName();

    private static VdtCameraManager mSharedManager = new VdtCameraManager();


    private EventBus mEventBus = EventBus.getDefault();


    public static VdtCameraManager getManager() {
        return mSharedManager;
    }


    // note: CameraManager is a global data,
    // we have to track each callback even they are installed by the same activity.


    // cameras: connected + connecting + wifi-ap
    private final List<VdtCamera> mConnectedVdtCameras = new ArrayList<>();
    private final List<VdtCamera> mConnectingVdtCameras = new ArrayList<>();

    private VdtCamera mCurrentCamera;


    public synchronized void connectCamera(VdtCamera.ServiceInfo serviceInfo, String from) {

        if (cameraExistsIn(serviceInfo.inetAddr, serviceInfo.port, mConnectedVdtCameras)) {
//            Logger.t(TAG).d("already existed in connected");
            return;
        }

        if (cameraExistsIn(serviceInfo.inetAddr, serviceInfo.port, mConnectingVdtCameras)) {
//            Logger.t(TAG).d("already existed in connecting");
            return;
        }
        Log.d(TAG, "connect Camera  " + serviceInfo.inetAddr + " port: " + serviceInfo.port + " from " + from);

        VdtCamera vdtCamera = new VdtCamera(serviceInfo, new VdtCamera.OnConnectionChangeListener() {
            @Override
            public void onConnected(VdtCamera vdtCamera) {

                mEventBus.post(new CameraConnectionEvent(CameraConnectionEvent.VDT_CAMERA_CONNECTING, vdtCamera));
            }

            @Override
            public void onVdbConnected(VdtCamera vdtCamera) {
                onCameraConnected(vdtCamera);
                if (mCurrentCamera == null) {
                    mCurrentCamera = vdtCamera;
                }
                mEventBus.post(new CameraConnectionEvent(CameraConnectionEvent.VDT_CAMERA_CONNECTED, vdtCamera));
            }

            @Override
            public void onDisconnected(VdtCamera vdtCamera) {
                onCameraDisconnected(vdtCamera);
            }
        });

        Log.d(TAG, "create new VdtCamera current connected camera size: " + mConnectedVdtCameras.size() + " connecting: " + mConnectingVdtCameras.size());


        mConnectingVdtCameras.add(vdtCamera);
//        vdtCamera.startClient();


    }

    private boolean cameraExistsIn(InetAddress inetAddr, int port, List<VdtCamera> list) {
        for (VdtCamera c : list) {
            InetAddress address = c.getAddress();
            if (address.equals(inetAddr))
                return true;
        }
        return false;
    }


    public List<VdtCamera> getConnectedCameras() {
        return mConnectedVdtCameras;
    }


    public VdtCamera findConnectedCamera(String ssid, String hostString) {
        return findCameraInList(ssid, hostString, mConnectedVdtCameras);
    }


    private VdtCamera findCameraInList(String ssid, String hostString, List<VdtCamera> list) {
        for (VdtCamera c : list) {
            if (c.idMatch(ssid, hostString))
                return c;
        }
        return null;
    }


    public boolean isConnected() {
        if (getConnectedCameras().size() > 0) {
            return true;
        }
        return false;
    }


    public void setCurrentCamera(int position) {
        this.mCurrentCamera = mConnectedVdtCameras.get(position);
    }


    public VdtCamera getCurrentCamera() {
        return mCurrentCamera;
    }

    public VdbRequestQueue getCurrentVdbRequestQueue() {
        if (mCurrentCamera != null) {
            return mCurrentCamera.getRequestQueue();
        } else {
            return null;
        }
    }

    private void onCameraConnected(VdtCamera vdtCamera) {
        for (int i = 0; i < mConnectingVdtCameras.size(); i++) {
            VdtCamera oneCamera = mConnectingVdtCameras.get(i);
            if (oneCamera == vdtCamera) {
                mConnectingVdtCameras.remove(i);
                mConnectedVdtCameras.add(vdtCamera);
                Log.d(TAG, "camera connected: " + vdtCamera.getInetSocketAddress());
                return;
            }
        }

        Log.d(TAG, "camera connected, but was not connecting, stop it");


    }


    private synchronized void onCameraDisconnected(VdtCamera vdtCamera) {
        // disconnect msg may be sent from msg thread,
        // need to stop it fully
        //vdtCamera.removeCallback(mCameraCallback);


        for (int i = 0; i < mConnectedVdtCameras.size(); i++) {
            if (mConnectedVdtCameras.get(i) == vdtCamera) {
                mConnectedVdtCameras.remove(i);
                Log.d(TAG, "camera disconnected " + vdtCamera.getInetSocketAddress());
                break;
            }
        }

        for (int i = 0; i < mConnectingVdtCameras.size(); i++) {
            if (mConnectingVdtCameras.get(i) == vdtCamera) {
                mConnectingVdtCameras.remove(i);
                Log.d(TAG, "connecting camera disconnected " + vdtCamera.getInetSocketAddress());
                break;
            }
        }

        if (vdtCamera == mCurrentCamera) {
            if (mConnectedVdtCameras.size() == 0) {
                mCurrentCamera = null;
            } else {
                mCurrentCamera = mConnectedVdtCameras.get(0);
            }
        }

        Log.d(TAG, "camera disconnected camera size: " + mConnectedVdtCameras.size() + " connecting: " + mConnectingVdtCameras.size());
        mEventBus.post(new CameraConnectionEvent(CameraConnectionEvent.VDT_CAMERA_DISCONNECTED, vdtCamera));


    }


}
