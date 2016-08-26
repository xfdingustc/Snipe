package com.xfdingustc.snipe.hardware;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;


import com.xfdingustc.snipe.control.VdtCamera;
import com.xfdingustc.snipe.control.VdtCameraManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * //
 * Created by Xiaofei on 2015/9/17.
 */
public class DeviceScanner extends Thread {
    private static final String TAG = DeviceScanner.class.getSimpleName();

    private static final String SERVICE_TYPE = "_ccam._tcp.local.";

    private static final int SCAN_INTERVAL = 1000;

    public static final String SERVICE_VIDITCAM = "ViditCam";
    public static final String SERVICE_VIDIT_STUDIO = "Vidit Studio";
    private final Context mContext;

    @Nullable
    private WifiManager mWifiManager;
    private VdtCameraManager mVdtCameraManager = VdtCameraManager.getManager();
    private List<InetAddress> mAddress = new ArrayList<>();
    private List<JmDNS> mDns = new ArrayList<>();
    private WifiManager.MulticastLock mLock;
    private boolean mbRunning;

    public DeviceScanner(Context context) {
        super(TAG);
        this.mContext = context;
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }


    public synchronized void startWork() {
        if (!mbRunning) {
            mbRunning = true;
            start();
        }
    }


    public synchronized void stopWork() {
        if (mbRunning) {
            mbRunning = false;
            interrupt();
            notifyAll();
        }
    }


    @Override
    public void run() {

        threadLoop();
    }

    private synchronized void threadLoop() {
        while (mbRunning) {
            if (!mbRunning) {
                break;
            }
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while (en.hasMoreElements()) {
                    NetworkInterface ni = en.nextElement();
                    Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress addr = enumIpAddr.nextElement();
                        if (addr.isSiteLocalAddress()) {
//                            Logger.t(TAG).d("addr: " + addr);
                            mAddress.add(addr);
                        }
                    }
                }

                for (InetAddress addr : mAddress) {
                    JmDNS dns = JmDNS.create(addr, SERVICE_VIDITCAM);
                    mDns.add(dns);
                    dns.addServiceListener(SERVICE_TYPE, mServiceListener);

                }

                wait(SCAN_INTERVAL * 5);

                for (JmDNS dns : mDns) {
                    dns.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mAddress.clear();
                mDns.clear();
            }


            try {
                Thread.sleep(SCAN_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    ServiceListener mServiceListener = new ServiceListener() {
        @Override
        public void serviceAdded(ServiceEvent event) {
//            Logger.t(TAG).d("serviceAdded: " + event.getName() + ", " + event.getType());
//            Logger.t(TAG).d(event.getInfo().toString());
            // Vidit Camera, _ccam._tcp.local.
            event.getDNS().requestServiceInfo(event.getType(), event.getName(), 1);

        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
//            Logger.t(TAG).d("serviceRemoved: " + event.getName() + ", " + event.getType());
            Log.d(TAG, event.getInfo().toString());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
//            Logger.t(TAG).d("serviceResolved: " + event.getName() + ", " + event.getType());
//            Logger.t(TAG).d(event.getInfo().toString());

            ServiceInfo info = event.getInfo();
            Inet4Address[] addresses = info.getInet4Addresses();
            if (addresses.length > 0) {
                String name = event.getName();
                boolean bIsPcServer = name.equals(SERVICE_VIDIT_STUDIO);
                String serverName = info.getServer();
                int index = serverName.indexOf(".local.");
                if (index >= 0) {
                    serverName = serverName.substring(0, index);
                }
                VdtCamera.ServiceInfo serviceInfo = new VdtCamera.ServiceInfo(addresses[0], info
                    .getPort(), serverName, name, bIsPcServer);
                mVdtCameraManager.connectCamera(serviceInfo, "mdns");

            }

        }
    };


}