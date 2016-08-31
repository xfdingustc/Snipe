package com.xfdingustc.snipe.control;


import android.util.Log;

import com.orhanobut.logger.Logger;
import com.xfdingustc.rxutils.library.RxBus;
import com.xfdingustc.snipe.BasicVdbSocket;
import com.xfdingustc.snipe.SnipeError;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbConnection;
import com.xfdingustc.snipe.VdbRequestQueue;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.VdbSocket;
import com.xfdingustc.snipe.control.entities.NetworkItemBean;
import com.xfdingustc.snipe.control.events.BluetoothEvent;
import com.xfdingustc.snipe.control.events.CameraStateChangeEvent;
import com.xfdingustc.snipe.control.events.MarkLiveMsgEvent;
import com.xfdingustc.snipe.control.events.NetworkEvent;
import com.xfdingustc.snipe.toolbox.ClipInfoMsgHandler;
import com.xfdingustc.snipe.toolbox.MarkLiveMsgHandler;
import com.xfdingustc.snipe.toolbox.RawDataMsgHandler;
import com.xfdingustc.snipe.toolbox.VdbReadyMsgHandler;
import com.xfdingustc.snipe.toolbox.VdbUnmountedMsgHandler;
import com.xfdingustc.snipe.utils.ToStringUtils;
import com.xfdingustc.snipe.vdb.ClipActionInfo;
import com.xfdingustc.snipe.vdb.VdbReadyInfo;
import com.xfdingustc.snipe.vdb.rawdata.RawDataItem;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class VdtCamera implements VdtCameraCmdConsts {
    private static final String TAG = VdtCamera.class.getSimpleName();

    public static final int STATE_MIC_UNKNOWN = -1;
    public static final int STATE_MIC_ON = 0;
    public static final int STATE_MIC_OFF = 1;

    public static final int STATE_SPEAKER_UNKNOWN = -1;
    public static final int STATE_SPEAKER_OFF = 0;
    public static final int STATE_SPEAKER_ON = 1;

    public static final int STATE_BATTERY_UNKNOWN = -1;
    public static final int STATE_BATTERY_FULL = 0;
    public static final int STATE_BATTERY_NOT_CHARGING = 1;
    public static final int STATE_BATTERY_DISCHARGING = 2;
    public static final int STATE_BATTERY_CHARGING = 3;

    public static final int STATE_POWER_UNKNOWN = -1;
    public static final int STATE_POWER_NO = 0;
    public static final int STATE_POWER_YES = 1;

    public static final int STATE_RECORD_UNKNOWN = -1;
    public static final int STATE_RECORD_STOPPED = 0;
    public static final int STATE_RECORD_STOPPING = 1;
    public static final int STATE_RECORD_STARTING = 2;
    public static final int STATE_RECORD_RECORDING = 3;
    public static final int STATE_RECORD_SWITCHING = 4;

    public static final int STATE_STORAGE_UNKNOWN = -1;
    public static final int STATE_STORAGE_NO_STORAGE = 0;
    public static final int STATE_STORAGE_LOADING = 1;
    public static final int STATE_STORAGE_READY = 2;
    public static final int STATE_STORAGE_ERROR = 3;
    public static final int STATE_STORAGE_USBDISC = 4;

    public static final int OVERLAY_FLAG_NAME = 0x01;
    public static final int OVERLAY_FLAG_TIME = 0x02;
    public static final int OVERLAY_FLAG_GPS = 0x04;
    public static final int OVERLAY_FLAG_SPEED = 0x08;

    public static final int VIDEO_RESOLUTION_UNKNOWN = -1;
    public static final int VIDEO_RESOLUTION_1080P30 = 0;
    public static final int VIDEO_RESOLUTION_1080P60 = 1;
    public static final int VIDEO_RESOLUTION_720P30 = 2;
    public static final int VIDEO_RESOLUTION_720P60 = 3;
    public static final int VIDEO_RESOLUTION_4KP30 = 4;
    public static final int VIDEO_RESOLUTION_4KP60 = 5;
    public static final int VIDEO_RESOLUTION_480P30 = 6;
    public static final int VIDEO_RESOLUTION_480P60 = 7;
    public static final int VIDEO_RESOLUTION_720P120 = 8;
    public static final int VIDEO_RESOLUTION_STILL = 9;
    public static final int VIDEO_RESOLUTION_NUM = 10;

    public static final int VIDEO_RESOLUTION_720P = 0;
    public static final int VIDEO_RESOLUTION_1080P = 1;

    public static final int VIDEO_FRAMERATE_30FPS = 0;
    public static final int VIDEO_FRAMERATE_60FPS = 1;
    public static final int VIDEO_FRAMERATE_120FPS = 2;

    public static final int VIDEO_QUALITY_UNKNOWN = -1;
    public static final int VIDEO_QUALITY_SUPPER = 0;
    public static final int VIDEO_QUALITY_HI = 1;
    public static final int VIDEO_QUALITY_MID = 2;
    public static final int VIDEO_QUALITY_LOW = 3;
    public static final int VIDEO_QUALITY_NUM = 4;

    public static final int REC_MODE_UNKNOWN = -1;
    public static final int FLAG_AUTO_RECORD = 1 << 0;
    public static final int FLAG_LOOP_RECORD = 1 << 1;
    public static final int REC_MODE_MANUAL = 0;
    public static final int REC_MODE_AUTOSTART = FLAG_AUTO_RECORD;
    public static final int REC_MODE_MANUAL_LOOP = FLAG_LOOP_RECORD;
    public static final int REC_MODE_AUTOSTART_LOOP = (FLAG_AUTO_RECORD | FLAG_LOOP_RECORD);

    public static final int COLOR_MODE_UNKNOWN = -1;
    public static final int COLOR_MODE_NORMAL = 0;
    public static final int COLOR_MODE_SPORT = 1;
    public static final int COLOR_MODE_CARDV = 2;
    public static final int COLOR_MODE_SCENE = 3;
    public static final int COLOR_MODE_NUM = 4;

    public static final int ERROR_START_RECORD_OK = 0;
    public static final int ERROR_START_RECORD_NO_CARD = 1;
    public static final int ERROR_START_RECORD_CARD_FULL = 2;
    public static final int ERROR_START_RECORD_CARD_ERROR = 3;


    public static final int WIFI_MODE_UNKNOWN = -1;
    public static final int WIFI_MODE_AP = 0;
    public static final int WIFI_MODE_CLIENT = 1;
    public static final int WIFI_MODE_OFF = 2; //


    public static final int BT_STATE_UNKNOWN = -1;
    public static final int BT_STATE_DISABLED = 0;
    public static final int BT_STATE_ENABLED = 1;


    private boolean mIsConnected = false;
    private boolean mIsVdbConnected = false;


    private String mCameraName = new String();
    private String mFirmwareVersion = new String();
    private String mBspVersion = new String();
    private String mHardwareName;
    private int mApiVersion = 0;
    private String mApiVersionStr = new String();
    private String mBuild = new String();

    private int mMicState = STATE_MIC_UNKNOWN;
    private int mMicVol = -1;

    private int mSpeakerState = STATE_SPEAKER_UNKNOWN;
    private int mSpeakerVol = -1;

    private int mBatteryState = STATE_BATTERY_UNKNOWN;
    private int mPowerState = STATE_POWER_UNKNOWN;
    private int mBatteryVol = -1;

    private int mStorageState = STATE_STORAGE_UNKNOWN;
    private long mStorageTotalSpace = 0;
    private long mStorageFreeSpace = 0;

    private int mRecordState = STATE_RECORD_UNKNOWN;
    private int mRecordTime = 0;

    private int mOverlayFlags = 0;

    private int mVideoResolutionList = 0;
    private int mVideoResolutionIndex = VIDEO_RESOLUTION_UNKNOWN;

    private int mVideoQualityList = 0;
    private int mVideoQualityIndex = VIDEO_QUALITY_UNKNOWN;

    private int mRecordModeList = 0;
    private int mRecordModeIndex = REC_MODE_UNKNOWN;

    private int mColorModeList = 0;
    private int mColorModeIndex = COLOR_MODE_UNKNOWN;

    private int mMarkBeforeTime = -1;
    private int mMarkAfterTime = -1;

    private int mDisplayBrightness = 0;
    private String mAutoOffTime = null;
    private String mAutoPowerOffDelay = null;
    private String mScreenSaverStyle = null;

    private int mWifiMode = WIFI_MODE_UNKNOWN;
    public int mNumWifiAP = 0;


    private int mBtState = BT_STATE_UNKNOWN;
    private BtDevice mObdDevice = new BtDevice(BtDevice.BT_DEVICE_TYPE_OBD);
    private BtDevice mRemoteCtrlDevice = new BtDevice(BtDevice.BT_DEVICE_TYPE_REMOTE_CTR);


    private final ServiceInfo mServiceInfo;


    private InetSocketAddress mAddress;
    private InetSocketAddress mPreviewAddress;


    private WeakReference<OnScanHostListener> mOnScanHostListener;

    private List<BtDevice> mScannedBtDeviceList = new ArrayList<>();
    private int mScannedBtDeviceNumber = 0;

    private VdbRequestQueue mVdbRequestQueue;

    private EventBus mEventBus = EventBus.getDefault();

    private RxBus mRxBus = RxBus.getDefault();

    private VdtCameraCommunicationBus mCommunicationBus;


    private WeakReference<OnRawDataUpdateListener> mOnRawDataUpdateListener;

    public static class ServiceInfo {
        public String ssid;
        public final InetAddress inetAddr;
        public final int port;
        public final String serverName;
        public final String serviceName;
        public final boolean bPcServer;


        public ServiceInfo(InetAddress inetAddr, int port, String serverName, String serviceName, boolean bPcServer) {
            this.ssid = "";
            this.inetAddr = inetAddr;
            this.port = port;
            this.serverName = serverName;
            this.serviceName = serviceName;
            this.bPcServer = bPcServer;
        }

        @Override
        public String toString() {
            return ToStringUtils.getString(this);
        }
    }


    public interface OnConnectionChangeListener {
        void onConnected(VdtCamera vdtCamera);

        void onVdbConnected(VdtCamera vdtCamera);

        void onDisconnected(VdtCamera vdtCamera);
    }

    public interface OnNewFwVersionListern {
        void onNewVersion(int response);
    }


    private OnConnectionChangeListener mOnConnectionChangeListener = null;

    private OnNewFwVersionListern mOnNewFwVersionListerner = null;


    private VdbConnection mVdbConnection;

    public VdbConnection getVdbConnection() {
        return mVdbConnection;
    }


    public VdtCamera(VdtCamera.ServiceInfo serviceInfo, OnConnectionChangeListener listener) {
        mServiceInfo = serviceInfo;
        mOnConnectionChangeListener = listener;
        mAddress = new InetSocketAddress(serviceInfo.inetAddr, serviceInfo.port);
        mCommunicationBus = new VdtCameraCommunicationBus(mAddress, new VdtCameraCommunicationBus.ConnectionChangeListener() {
            @Override
            public void onConnected() {
                initCameraState();
                onCameraConnected();
            }

            @Override
            public void onDisconnected() {
                onCameraDisconnected();
            }
        }, new VdtCameraCommunicationBus.CameraMessageHandler() {
            @Override
            public void handleMessage(int code, String p1, String p2) {
                handleCameraMessage(code, p1, p2);
            }
        });

        mCommunicationBus.start();

    }


    public void setOnRawDataItemUpdateListener(OnRawDataUpdateListener listener) {
        mOnRawDataUpdateListener = new WeakReference<>(listener);
    }

    public void setCameraName(String name) {
        if (name == null || name.isEmpty()) {
            // use empty string for unnamed camera
            name = "No name";
        }
        if (!mCameraName.equals(name)) {
            Log.d(TAG, "setCameraName: " + name);
            mCameraName = name;

            mRxBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMERA_STATE_INFO, this));

        }
    }

    public String versionString() {
        int main = (mApiVersion >> 16) & 0xff;
        int sub = mApiVersion & 0xffff;
        return String.format(Locale.US, "%d.%d.%s", main, sub, mBuild);
    }

    public void setFirmwareVersion(String hardwareName, String bspVersion) {
        mHardwareName = hardwareName.substring(hardwareName.indexOf("@") + 1);
        if (!mBspVersion.equals(bspVersion)) {
//            Logger.t(TAG).d("setFirmwareVersion: " + version);
            mBspVersion = bspVersion;
        }
    }

    public String getApiVersion() {
        return mApiVersionStr;
    }

    public String getHardwareName() {
        return mHardwareName;
    }


    private int makeVersion(int main, int sub) {
        return (main << 16) | sub;
    }

    public void setApiVersion(int main, int sub, String build) {
        int version = makeVersion(main, sub);
        if (mApiVersion != version || !mBuild.equals(build)) {
//            Logger.t(TAG).d("setApiVersion: " + version);
            mApiVersion = version;
            mBuild = build;

        }
    }

    // API
    public boolean isPcServer() {
        return mServiceInfo.bPcServer;
    }


    public int getBatteryState() {
        return mBatteryState;
    }

    public int getBatteryVolume() {
        mCommunicationBus.sendCommand(CMD_CAM_MSG_BATTERY_INFOR);
        return mBatteryVol;
    }

    public int getStorageState() {
        return mStorageState;
    }

    public long getStorageTotalSpace() {
        return mStorageTotalSpace;
    }

    public long getStorageFreeSpace() {
        return mStorageFreeSpace;
    }

    public String getServerName() {
        return mServiceInfo.serverName;
    }

    public ServiceInfo getServerInfo() {
        return mServiceInfo;
    }

    public int getBtState() {
        return mBtState;
    }

    public BtDevice getObdDevice() {
        return mObdDevice;
    }

    public BtDevice getRemoteCtrlDevice() {
        return mRemoteCtrlDevice;
    }

    public void getIsBtEnabled() {
        mCommunicationBus.sendCommand(CMD_CAM_BT_IS_ENABLED);
    }




    public void setMicEnabled(boolean enabled) {
        int micState = enabled ? STATE_MIC_ON : STATE_MIC_OFF;
        int gain = 5;
        if (micState == STATE_MIC_ON) {
            gain = 5;
        }

        mCommunicationBus.sendCommand(CMD_AUDIO_SET_MIC, micState, gain);
    }

    public boolean isSpeakerOn() {
        return mSpeakerState == STATE_SPEAKER_ON;
    }

    public int getSpeakerVol() {
        return mSpeakerVol;
    }

    public void setSpeakerStatus(boolean on, int volume) {
        int speakerState = on ? STATE_SPEAKER_ON : STATE_SPEAKER_OFF;
        if (volume < 0) {
            volume = 0;
        }
        if (volume > 10) {
            volume = 10;
        }
        Log.d(TAG, "speakerState:" + speakerState);
        mCommunicationBus.sendCommand(CMD_SET_SPEAKER_STATUS, speakerState, volume);
    }

    public int getDisplayBrightness() {
        mCommunicationBus.sendCommand(CMD_GET_DISPLAY_BRIGHTNESS);
        return mDisplayBrightness;
    }

    public void setDisplayBrightness(int brightness) {
        Log.d(TAG, "display brightness:" + brightness);
        mCommunicationBus.sendCommand(CMD_SET_DISPLAY_BRIGHTNESS, brightness);
    }

    public int getOverlayState() {
        mCommunicationBus.sendCommand(CMD_REC_GET_OVERLAY_STATE);
        return mOverlayFlags;
    }

    public void setOverlayState(int overlayState) {
        mCommunicationBus.sendCommand(CMD_REC_SET_OVERLAY, overlayState);
        mOverlayFlags = overlayState;
    }

    public String getScreenSaverTime() {
        mCommunicationBus.sendCommand(CMD_GET_DISPLAY_AUTO_OFF_TIME);
        return mAutoOffTime;
    }

    public void setScreenSaver(String autoOffTime) {
        mAutoOffTime = autoOffTime;
        mCommunicationBus.sendCommand(CMD_SET_DISPLAY_AUTO_OFF_TIME, autoOffTime);
    }

    public String getAutoPowerOffDelay() {
        mCommunicationBus.sendCommand(CMD_GET_AUTO_POWER_OFF_DELAY);
        return mAutoPowerOffDelay;
    }

    public void setAutoPowerOffDelay(String autoPowerOffDelay) {
        Log.d(TAG, autoPowerOffDelay);
        mCommunicationBus.sendCommand(CMD_SET_AUTO_POWER_OFF_DELAY, autoPowerOffDelay);
        mAutoPowerOffDelay = autoPowerOffDelay;
    }

    public String getScreenSaverStyle() {
        Log.d(TAG, String.format("getScreenSaverStyle" + mScreenSaverStyle));
        mCommunicationBus.sendCommand(CMD_GET_SCREEN_SAVER_STYLE);
        return mScreenSaverStyle;
    }

    public void setScreenSaverStyle(String screenSaverStyle) {
        Log.d(TAG, screenSaverStyle);
        mCommunicationBus.sendCommand(CMD_SET_SCREEN_SAVER_STYLE, screenSaverStyle);
    }


    public void setWifiMode(int wifiMode) {
        mCommunicationBus.sendCommand(CMD_NETWORK_CONNECT_HOST, wifiMode);
    }

    public int getWifiMode() {
        mCommunicationBus.sendCommand(CMD_NETWORK_GET_WLAN_MODE);
        return mWifiMode;
    }

    public String getBspFirmware() {
        mCommunicationBus.sendCommand(CMD_FW_GET_VERSION);
        return mBspVersion;
    }

    public void sendNewFirmware(String md5, OnNewFwVersionListern listener) {
        mOnNewFwVersionListerner = listener;
        mCommunicationBus.sendCommand(CMD_FW_NEW_VERSION, md5);
    }

    public void upgradeFirmware() {
        mCommunicationBus.sendCommand(CMD_FW_DO_UPGRADE);
    }

    public VdbRequestQueue getRequestQueue() {
        return mVdbRequestQueue;
    }


    public InetAddress getAddress() {
        return mServiceInfo.inetAddr;
    }


    public String getSSID() {
        return mServiceInfo.ssid;
    }

    public String getHostString() {
        return mServiceInfo.inetAddr.getHostAddress();
    }


    public boolean idMatch(String ssid, String hostString) {
        if (ssid == null || hostString == null) {
            return false;
        }
        String myHostString = getHostString();
        if (mServiceInfo.ssid == null || myHostString == null) {
            return false;
        }
        return mServiceInfo.ssid.equals(ssid) && myHostString.equals(hostString);
    }


    public int getRecordState() {
        return mRecordState;
    }

    public int getVideoResolution() {
        mCommunicationBus.sendCommand(CMD_REC_GET_RESOLUTION);
        Log.d(TAG, "get video quality index: " + mVideoResolutionIndex);
        switch (mVideoResolutionIndex) {
            case VIDEO_RESOLUTION_1080P30:
            case VIDEO_RESOLUTION_1080P60:
                return VIDEO_RESOLUTION_1080P;
            default:
                return VIDEO_RESOLUTION_720P;
        }
    }


    public int getVideoResolutionFramerate() {
        mCommunicationBus.sendCommand(CMD_REC_GET_RESOLUTION);
        return mVideoResolutionIndex;
    }

    public String getVideoResolutionStr() {
        mCommunicationBus.sendCommand(CMD_REC_GET_RESOLUTION);
        Log.d(TAG, "" + mVideoResolutionIndex);
        switch (mVideoResolutionIndex) {
            case VIDEO_RESOLUTION_1080P30:
                return "1080p30";
            case VIDEO_RESOLUTION_1080P60:
                return "1080p60";
            case VIDEO_RESOLUTION_720P30:
                return "720p30";
            case VIDEO_RESOLUTION_720P60:
                return "720p60";
            case VIDEO_RESOLUTION_720P120:
                return "720p120";
            default:
                return "Unknown";
        }


    }

    public int getVideoFramerate() {
        mCommunicationBus.sendCommand(CMD_REC_GET_RESOLUTION);
        Log.d(TAG, "get video quality index: " + mVideoResolutionIndex);
        switch (mVideoResolutionIndex) {
            case VIDEO_RESOLUTION_1080P30:
            case VIDEO_RESOLUTION_4KP30:
            case VIDEO_RESOLUTION_480P30:
            case VIDEO_RESOLUTION_720P30:
                return VIDEO_FRAMERATE_30FPS;

            case VIDEO_RESOLUTION_1080P60:
            case VIDEO_RESOLUTION_720P60:
            case VIDEO_RESOLUTION_4KP60:
            case VIDEO_RESOLUTION_480P60:
                return VIDEO_FRAMERATE_60FPS;


            case VIDEO_RESOLUTION_720P120:
                return VIDEO_FRAMERATE_120FPS;
            default:
                return VIDEO_FRAMERATE_30FPS;

        }
    }

    public int getRecordMode() {
        return mRecordModeIndex;
    }


    public InetSocketAddress getInetSocketAddress() {
        return mAddress;
    }

    public InetSocketAddress getPreviewAddress() {
        return mPreviewAddress;
    }

    private void onCameraConnected() {
        InetSocketAddress addr = mAddress;
        if (addr != null) {
            mPreviewAddress = new InetSocketAddress(addr.getAddress(), 8081);
            if (mOnConnectionChangeListener != null) {
                mOnConnectionChangeListener.onConnected(this);
            }
        }
        mVdbConnection = new VdbConnection(getHostString());

        try {
            mVdbConnection.connect();
            mIsVdbConnected = true;
            VdbSocket vdbSocket = new BasicVdbSocket(getVdbConnection());
            mVdbRequestQueue = new VdbRequestQueue(vdbSocket);
            mVdbRequestQueue.start();
            if (mOnConnectionChangeListener != null) {
                mOnConnectionChangeListener.onVdbConnected(VdtCamera.this);
            }
            registerMessageHandler();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mIsConnected = true;
    }

    private void registerMessageHandler() {
        RawDataMsgHandler rawDataMsgHandler = new RawDataMsgHandler(new VdbResponse.Listener<List<RawDataItem>>() {
            @Override
            public void onResponse(List<RawDataItem> response) {
//                Logger.t(TAG).d("receive raw data item");
//                mEventBus.post(new RawDataItemEvent(VdtCamera.this, response));
                if (mOnRawDataUpdateListener != null) {
                    OnRawDataUpdateListener listener = mOnRawDataUpdateListener.get();
                    if (listener != null) {
                        listener.OnRawDataUpdate(VdtCamera.this, response);
                    }
                }
            }

        }, new VdbResponse.ErrorListener() {
            @Override
            public void onErrorResponse(SnipeError error) {
                Log.d(TAG, "RawDataMsgHandler ERROR", error);
            }
        });
        mVdbRequestQueue.registerMessageHandler(rawDataMsgHandler);

        ClipInfoMsgHandler clipInfoMsgHandler = new ClipInfoMsgHandler(
            new VdbResponse.Listener<ClipActionInfo>() {
                @Override
                public void onResponse(ClipActionInfo response) {
//                    Logger.t(TAG).e(response.toString());
                }
            },
            new VdbResponse.ErrorListener() {
                @Override
                public void onErrorResponse(SnipeError error) {
                    Log.d(TAG, "ClipInfoMsgHandler ERROR", error);
                }
            });
        mVdbRequestQueue.registerMessageHandler(clipInfoMsgHandler);

        MarkLiveMsgHandler markLiveMsgHandler = new MarkLiveMsgHandler(
            new VdbResponse.Listener<ClipActionInfo>() {
                @Override
                public void onResponse(ClipActionInfo response) {
                    mEventBus.post(new MarkLiveMsgEvent(VdtCamera.this, response));
                    Log.d(TAG, response.toString());
                }
            },
            new VdbResponse.ErrorListener() {
                @Override
                public void onErrorResponse(SnipeError error) {
                    Log.d(TAG, "MarkLiveMsgHandler ERROR", error);
                }
            });
        mVdbRequestQueue.registerMessageHandler(markLiveMsgHandler);

        VdbReadyMsgHandler vdbReadyMsgHandler = new VdbReadyMsgHandler(
            new VdbResponse.Listener<Object>() {
                @Override
                public void onResponse(Object response) {
                    mEventBus.post(new VdbReadyInfo(true));
                    Log.d(TAG, "handling vdbReadyMsg");
                }
            },
            new VdbResponse.ErrorListener() {
                @Override
                public void onErrorResponse(SnipeError error) {
                    Log.e(TAG, "VdbReadyMsgHandler ERROR" + error);
                }
            });
        mVdbRequestQueue.registerMessageHandler(vdbReadyMsgHandler);

        VdbUnmountedMsgHandler vdbUnmountedMsgHandler = new VdbUnmountedMsgHandler(
            new VdbResponse.Listener<Object>() {
                @Override
                public void onResponse(Object response) {
                    mEventBus.post(new VdbReadyInfo(false));
                    Log.d(TAG, "handling vdbUnmountedMsg");
                }
            },
            new VdbResponse.ErrorListener() {
                @Override
                public void onErrorResponse(SnipeError error) {
                    Log.e(TAG, "VdbReadyMsgHandler ERROR");
                }
            });
        mVdbRequestQueue.registerMessageHandler(vdbUnmountedMsgHandler);

    }

    public void registerRawDataItemMsgHandler() {
        Log.d(TAG, "registerRawDataItemMsgHandler");

    }

    public void unregisterRawDataItemMagHandler() {
        Log.d(TAG, "unregisterRawDataItemMagHandler");
        if (mVdbRequestQueue != null) {
            mVdbRequestQueue.unregisterMessageHandler(VdbCommand.Factory.MSG_RawData);
        }
    }

    private void onCameraDisconnected() {

//        mVdbRequestQueue.stop();
        if (mVdbRequestQueue != null) {
            mVdbRequestQueue.unregisterMessageHandler(VdbCommand.Factory.MSG_RawData);
            mVdbRequestQueue.unregisterMessageHandler(VdbCommand.Factory.MSG_ClipInfo);
            mVdbRequestQueue.unregisterMessageHandler(VdbCommand.Factory.VDB_MSG_MarkLiveClipInfo);
        }

        if (mOnConnectionChangeListener != null) {
            mOnConnectionChangeListener.onDisconnected(this);
        }
        mIsConnected = false;
    }


    private void initCameraState() {
        mCommunicationBus.sendCommand(CMD_CAM_GET_API_VERSION);
        mCommunicationBus.sendCommand(CMD_FW_GET_VERSION);

        mCommunicationBus.sendCommand(CMD_CAM_GET_NAME);
        mCommunicationBus.sendCommand(CMD_CAM_MSG_MIC_INFOR);
        mCommunicationBus.sendCommand(CMD_REC_GET_REC_MODE);
        mCommunicationBus.sendCommand(CMD_REC_GET_RESOLUTION);
        mCommunicationBus.sendCommand(CMD_REC_LIST_RESOLUTIONS);
        mCommunicationBus.sendCommand(CMD_CAM_GET_GET_ALL_INFOR);
        mCommunicationBus.sendCommand(CMD_CAM_GET_STATE);
        mCommunicationBus.sendCommand(CMD_NETWORK_GET_WLAN_MODE);
        mCommunicationBus.sendCommand(CMD_NETWORK_GET_HOST_NUM);
        mCommunicationBus.sendCommand(CMD_REC_GET_MARK_TIME);
        mCommunicationBus.sendCommand(CMD_CAM_MSG_BATTERY_INFOR);
        mCommunicationBus.sendCommand(CMD_GET_SPEAKER_STATUS);
        mCommunicationBus.sendCommand(CMD_GET_DISPLAY_BRIGHTNESS);
        mCommunicationBus.sendCommand(CMD_GET_DISPLAY_AUTO_OFF_TIME);
        mCommunicationBus.sendCommand(CMD_REC_GET_OVERLAY_STATE);
        mCommunicationBus.sendCommand(CMD_GET_AUTO_POWER_OFF_DELAY);
        mCommunicationBus.sendCommand(CMD_GET_SCREEN_SAVER_STYLE);
        long timeMillis = System.currentTimeMillis();
        int timeZone = TimeZone.getDefault().getRawOffset();


        mCommunicationBus.sendCommand(CMD_NETWORK_SYNCTIME, ((Long) (timeMillis / 1000)).toString(),
            ((Integer) (timeZone / (3600 * 1000))).toString());

        mCommunicationBus.sendCommand(CMD_CAM_BT_IS_ENABLED);
        mCommunicationBus.sendCommand(CMD_CAM_BT_GET_DEV_STATUS, BtDevice.BT_DEVICE_TYPE_REMOTE_CTR);
        mCommunicationBus.sendCommand(CMD_CAM_BT_GET_DEV_STATUS, BtDevice.BT_DEVICE_TYPE_OBD);

    }


    public void setBtEnable(boolean enable) {
        mCommunicationBus.sendCommand(CMD_CAM_BT_ENABLE, enable ? 1 : 0);
        Log.d(TAG, "sent CMD_CAM_BT_ENABLE");
    }

    public void scanBluetoothDevices() {
        mCommunicationBus.sendCommand(CMD_CAM_BT_DO_SCAN);
    }

    public void getBtHostNumber() {
        mCommunicationBus.sendCommand(CMD_CAM_BT_GET_HOST_NUM);
    }


    public void doBtUnbind(int type, String mac) {
        Log.d(TAG, "cmd_CAM_BT_doUnBind, type=" + type + ", mac=" + mac);
        mCommunicationBus.sendCommand(CMD_CAM_BT_DO_UNBIND, Integer.toString(type), mac);

    }

    public void doBind(int type, String mac) {
        Log.d(TAG, "cmd_CAM_BT_doBind, type=" + type + ", mac=" + mac);
        mCommunicationBus.sendCommand(CMD_CAM_BT_DO_BIND, Integer.toString(type), mac);
    }


    public void setName(String name) {
        mCommunicationBus.sendCommand(CMD_CAM_SET_NAME, name);
    }

    public String getName() {
        mCommunicationBus.sendCommand(CMD_CAM_GET_NAME);
        return mCameraName;
    }

    public void setVideoResolution(int resolution, int frameRate) {
        int videoResulution = VIDEO_RESOLUTION_1080P30;
        if (resolution == VIDEO_RESOLUTION_1080P) {
            switch (frameRate) {
                case VIDEO_FRAMERATE_30FPS:
                    videoResulution = VIDEO_RESOLUTION_1080P30;
                    break;
                case VIDEO_FRAMERATE_60FPS:
                    videoResulution = VIDEO_RESOLUTION_1080P60;
                    break;
            }
        } else if (resolution == VIDEO_RESOLUTION_720P) {
            switch (frameRate) {
                case VIDEO_FRAMERATE_30FPS:
                    videoResulution = VIDEO_RESOLUTION_720P30;
                    break;
                case VIDEO_FRAMERATE_60FPS:
                    videoResulution = VIDEO_RESOLUTION_720P60;
                    break;

            }
        }
        Log.d(TAG, "set video resolution: " + videoResulution);
        setVideoResolution(videoResulution);
    }

    public void setVideoResolution(int resolutionIndex) {
        mVideoResolutionIndex = resolutionIndex;
        mCommunicationBus.sendCommand(CMD_REC_SET_RESOLUTION, resolutionIndex);
    }


    public void setVideoQuality(int qualityIndex) {
        mCommunicationBus.sendCommand(CMD_REC_SET_QUALITY, qualityIndex);
    }

    public void getRecordQuality() {
        mCommunicationBus.sendCommand(CMD_REC_GET_QUALITY);

    }

    public void setRecordColorMode(int index) {
        mCommunicationBus.sendCommand(CMD_REC_SET_COLOR_MODE, index);
    }

    public void getRecordColorMode() {
        mCommunicationBus.sendCommand(CMD_REC_GET_COLOR_MODE);
    }

    public void setRecordRecMode(int flags) {
        mCommunicationBus.sendCommand(CMD_REC_SET_REC_MODE, flags);
    }

    public void getRecordRecMode() {
        mCommunicationBus.sendCommand(CMD_REC_GET_REC_MODE);
    }

    public void setAudioMic(boolean isOn, int vol) {
        int state = isOn ? STATE_MIC_ON : STATE_MIC_OFF;
        mCommunicationBus.sendCommand(CMD_AUDIO_SET_MIC, state, vol);
    }

    public void getAudioMicState() {
        mCommunicationBus.sendCommand(CMD_AUDIO_GET_MIC_STATE);
    }


    public void scanHost(OnScanHostListener listener) {
        mOnScanHostListener = new WeakReference<OnScanHostListener>(listener);
        mCommunicationBus.sendCommand(CMD_NETWORK_SCANHOST);
    }

    public void getSetup() {
        mCommunicationBus.sendCommand(USER_CMD_GET_SETUP);
    }


    public void startPreview() {
        mCommunicationBus.sendCommand(CMD_CAM_WANT_PREVIEW);
    }

    public int getRecordTime() {
        mCommunicationBus.sendCommand(CMD_CAM_GET_TIME);
        return mRecordTime;
    }

    public void getRecordResolutionList() {
        mCommunicationBus.sendCommand(CMD_REC_LIST_RESOLUTIONS);
    }

    public void markLiveVideo() {
        mCommunicationBus.sendCommand(CMD_REC_MARK_LIVE_VIDEO);
    }

    public int getMarkBeforeTime() {
        return mMarkBeforeTime;
    }

    public int getMarkAfterTime() {
        return mMarkAfterTime;
    }

    public void setMarkTime(int before, int after) {
        if (before < 0 || after < 0) {
            return;
        }
        mCommunicationBus.sendCommand(CMD_REC_SET_MARK_TIME, before, before);
    }

    public void stopRecording() {
        mCommunicationBus.sendCommand(CMD_CAM_STOP_REC);
    }

    public void startRecording() {
        mCommunicationBus.sendCommand(CMD_CAM_START_REC);
    }


    public void getNetworkHostHum() {
        mCommunicationBus.sendCommand(CMD_NETWORK_GET_HOST_NUM);
    }

    public void setNetworkRmvHost(String ssid) {
        mCommunicationBus.sendCommand(CMD_NETWORK_RMV_HOST, ssid, "");
        mCommunicationBus.sendCommand(CMD_NETWORK_GET_HOST_NUM);
    }

    public void addNetworkHost(String ssid, String password) {
        mCommunicationBus.sendCommand(CMD_NETWORK_ADD_HOST, ssid, password);
        mCommunicationBus.sendCommand(CMD_NETWORK_GET_HOST_NUM);
    }


    public void connectNetworkHost(String ssid) {
        if (ssid == null) {
            ssid = "";
        }
        mCommunicationBus.sendCommand(CMD_NETWORK_CONNECTHOTSPOT, ssid);

    }



    public boolean isMicEnabled() {
        return mMicState == STATE_MIC_ON;
    }

    public static class StorageInfo {
        public int totalSpace;
        public int freeSpace;
    }

    public StorageInfo getStorageInfo() {
        StorageInfo storageInfo = new StorageInfo();
        storageInfo.totalSpace = (int) (getStorageTotalSpace() / 1024);
        storageInfo.freeSpace = (int) (getStorageFreeSpace() / 1024);
        return storageInfo;
    }


    private void ack_Cam_getApiVersion(String p1) {
        mApiVersionStr = p1;
        int main = 0, sub = 0;
        String build = "";
        int i_main = p1.indexOf('.', 0);
        if (i_main >= 0) {
            String t = p1.substring(0, i_main);
            main = Integer.parseInt(t);
            i_main++;
            int i_sub = p1.indexOf('.', i_main);
            if (i_sub >= 0) {
                t = p1.substring(i_main, i_sub);
                sub = Integer.parseInt(t);
                i_sub++;
                build = p1.substring(i_sub);
            }
        }
        setApiVersion(main, sub, build);
    }


    private void ack_Cam_get_Name_result(String p1, String p2) {
        setCameraName(p1);
    }


    private void ack_Cam_get_State_result(String p1, String p2) {
        int state = Integer.parseInt(p1);
        boolean is_still = p2.length() > 0 ? Integer.parseInt(p2) != 0 : false;
        if (mRecordState != state) {
            mRxBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMERA_STATE_REC, VdtCamera.this, null));
            mRecordState = state;
        }
    }


    private void ack_Cam_get_time_result(String p1, String p2) {
        int duration = Integer.parseInt(p1);

        if (mRecordTime != duration) {
            mRxBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMERA_STATE_REC_DURATION, VdtCamera.this, duration));
            mRecordTime = duration;
        }


    }


    private void ack_Cam_msg_Storage_infor(String p1, String p2) {
        mStorageState = Integer.parseInt(p1);
    }

    private void ack_Cam_msg_StorageSpace_infor(String p1, String p2) {
        long totalSpace = p1.length() > 0 ? Long.parseLong(p1) : 0;
        long freeSpace = p2.length() > 0 ? Long.parseLong(p2) : 0;

        mStorageTotalSpace = totalSpace;
        mStorageFreeSpace = freeSpace;
    }


    private void ack_Cam_msg_Battery_infor(String p1, String p2) {
//        Logger.t(TAG).d("ack_Cam_msg_Battery_infor: " + p1 + " p2: " + p2);
        int vol = Integer.parseInt(p2);
        mBatteryVol = vol;
    }

    private void ack_Cam_msg_power_infor(String p1, String p2) {
        if (p1.length() == 0 || p2.length() == 0) {
            Log.d(TAG, "bad power info, schedule update");

        } else {
            int batteryState = STATE_BATTERY_UNKNOWN;
            if (p1.equals("Full")) {
                batteryState = STATE_BATTERY_FULL;
            } else if (p1.equals("Not charging")) {
                batteryState = STATE_BATTERY_NOT_CHARGING;
            } else if (p1.equals("Discharging")) {
                batteryState = STATE_BATTERY_DISCHARGING;
            } else if (p1.equals("Charging")) {
                batteryState = STATE_BATTERY_CHARGING;
            }
            int powerState = Integer.parseInt(p2);
            mBatteryState = batteryState;
            mPowerState = powerState;
        }
    }


    private void ack_Cam_msg_GPS_infor(String p1, String p2) {
        int state = Integer.parseInt(p1);

    }

    private void ack_Cam_msg_Mic_infor(String p1, String p2) {
        Log.d(TAG, "get mic state: p1: " + p1 + " p2: " + p2);
        int state = Integer.parseInt(p1);
        int vol = Integer.parseInt(p2);
        mMicState = state;
        mMicVol = vol;
        mRxBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMEAR_STATE_MICROPHONE_STATUS_CHANGED, this));
    }


    private void ack_Network_GetWLanMode(String p1, String p2) {
        int mode = Integer.parseInt(p1);
        mWifiMode = mode;
    }


    private void ack_Network_GetHostNum(String p1, String p2) {
        int num = Integer.parseInt(p1);
        mNumWifiAP = num;
        for (int i = 0; i < num; i++) {
            mCommunicationBus.sendCommand(CMD_DOMAIN_CAM, CMD_NETWORK_GET_HOST_INFOR, i);
        }
    }


    private void ack_Network_GetHostInfor(String p1, String p2) {
    }

    private void ack_Rec_error(String p1, String p2) {
        int error = Integer.parseInt(p1);
        mEventBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMERA_STATE_REC_ERROR, VdtCamera.this, error));
    }


    private void ack_fw_getVersion(String p1, String p2) {
//        Logger.t(TAG).d("ack get firmware p1: " + p1 + " P2: " + p2);
        setFirmwareVersion(p1, p2);
    }


    private void ack_CAM_BT_isEnabled(String p1) {
        int enabled = Integer.parseInt(p1);
        mBtState = enabled;
//        Logger.t(TAG).d("ack CAM BT isEnabled: "+ enabled);
        if (enabled == BT_STATE_ENABLED) {
            mCommunicationBus.sendCommand(CMD_CAM_BT_GET_DEV_STATUS, BtDevice.BT_DEVICE_TYPE_REMOTE_CTR);
            mCommunicationBus.sendCommand(CMD_CAM_BT_GET_DEV_STATUS, BtDevice.BT_DEVICE_TYPE_OBD);
        }
    }


    private void ack_CAM_BT_getDEVStatus(String p1, String p2) {
        int i_p1 = Integer.parseInt(p1);
        Log.d(TAG, "i_p1: " + p1);
        int devType = i_p1 >> 8;
        int devState = i_p1 & 0xff;
        String mac = "";
        String name = "";
        int index = p2.indexOf('#');
        if (index >= 0) {
            mac = p2.substring(0, index);
            name = p2.substring(index + 1);
        }
        if (mac.equals("NA")) {
            //cmd_CAM_BT_getDEVStatus(dev_type);
            //return;
            devState = BtDevice.BT_DEVICE_STATE_OFF;
            mac = "";
            name = "";
        }

        Logger.t(TAG).d("bt devide type: " + devType + " dev_state " + devState + " mac: " + mac + " name " + name);
        if (BtDevice.BT_DEVICE_TYPE_OBD == devType) {
            mObdDevice.setDevState(devState, mac, name);
        } else if (BtDevice.BT_DEVICE_TYPE_REMOTE_CTR == devType) {
            mRemoteCtrlDevice.setDevState(devState, mac, name);
        }
        mRxBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMERA_STATE_BT_DEVICE_STATUS_CHANGED, this));

    }


    private void ack_CAM_BT_getHostNum(String p1) {
        int numDevs = Integer.parseInt(p1);
        if (numDevs < 0) {
            numDevs = 0;
        }
        mScannedBtDeviceNumber = numDevs;
        Log.d(TAG, "find devices: " + mScannedBtDeviceNumber);
        mScannedBtDeviceList.clear();
        for (int i = 0; i < numDevs; i++) {
            mCommunicationBus.sendCommand(CMD_CAM_BT_GET_HOST_INFOR, i);
        }
    }


    private void ack_CAM_BT_getHostInfor(String name, String mac) {
        int type;
        if (name.indexOf("OBD") >= 0) {
            type = BtDevice.BT_DEVICE_TYPE_OBD;
        } else if (name.indexOf("RC") >= 0) {
            type = BtDevice.BT_DEVICE_TYPE_REMOTE_CTR;
        } else {
            type = BtDevice.BT_DEVICE_TYPE_OTHER;
        }

        Log.d(TAG, "type: " + type + " mac: " + mac + " name: " + name);
        BtDevice device = new BtDevice(type);
        device.setDevState(BtDevice.BT_DEVICE_STATE_UNKNOWN, mac, name);

        mScannedBtDeviceList.add(device);
        if (mScannedBtDeviceList.size() == mScannedBtDeviceNumber) {
            mEventBus.post(new BluetoothEvent(BluetoothEvent.BT_SCAN_COMPLETE, mScannedBtDeviceList));
        }

    }


    private void ack_CAM_BT_doScan(String p1) {
        int ret = Integer.parseInt(p1);
        Log.d(TAG, "ret: " + ret);
        if (ret == 0) {
            mCommunicationBus.sendCommand(CMD_CAM_BT_GET_HOST_NUM);
        }
    }


    private void ack_CAM_BT_doBind(String p1, String p2) {
        int type = Integer.parseInt(p1);
        int result = Integer.parseInt(p2);
        if (result == 0) {
            if (type == BtDevice.BT_DEVICE_TYPE_REMOTE_CTR || type == BtDevice.BT_DEVICE_TYPE_OBD) {
                mCommunicationBus.sendCommand(CMD_CAM_BT_GET_DEV_STATUS, type);
            }
        }
        Log.d(TAG, "ack_CAM_BT_doBind" + "type:" + type + ", result:" + result);
        mEventBus.post(new BluetoothEvent(BluetoothEvent.BT_DEVICE_BIND_FINISHED));
    }


    private void ack_CAM_BT_doUnBind(String p1, String p2) {
        int type = Integer.parseInt(p1);
        if (type == BtDevice.BT_DEVICE_TYPE_REMOTE_CTR || type == BtDevice.BT_DEVICE_TYPE_OBD) {
            mCommunicationBus.sendCommand(CMD_CAM_BT_GET_DEV_STATUS, type);
        }
        Log.d(TAG, "ack_CAM_BT_doUnBind" + "type:" + type);
        mEventBus.post(new BluetoothEvent(BluetoothEvent.BT_DEVICE_UNBIND_FINISHED));
    }


    private void ack_Rec_List_Resolutions(String p1, String p2) {
        int list = Integer.parseInt(p1);
        mVideoResolutionList = list;
    }


    private void ack_Rec_get_Resolution(String p1, String p2) {
        int index = Integer.parseInt(p1);
//            Logger.t(TAG).d("set video resolution index: " + index);
        mVideoResolutionIndex = index;
    }


    private void ack_Rec_List_Qualities(String p1, String p2) {
        int list = Integer.parseInt(p1);
        mVideoQualityList = list;
    }


    private void ack_Rec_get_Quality(String p1, String p2) {
        int index = Integer.parseInt(p1);

        mVideoQualityIndex = index;
    }


    private void ack_Rec_List_RecModes(String p1, String p2) {
        int list = Integer.parseInt(p1);
        mRecordModeList = list;
    }


    private void ack_Rec_get_RecMode(String p1, String p2) {
        int index = Integer.parseInt(p1);
        if (mRecordModeIndex != index) {
            mEventBus.post(new CameraStateChangeEvent(CameraStateChangeEvent.CAMERA_STATE_REC, VdtCamera.this, null));
            mRecordModeIndex = index;
        }
    }


    private void ack_Rec_List_ColorModes(String p1, String p2) {
        int list = Integer.parseInt(p1);
        mColorModeList = list;
    }


    private void ack_Rec_get_ColorMode(String p1, String p2) {
        int index = Integer.parseInt(p1);
        mColorModeIndex = index;
    }


    private void ack_Rec_getOverlayState(String p1, String p2) {
        int flags = Integer.parseInt(p1);
        mOverlayFlags = 2 & flags;
    }

    private void ack_Rec_setOverlayState(String p1, String p2) {
        Log.d(TAG, String.format("cmd_setOverlayState: p1: %s, p2: %s", p1, p2));
        int flags = Integer.parseInt(p1);
        //mOverlayFlags = 2 & flags;
    }


    private void ack_Rec_GetMarkTime(String p1, String p2) {
//            Logger.t(TAG).d(String.format("cmd_Rec_GetMarkTime: p1: %s, p2: %s", p1, p2));
        try {
            mMarkBeforeTime = Integer.parseInt(p1);
            mMarkAfterTime = Integer.parseInt(p2);
        } catch (Exception e) {
            Log.d(TAG, String.format("cmd_Rec_GetMarkTime: p1: %s, p2: %s", p1, p2), e);
        }
    }


    private void ack_Rec_SetMarkTime(String p1, String p2) {
        Log.d(TAG, String.format("ack_Rec_SetMarkTime: p1: %s, p2: %s", p1, p2));
        try {
            mMarkBeforeTime = Integer.parseInt(p1);
            mMarkAfterTime = Integer.parseInt(p2);
        } catch (Exception e) {
            Log.d(TAG, String.format("ack_Rec_SetMarkTime: p1: %s, p2: %s", p1, p2), e);
        }
    }

    private void ack_CAM_getSpeakerStatus(String p1, String p2) {
        Log.d(TAG, String.format("ack_CAM_getSpeakerStatus: p1: %s, p2: %s", p1, p2));
        try {
            int speakerState = Integer.parseInt(p1);
            int speakerVol = Integer.parseInt(p2);
            mSpeakerState = speakerState;
            mSpeakerVol = speakerVol;
        } catch (Exception e) {
            Log.e(TAG, String.format("ack_CAM_getSpeakerStatus: p1: %s, p2: %s", p1, p2));
        }
    }

    private void ack_setDisplayBrightness(String p1, String p2) {
        try {
            mDisplayBrightness = Integer.parseInt(p1);
        } catch (Exception e) {
            Log.d(TAG, String.format("cmd_set_display_brightness: p1: %s, p2: %s", p1, p2), e);
        }
    }


    private void ack_getDisplayBrightness(String p1, String p2) {
        Log.d(TAG, String.format("ack_get_display_brightness: p1: %s, p2: %s", p1, p2));
        try {
            mDisplayBrightness = Integer.parseInt(p1);
        } catch (Exception e) {
            Log.d(TAG, String.format("ack_get_display_brightness: p1: %s, p2: %s", p1, p2), e);
        }
    }

    private void ack_setDisplayAutoOffTime(String p1, String p2) {
        try {
            mAutoOffTime = p1;
        } catch (Exception e) {
            Log.d(TAG, String.format("cmd_set_display_auto_off_time: p1: %s, p2: %s", p1, p2), e);
        }
    }


    private void ack_getDisplayAutoOffTime(String p1, String p2) {
        Log.d(TAG, String.format("ack_get_display_auto_off_time: p1: %s, p2: %s", p1, p2));
        try {
            mAutoOffTime = p1;
        } catch (Exception e) {
            Log.d(TAG, String.format("ack_get_display_auto_off_time: p1: %s, p2: %s", p1, p2), e);
        }
    }

    private void ack_setAutoPowerOffDelay(String p1, String p2) {
        Log.d(TAG, String.format("%s, %s", p1, p2));
        try {
            mAutoPowerOffDelay = p1;
        } catch (Exception e) {
            Log.d(TAG, String.format("%s, %s", p1, p2), e);
        }
    }

    private void ack_getAutoPowerOffDelay(String p1, String p2) {
        Log.d(TAG, String.format("%s, %s", p1, p2));
        try {
            mAutoPowerOffDelay = p1;
        } catch (Exception e) {
            Log.d(TAG, String.format("%s, %s", p1, p2), e);
        }
    }

    private void ack_setScreenSaverStyle(String p1, String p2) {
        Log.d(TAG, String.format("%s, %s", p1, p2));
        try {
        } catch (Exception e) {
            Log.d(TAG, String.format("%s, %s", p1, p2), e);
        }
    }

    private void ack_getScreenSaverStyle(String p1, String p2) {
        Log.d(TAG, String.format("%s, %s", p1, p2));
        try {
            mScreenSaverStyle = p1;
        } catch (Exception e) {
            Log.d(TAG, String.format("%s, %s", p1, p2), e);
        }
    }


    private void handleCameraMessage(int cmd, String p1, String p2) {
        switch (cmd) {
            case CMD_CAM_GET_API_VERSION:
                ack_Cam_getApiVersion(p1);
                break;
            case CMD_CAM_GET_NAME_RESULT:
                ack_Cam_get_Name_result(p1, p2);
                break;
            case CMD_CAM_GET_STATE_RESULT:
                ack_Cam_get_State_result(p1, p2);
                break;
            case CMD_CAM_GET_TIME_RESULT:
                ack_Cam_get_time_result(p1, p2);
                break;
            case CMD_CAM_MSG_STORAGE_INFOR:
                ack_Cam_msg_Storage_infor(p1, p2);
                break;
            case CMD_CAM_MSG_STORAGE_SPACE_INFOR:
                ack_Cam_msg_StorageSpace_infor(p1, p2);
                break;
            case CMD_CAM_MSG_BATTERY_INFOR:
                ack_Cam_msg_Battery_infor(p1, p2);
                break;
            case CMD_CAM_MSG_POWER_INFOR:
                ack_Cam_msg_power_infor(p1, p2);
                break;
            case CMD_CAM_MSG_GPS_INFOR:
                ack_Cam_msg_GPS_infor(p1, p2);
                break;
            case CMD_CAM_MSG_MIC_INFOR:
                ack_Cam_msg_Mic_infor(p1, p2);
                break;
            case CMD_NETWORK_GET_WLAN_MODE:
                ack_Network_GetWLanMode(p1, p2);
                break;
            case CMD_NETWORK_GET_HOST_NUM:
                ack_Network_GetHostNum(p1, p2);
                break;
            case CMD_NETWORK_GET_HOST_INFOR:
                ack_Network_GetHostInfor(p1, p2);
                break;
            case CMD_NETWORK_ADD_HOST:
                handleOnNetworkAddHost(p1, p2);
                break;
            case CMD_NETWORK_CONNECT_HOST:
                handleOnNetworkConnectHost(p1, p2);
                break;
            case CMD_NETWORK_SCANHOST:
                handleNetWorkScanHostResult(p1, p2);
                break;
            case CMD_NETWORK_CONNECTHOTSPOT:
                handleNetworkConnectHost(p1, p2);
                break;
            case CMD_REC_ERROR:
                ack_Rec_error(p1, p2);
                break;
            case CMD_FW_GET_VERSION:
                ack_fw_getVersion(p1, p2);
                break;
            case CMD_FW_NEW_VERSION:
                handleNewFwVersion(p1, p2);
                break;
            case CMD_CAM_BT_IS_ENABLED:
                ack_CAM_BT_isEnabled(p1);
                break;
            case CMD_CAM_BT_GET_DEV_STATUS:
                ack_CAM_BT_getDEVStatus(p1, p2);
                break;
            case CMD_CAM_BT_GET_HOST_NUM:
                ack_CAM_BT_getHostNum(p1);
                break;
            case CMD_CAM_BT_GET_HOST_INFOR:
                ack_CAM_BT_getHostInfor(p1, p2);
                break;
            case CMD_CAM_BT_DO_SCAN:
                ack_CAM_BT_doScan(p1);
                break;
            case CMD_CAM_BT_DO_BIND:
                ack_CAM_BT_doBind(p1, p2);
                break;
            case CMD_CAM_BT_DO_UNBIND:
                ack_CAM_BT_doUnBind(p1, p2);
                break;
            case CMD_GET_SPEAKER_STATUS:
                ack_CAM_getSpeakerStatus(p1, p2);
                break;
            case CMD_REC_LIST_RESOLUTIONS:
                ack_Rec_List_Resolutions(p1, p2);
                break;
            case CMD_REC_GET_RESOLUTION:
                ack_Rec_get_Resolution(p1, p2);
                break;
            case CMD_REC_LIST_QUALITIES:
                ack_Rec_List_Qualities(p1, p2);
                break;
            case CMD_REC_GET_QUALITY:
                ack_Rec_get_Quality(p1, p2);
                break;
            case CMD_REC_LIST_REC_MODES:
                ack_Rec_List_RecModes(p1, p2);
                break;
            case CMD_REC_GET_REC_MODE:
                ack_Rec_get_RecMode(p1, p2);
                break;
            case CMD_REC_LIST_COLOR_MODES:
                ack_Rec_List_ColorModes(p1, p2);
                break;
            case CMD_REC_GET_COLOR_MODE:
                ack_Rec_get_ColorMode(p1, p2);
                break;
            case CMD_REC_GET_OVERLAY_STATE:
                ack_Rec_getOverlayState(p1, p2);
                break;
            case CMD_REC_SET_OVERLAY:
                ack_Rec_setOverlayState(p1, p2);
                break;
            case CMD_REC_GET_MARK_TIME:
                ack_Rec_GetMarkTime(p1, p2);
                break;
            case CMD_REC_SET_MARK_TIME:
                ack_Rec_SetMarkTime(p1, p2);
                break;
            case CMD_GET_DISPLAY_BRIGHTNESS:
                ack_getDisplayBrightness(p1, p2);
                break;
            case CMD_SET_DISPLAY_BRIGHTNESS:
                ack_setDisplayBrightness(p1, p2);
                break;
            case CMD_GET_DISPLAY_AUTO_OFF_TIME:
                ack_getDisplayAutoOffTime(p1, p2);
                break;
            case CMD_SET_DISPLAY_AUTO_OFF_TIME:
                ack_setDisplayAutoOffTime(p1, p2);
                break;
            case CMD_GET_AUTO_POWER_OFF_DELAY:
                ack_getAutoPowerOffDelay(p1, p2);
                break;
            case CMD_SET_AUTO_POWER_OFF_DELAY:
                ack_setAutoPowerOffDelay(p1, p2);
                break;
            case CMD_GET_SCREEN_SAVER_STYLE:
                ack_getScreenSaverStyle(p1, p2);
                break;
            case CMD_SET_SCREEN_SAVER_STYLE:
                ack_setScreenSaverStyle(p1, p2);
                break;
            default:
                //Logger.t(TAG).d("ack " + cmd + " not handled, p1=" + p1 + ", p2=" + p2);
                break;

        }
    }

    private void handleNewFwVersion(String p1, String p2) {
        Log.d(TAG, "p1: " + p1 + " p2: " + p2);
        if (mOnNewFwVersionListerner != null) {
            mOnNewFwVersionListerner.onNewVersion(Integer.valueOf(p1));
        }
    }

    private void handleOnNetworkConnectHost(String p1, String p2) {
        Log.d(TAG, "p1: " + p1 + " p2: " + p2);
    }

    private void handleNetworkConnectHost(String p1, String p2) {
        Log.d(TAG, "p1: " + p1 + " p2: " + p2);

        mEventBus.post(new NetworkEvent(NetworkEvent.NETWORK_EVENT_WHAT_CONNECTED, Integer.parseInt(p1)));
    }

    private void handleOnNetworkAddHost(String p1, String p2) {
//        Logger.t(TAG).d("p1: " + p1 + " p2: " + p2);

        mEventBus.post(new NetworkEvent(NetworkEvent.NETWORK_EVENT_WHAT_ADDED));
    }

    private void handleNetWorkScanHostResult(String p1, String p2) {
        if (p1 == null || p1.isEmpty()) {
            return;
        }
//        Logger.t(TAG).json(p1);
        List<NetworkItemBean> networkItemBeanList = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(p1);
            JSONArray networks = object.getJSONArray("networks");
            for (int i = 0; i < networks.length(); i++) {
                JSONObject networkObject = networks.getJSONObject(i);
                NetworkItemBean networkItem = new NetworkItemBean();
                networkItem.ssid = networkObject.optString("ssid");
                networkItem.bssid = networkObject.optString("bssid");
                networkItem.flags = networkObject.optString("flags");
                networkItem.frequency = networkObject.optInt("frequency");
                networkItem.signalLevel = networkObject.optInt("signal_level");
                boolean added = networkObject.optBoolean("added");
                if (added) {
                    networkItem.status = NetworkItemBean.CONNECT_STATUS_SAVED;
                }
                networkItemBeanList.add(networkItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(networkItemBeanList, new Comparator<NetworkItemBean>() {
            @Override
            public int compare(NetworkItemBean itemBean, NetworkItemBean t1) {
                return t1.signalLevel - itemBean.signalLevel;
            }
        });

        if (mOnScanHostListener != null) {
            OnScanHostListener listener = mOnScanHostListener.get();
            if (listener != null) {
                listener.OnScanHostResult(networkItemBeanList);
            }
        }

    }


    public interface OnRawDataUpdateListener {
        void OnRawDataUpdate(VdtCamera camera, List<RawDataItem> item);
    }

    public interface OnScanHostListener {
        void OnScanHostResult(List<NetworkItemBean> networkList);
    }
}
