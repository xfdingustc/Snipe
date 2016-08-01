package com.xfdingustc.snipe;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Xiaofei on 2015/8/18.
 */
public class VdbConnection {
    private static final String TAG = VdbConnection.class.getSimpleName();
    private static final int VDB_CMD_PORT = 8083;
    private static final int CONNECT_TIMEOUT = 1000 * 30;
    private Socket mSocket;
    private InetSocketAddress mSocketAddress;

    public VdbConnection(String address) {
        mSocket = new Socket();
        mSocketAddress = new InetSocketAddress(address, VDB_CMD_PORT);
    }

    public void connect() throws IOException, InterruptedException {
        mSocket.setReceiveBufferSize(8192);
        mSocket.connect(mSocketAddress, CONNECT_TIMEOUT);
//        Logger.t(TAG).d("Connected");
        // clear input stream
        // TODO: we need figure out why we received null package here
        //byte[] tmp = new byte[160];
        //readFully(tmp, 0, 160);
    }

    public void disconnect() throws IOException {
        mSocket.close();
    }

    public boolean isConnected() {
        return mSocket.isConnected();
    }


    public void sendCommnd(VdbCommand command) throws IOException {
        sendByteArray(command.getCmdBuffer());
    }

    public byte[] receivedAck() throws IOException {
        byte[] buffer = new byte[160];
        readFully(buffer, 0, buffer.length);
        return buffer;
    }


    public void readFully(byte[] buffer, int pos, int size) throws IOException {
        InputStream input = mSocket.getInputStream();

        while (size > 0) {
            int ret = input.read(buffer, pos, size);
            if (ret < 0) {
                throw new IOException();
            }
            pos += ret;
            size -= ret;
        }

    }

    public void setSoTimeout(int timeoutMs) throws SocketException {
        mSocket.setSoTimeout(timeoutMs);
    }

    private void sendByteArray(byte[] data) throws IOException {
        mSocket.getOutputStream().write(data);
    }

    // API
    public void sendByteArray(byte[] data, int offset, int count) throws IOException {
        mSocket.getOutputStream().write(data, offset, count);
    }
}
