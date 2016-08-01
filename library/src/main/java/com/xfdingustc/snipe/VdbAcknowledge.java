package com.xfdingustc.snipe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Xiaofei on 2015/8/18.
 */
public class VdbAcknowledge {
    private static final String TAG = VdbAcknowledge.class.getSimpleName();
    public final int statusCode;
    public final boolean notModified;
    public byte[] mReceiveBuffer;
    public byte[] mMsgBuffer;
    private final VdbConnection mVdbConnection;
    private int mMsgIndex;
    protected static final int MSG_MAGIC = 0xFAFBFCFF;
    private static final int VDB_ACK_SIZE = 160;
    protected int mMsgSeqid;
    protected int mUser1;
    protected int mUser2;

    private int mCmdRetCode;
    private int mMsgCode;

    protected int mMsgFlags;
    protected int mCmdTag;

    private static final int MAX_VALID_SIZE = 1024 * 1024 * 10;

    public VdbAcknowledge(int statusCode, VdbConnection vdbConnection) throws IOException {
        this.statusCode = statusCode;
        this.notModified = false;
        this.mVdbConnection = vdbConnection;
        mReceiveBuffer = mVdbConnection.receivedAck();
        parseAcknowledge();
    }

    private void parseAcknowledge() throws IOException {
        mMsgIndex = 0;
        if (readi32() != MSG_MAGIC) {
            //
        }
        mMsgSeqid = readi32(); // ++ each time, set by server
        mUser1 = readi32(); // cmd->user1
        mUser2 = readi32(); // cmd->user2
        mMsgCode = readi16(); // cmd->cmd_code
        mMsgFlags = readi16(); // cmd->cmd_flags
        mCmdTag = readi32(); // cmd->cmd_tag
        mCmdRetCode = readi32();


//        Logger.t(TAG).v(String.format("VdbAcknowledge: StatusCode[%d], CmdCode[%d]",
//                statusCode, mMsgCode));

        int extra_bytes = readi32();
        if (extra_bytes > 0) {
            int size = VDB_ACK_SIZE + extra_bytes;
            if (size > MAX_VALID_SIZE) {
                throw new IOException("Abnormal size: " + size);
            }
            mMsgBuffer = new byte[size];
            System.arraycopy(mReceiveBuffer, 0, mMsgBuffer, 0, VDB_ACK_SIZE);
            mVdbConnection.readFully(mMsgBuffer, VDB_ACK_SIZE, extra_bytes);
            mReceiveBuffer = mMsgBuffer;
        }

        mMsgIndex = 32;
    }

    public int getRetCode() {
        return mCmdRetCode;
    }

    public int getMsgCode() {
        return mMsgCode;
    }

    public int getUser1() {
        return mUser1;
    }

    public boolean isMessageAck() {
        return (mMsgCode >= VdbCommand.Factory.MSG_VdbReady) && (mMsgCode <= VdbCommand.Factory.VDB_MSG_MarkLiveClipInfo);
    }

    public int readi32() {
        int result = (int) mReceiveBuffer[mMsgIndex] & 0xFF;
        mMsgIndex++;
        result |= ((int) mReceiveBuffer[mMsgIndex] & 0xFF) << 8;
        mMsgIndex++;
        result |= ((int) mReceiveBuffer[mMsgIndex] & 0xFF) << 16;
        mMsgIndex++;
        result |= ((int) mReceiveBuffer[mMsgIndex] & 0xFF) << 24;
        mMsgIndex++;
        return result;
    }

    public byte readi8() {
        byte result = mReceiveBuffer[mMsgIndex];
        mMsgIndex++;
        return result;
    }

    public short readi16() {
        int result = (int) mReceiveBuffer[mMsgIndex] & 0xFF;
        mMsgIndex++;
        result |= ((int) mReceiveBuffer[mMsgIndex] & 0xFF) << 8;
        mMsgIndex++;
        return (short) result;
    }

    public long readi64() {
        int lo = readi32();
        int hi = readi32();
        return ((long) hi << 32) | ((long) lo & 0xFFFFFFFFL);
    }

    public void skip(int n) {
        mMsgIndex += n;
    }

    public byte[] readByteArray() {
        int size = readi32();
        return readByteArray(size);
    }

    public byte[] readByteArray(int size) {
        byte[] result = new byte[size];
        System.arraycopy(mReceiveBuffer, mMsgIndex, result, 0, size);
        mMsgIndex += size;
        return result;
    }

    public void readByteArray(byte[] output, int size) {
        System.arraycopy(mReceiveBuffer, mMsgIndex, output, 0, size);
        mMsgIndex += size;
    }

    public String readString() {
        int size = readi32();
        String result;
        try {
            result = new String(mReceiveBuffer, mMsgIndex, size - 1, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            result = "";
        }
        mMsgIndex += size;
        return result;
    }

    public String readStringAligned() {
        int size = readi32();
        if (size <= 0)
            return "";
        String result;
        try {
            result = new String(mMsgBuffer, mMsgIndex, size - 1, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            result = "";
        }
        mMsgIndex += size;
        if ((size % 4) != 0) {
            mMsgIndex += 4 - (size % 4);
        }
        return result;
    }

    public int readStringAlignedReturnSize(String str) {
        int origialSize = mMsgIndex;
        int size = readi32();
        if (size <= 0) {
            str = "";
            return mMsgIndex - origialSize;
        }

        try {
            str = new String(mMsgBuffer, mMsgIndex, size - 1, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            str = "";
        }
        mMsgIndex += size;
        if ((size % 4) != 0) {
            mMsgIndex += 4 - (size % 4);
        }
        return mMsgIndex - origialSize;
    }

}
