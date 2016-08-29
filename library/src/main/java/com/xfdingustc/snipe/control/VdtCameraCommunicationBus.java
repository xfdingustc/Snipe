package com.xfdingustc.snipe.control;


import android.util.Log;


import com.xfdingustc.snipe.control.mina.VdtCodecFactory;
import com.xfdingustc.snipe.control.mina.VdtCommand;
import com.xfdingustc.snipe.control.mina.VdtMessage;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * Created by Xiaofei on 2016/6/1.
 */
class VdtCameraCommunicationBus implements VdtCameraCmdConsts {
    private static final String TAG = VdtCameraCommunicationBus.class.getSimpleName();
    private final InetSocketAddress mAddress;
    private final ConnectionChangeListener mConnectionListener;
    private final CameraMessageHandler mCameraMessageHandler;


    private boolean mConnectError;


    private IoSession mSession = null;
    private IoConnector mConnector = null;


    public VdtCameraCommunicationBus(InetSocketAddress address, ConnectionChangeListener connectionListener, CameraMessageHandler messageHandler) {
        this.mAddress = address;
        this.mConnectionListener = connectionListener;
        this.mCameraMessageHandler = messageHandler;
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMinaConnection();
            }
        }).start();
    }

    private void startMinaConnection() {
        mConnector = new NioSocketConnector();
        mConnector.setConnectTimeoutMillis(5000);

        mConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new VdtCodecFactory()));

        mConnector.addListener(new IoServiceListener() {
            @Override
            public void serviceActivated(IoService ioService) throws Exception {
                Log.d(TAG, "serviceActivated");
            }

            @Override
            public void serviceIdle(IoService ioService, IdleStatus idleStatus) throws Exception {
                Log.d(TAG, "serviceIdle");
            }

            @Override
            public void serviceDeactivated(IoService ioService) throws Exception {
                Log.d(TAG, "serviceDeactivated");
                connectError(false);
            }

            @Override
            public void sessionCreated(IoSession ioSession) throws Exception {
                Log.d(TAG, "sessionCreated");
            }

            @Override
            public void sessionClosed(IoSession ioSession) throws Exception {
                Log.d(TAG, "sessionClosed");
            }

            @Override
            public void sessionDestroyed(IoSession ioSession) throws Exception {
                Log.d(TAG, "sessionDestroyed");
                connectError(false);
            }
        });


        KeepAliveMessageFactory factory = new KeepAliveMessageFactory() {
            @Override
            public boolean isRequest(IoSession ioSession, Object o) {
//                Logger.t(TAG).d("isRequest: " + o.toString());
                return false;
            }

            @Override
            public boolean isResponse(IoSession ioSession, Object o) {

                VdtMessage message = (VdtMessage) o;
//                Logger.t(TAG).d("isResponse: " + message.toString());
                if (message.domain == CMD_DOMAIN_CAM && message.messageType == CMD_CAM_IS_API_SUPPORTED) {
//                    Logger.t(TAG).d("receive heart beart response");
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Object getRequest(IoSession ioSession) {
                return new VdtCommand(CMD_DOMAIN_CAM, CMD_CAM_IS_API_SUPPORTED, "", "");
            }

            @Override
            public Object getResponse(IoSession ioSession, Object o) {
                return null;
            }
        };
        KeepAliveFilter kaf = new KeepAliveFilter(factory, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
        kaf.setForwardEvent(true);
        kaf.setRequestInterval(1);
        kaf.setRequestTimeout(10);

        mConnector.getFilterChain().addLast("heart", kaf);

        mConnector.setHandler(new IoHandlerAdapter() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                super.messageReceived(session, message);
                VdtMessage message1 = (VdtMessage) message;
//                Logger.t(TAG).d("message received: " + message1.toString());
                switch (message1.domain) {
                    case CMD_DOMAIN_CAM:
//                        Logger.t(TAG).d("Domain = " + message1.domain + " cmd = " + message1.messageType);
                        mCameraMessageHandler.handleMessage(message1.messageType + CMD_DOMAIN_CAM_START, message1.parameter1, message1.parameter2);
                        break;
                    case CMD_DOMAIN_REC:
//                        Logger.t(TAG).d("Domain = " + message1.domain + " cmd =" + message1.messageType);
                        mCameraMessageHandler.handleMessage(message1.messageType + CMD_DOMAIN_REC_START, message1.parameter1, message1.parameter2);
                        break;
                    default:
                        break;
                }
            }


        });

        try {
            ConnectFuture future = mConnector.connect(mAddress);
            Log.d(TAG, "start connection");
            future.awaitUninterruptibly();
            mSession = future.getSession();
            mConnectionListener.onConnected();
            Log.d(TAG, "connected");
        } catch (Exception e) {
            Log.d(TAG, "connection error");
            connectError(false);
        }


    }


    public void sendCommand(int cmd) {
        sendCommand(cmd, "", "");
    }

    public void sendCommand(int cmd, int p1) {
        sendCommand(cmd, Integer.toString(p1), "");
    }

    public void sendCommand(int cmd, int p1, int p2) {
        sendCommand(cmd, Integer.toString(p1), Integer.toString(p2));
    }

    public void sendCommand(int cmd, String p1) {
        sendCommand(cmd, p1, "");
    }

    public void sendCommand(int cmd, String p1, String p2) {
        VdtCommand command;
        if (cmd >= CMD_DOMAIN_REC_START) {
            command = new VdtCommand(CMD_DOMAIN_REC, cmd - CMD_DOMAIN_REC_START, p1, p2);
        } else {
            command = new VdtCommand(CMD_DOMAIN_CAM, cmd - CMD_DOMAIN_CAM_START, p1, p2);
        }

        sendCommand(command);

    }

    private void sendCommand(VdtCommand command) {
        mSession.write(command);
    }


    // TODO, may deadlock here;
    private synchronized void connectError(boolean reconnect) {
        if (reconnect) {
            startMinaConnection();
        } else {
            if (!mConnectError) {
                Log.d(TAG, "connectError");
                if (mConnector != null) {
                    mConnector.dispose();
                }
                if (mSession != null) {
                    mSession.closeNow();
                }
                mConnectError = true;
                mConnectionListener.onDisconnected();
                Log.d(TAG, "socket is closed");
            }
        }
    }


    public interface ConnectionChangeListener {
        void onConnected();

        void onDisconnected();
    }

    public interface CameraMessageHandler {
        void handleMessage(int code, String p1, String p2);
    }
}
