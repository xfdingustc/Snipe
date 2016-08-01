package com.xfdingustc.snipe;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.toolbox.ClipSetExRequest;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipPos;


/**
 * Created by Xiaofei on 2015/8/19.
 */
public class VdbCommand {
    private static final String TAG = VdbCommand.class.getSimpleName();
    private static final int VDB_CMD_SIZE = 160;
    private byte[] mCmdBuffer = new byte[VDB_CMD_SIZE];
    private int mSendIndex = 0;
    private int mCommandCode;
    private int mAcknowledgeCode = -1;

    private VdbCommand() {

    }

    private void writeCmdCode(int code, int tag) {
        writeCmdCode(code, tag, 0, 0);
    }

    private void writeCmdCode(int code, int tag, int user1, int user2) {
        mSendIndex = 0;
        writei32(code);
        writei32(tag);
        writei32(user1); // user1
        writei32(user2); // user2
    }


    public byte[] getCmdBuffer() {
        return mCmdBuffer;
    }

    public int getCommandCode() {
        return mCommandCode;
    }

    public void setAcknowledgeCode(int code) {
        mAcknowledgeCode = code;
    }

    public int getAcknowledgeCode() {
        return mAcknowledgeCode;
    }

    private void writei32(int value) {
        mCmdBuffer[mSendIndex] = (byte) (value);
        mSendIndex++;
        mCmdBuffer[mSendIndex] = (byte) (value >> 8);
        mSendIndex++;
        mCmdBuffer[mSendIndex] = (byte) (value >> 16);
        mSendIndex++;
        mCmdBuffer[mSendIndex] = (byte) (value >> 24);
        mSendIndex++;
    }

    private void writei64(long value) {
        writei32((int) value);
        writei32((int) (value >> 32));
    }

    private void writeVdbId(String vdbId) {
        if (vdbId == null) {
            return;
        }
        int length = vdbId.length();
        // 4 + length + 0 + aligned_to_4
        int align = 0;
        if ((length + 1) % 4 != 0) {
            align = 4 - (length + 1) % 4;
        }
        // check buffer length
        if (mSendIndex + 4 + length + 1 + align > VDB_CMD_SIZE) {
            Logger.t(TAG).w("vdb_id is too long: " + length);
            return;
        }
        writei32(length + 1);
        for (int i = 0; i < length; i++) {
            mCmdBuffer[mSendIndex] = (byte) vdbId.charAt(i);
            mSendIndex++;
        }
        for (int i = 0; i <= align; i++) {
            mCmdBuffer[mSendIndex] = 0;
            mSendIndex++;
        }
    }

    public void setSequence(int sequence) {
        mCmdBuffer[8] = (byte) (sequence);
        mCmdBuffer[9] = (byte) (sequence >> 8);
        mCmdBuffer[10] = (byte) (sequence >> 16);
        mCmdBuffer[11] = (byte) (sequence >> 24);
    }


    private static class Builder {
        private VdbCommand mVdbCommand;

        private Builder() {
            mVdbCommand = new VdbCommand();
        }

        private Builder writeCmdCode(int code, int tag) {
            mVdbCommand.writeCmdCode(code, tag);
            mVdbCommand.mCommandCode = code;
            return this;
        }

        private Builder writeCmdCode(int code, int tag, int user1, int user2) {
            mVdbCommand.writeCmdCode(code, tag, user1, user2);
            mVdbCommand.mCommandCode = code;
            return this;
        }

        private Builder writeClipId(Clip.ID cid) {
            mVdbCommand.writei32(cid.type);
            mVdbCommand.writei32(cid.subType);
            return this;
        }


        private Builder writeInt32(int value) {
            mVdbCommand.writei32(value);
            return this;
        }

        private Builder writeInt64(long value) {
            mVdbCommand.writei64(value);
            return this;
        }

        private Builder writeVdbId(String vdbId) {
            mVdbCommand.writeVdbId(vdbId);
            return this;
        }

        private VdbCommand build() {
            return mVdbCommand;
        }
    }


    public static class Factory {

        protected static final int CMD_Null = 0;
        protected static final int CMD_GetVersionInfo = 1;
        protected static final int CMD_GetClipSetInfo = 2;
        protected static final int CMD_GetIndexPicture = 3;
        protected static final int CMD_GetPlaybackUrl = 4;
        // protected static final int CMD_GetDownloadUrl = 5; // obsolete
        protected static final int CMD_MarkClip = 6;
        // protected static final int CMD_GetCopyState = 7; // obsolete
        protected static final int CMD_DeleteClip = 8;
        protected static final int CMD_GetRawData = 9;
        protected static final int CMD_SetRawDataOption = 10;
        protected static final int CMD_GetRawDataBlock = 11;
        protected static final int CMD_GetDownloadUrlEx = 12;

        protected static final int CMD_GetAllPlaylists = 13;
        protected static final int CMD_GetPlaylistIndexPicture = 14;
        protected static final int CMD_ClearPlaylist = 15;
        protected static final int CMD_InsertClip = 16;
        protected static final int CMD_MoveClip = 17;
        protected static final int CMD_GetPlaylistPlaybackUrl = 18;

        //since version 1.4
        protected static final int CMD_GetUploadUrl = 19;

        protected static final int CMD_SetOptions = 20;

        protected static final int CMD_GetSpaceInfo = 21;

        //-----------------------
        // since version 1.2
        //-----------------------
        protected static final int CMD_GetClipExtent = 32;
        protected static final int CMD_SetClipExtent = 33;
        protected static final int VDB_CMD_GetClipSetInfoEx = 34;
        protected static final int VDB_CMD_GetAllClipSetInfo = 35;
        protected static final int VDB_CMD_GetClipInfo = 36;

        //-----------------------
        // since version 1.3
        //-----------------------
        protected static final int VDB_CMD_GetRawDataSize = 37;
        protected static final int CMD_GetPlaybackUrlEx = 38;
        protected static final int VDB_CMD_GetPictureList = 39;
        protected static final int VDB_CMD_ReadPicture = 40;
        protected static final int VDB_CMD_RemovePicture = 41;

        protected static final int VDB_CMD_CreatePlaylist = 50;
        protected static final int VDB_CMD_DeletePlaylist = 51;
        protected static final int CMD_InsertClipEx = 52;	// supersedes VDB_CMD_InsertClip
        protected static final int VDB_CMD_GetPlaylistPath = 53;

        private static final int URL_MUTE_AUDIO = (1 << 31);

        public static final int DOWNLOAD_FOR_FILE = 1;
        public static final int DOWNLOAD_FOR_IMAGE = 1 << 1;
        public static final int DOWNLOAD_FIRST_LOOP = 1 << 2;

        protected static final int MSG_START = 0x1000;

        public static final int MSG_VdbReady = MSG_START + 0;
        public static final int MSG_VdbUnmounted = MSG_START + 1;

        public static final int MSG_ClipInfo = MSG_START + 2;
        public static final int MSG_ClipRemoved = MSG_START + 3;

        public static final int MSG_BufferSpaceLow = MSG_START + 4;
        public static final int MSG_BufferFull = MSG_START + 5;
        public static final int MSG_CopyState = MSG_START + 6;
        public static final int MSG_RawData = MSG_START + 7;
        public static final int MSG_PlaylistCleared = MSG_START + 8;
        public static final int VDB_MSG_MarkLiveClipInfo = MSG_START + 32;

        protected static final int MSG_MAGIC = 0xFAFBFCFF;

        /*
        CMD_GetUploadUrl
         */
        public static final int UPLOAD_GET_V0 = 1;    // video stream 0
        public static final int UPLOAD_GET_V1 = (1 << 1);    // video stream 1
        public static final int UPLOAD_GET_PIC = (1 << 2);    // picture
        public static final int UPLOAD_GET_GPS = (1 << 3);    // gps
        public static final int UPLOAD_GET_OBD = (1 << 4);    // obd
        public static final int UPLOAD_GET_ACC = (1 << 5);    // acc

        public static final int UPLOAD_GET_PIC_RAW = (UPLOAD_GET_PIC | UPLOAD_GET_GPS | UPLOAD_GET_OBD | UPLOAD_GET_ACC);
        public static final int UPLOAD_GET_RAW = (UPLOAD_GET_GPS | UPLOAD_GET_OBD | UPLOAD_GET_ACC);
        public static final int UPLOAD_GET_VIDEO = (UPLOAD_GET_V0 | UPLOAD_GET_V1);
        public static final int UPLOAD_GET_STREAM_0 = (UPLOAD_GET_V0 | UPLOAD_GET_RAW);
        public static final int UPLOAD_GET_STREAM_1 = (UPLOAD_GET_V1 | UPLOAD_GET_RAW);


        public static VdbCommand createCmdGetClipSetInfoEx(int type, int flag) {
            Builder builder = new Builder();

            if (flag != ClipSetExRequest.FLAG_UNKNOWN) {
                builder.writeCmdCode(VDB_CMD_GetClipSetInfoEx, 0);
                builder.writeInt32(type);
                builder.writeInt32(flag);
            } else {
                builder.writeCmdCode(CMD_GetClipSetInfo, 0);
                builder.writeInt32(type);
            }
            return builder.build();
        }

        public static VdbCommand createCmdGetClipSetInfo(int type) {
            return new Builder()
                .writeCmdCode(CMD_GetClipSetInfo, 0)
                .writeInt32(type)
                .build();
        }

        public static VdbCommand createCmdGetIndexPicture(ClipPos clipPos) {
            int cmd = CMD_GetIndexPicture;
            if (false && clipPos.getType() == ClipPos.TYPE_POSTER) {
                cmd |= (1 << 16);
            }
            return new Builder()
                .writeCmdCode(cmd, clipPos.getType())
                .writeInt32(clipPos.cid.type)
                .writeInt32(clipPos.cid.subType)
                .writeInt32(clipPos.getType() | (clipPos.isLast() ? ClipPos.F_IS_LAST : 0))
                .writeInt64(clipPos.getClipTimeMs())
                .build();
        }

        public static VdbCommand createCmdGetClipPlaybackUrl(Clip.ID cid, int stream, int urlType,
                                                             boolean muteAudio, long clipTimeMs, int clipLengthMs) {

            Builder builder = new Builder();
            if (clipLengthMs > 0) {
                builder.writeCmdCode(CMD_GetPlaybackUrlEx, 0);
            } else {
                builder.writeCmdCode(CMD_GetPlaybackUrl, 0);
            }
            builder.writeClipId(cid)
                .writeInt32(stream)
                .writeInt32(muteAudio ? urlType | URL_MUTE_AUDIO : urlType)
                .writeInt64(clipTimeMs);

            if (clipLengthMs > 0) {
                builder.writeInt32(clipLengthMs);
            }
            return builder.build();

        }

        public static VdbCommand createCmdGetSpaceInfo() {
            return new Builder()
                .writeCmdCode(CMD_GetSpaceInfo, 0)
                .build();
        }

        public static VdbCommand createCmdGetClipDownloadUrl(Clip.ID cid, long startTime, int length, int downloadOption, boolean bFirstLoop) {
            int cmdTag = DOWNLOAD_FOR_FILE;
            if (bFirstLoop) {
                cmdTag |= DOWNLOAD_FIRST_LOOP;
            }
            //int duration = (int) (endMs - startMs);
            return new Builder()
                .writeCmdCode(CMD_GetDownloadUrlEx, cmdTag, 0, 0)
                .writeClipId(cid)
                .writeInt64(startTime)
                .writeInt32(length)
                .writeInt32(downloadOption)
                .build();
        }

        public static VdbCommand createCmdGetRawData(Clip clip, long clipTimeMs, int type) {
            return new Builder()
                .writeCmdCode(CMD_GetRawData, 0)
                .writeClipId(clip.cid)
                .writeInt64(clipTimeMs)
                .writeInt32(type)
                .build();
        }


        public static VdbCommand createCmdGetRawDataBlock(Clip.ID cid, boolean forDownload,
                                                          int dataType, long clipTimeMs, int duration) {

            return new Builder()
                .writeCmdCode(CMD_GetRawDataBlock, forDownload ? 1 : 0)
                .writeClipId(cid)
                .writeInt64(clipTimeMs)
                .writeInt32(duration)
                .writeInt32(dataType)
                .build();
        }

        public static VdbCommand createCmdGetClipExtent(Clip clip) {
            return new Builder()
                .writeCmdCode(CMD_GetClipExtent, 0)
                .writeClipId(clip.cid)
                .build();
        }

        public static VdbCommand createCmdSetClipExtent(Clip.ID cid, long newClipStart, long newClipEnd) {
            return new Builder()
                .writeCmdCode(CMD_SetClipExtent, 0)
                .writeClipId(cid)
                .writeInt64(newClipStart)
                .writeInt64(newClipEnd)
                .build();
        }

        public static VdbCommand createCmdGetUploadUrl(Clip.ID cid, boolean isPlayList, long clipTimeMs, int lengthMs, int uploadOpt) {
            int playListValue = isPlayList ? 1 : 0;
            return new Builder()
                .writeCmdCode(CMD_GetUploadUrl, 0)
                .writeInt32(playListValue)
                .writeClipId(cid)
                .writeInt64(clipTimeMs)
                .writeInt32(lengthMs)
                .writeInt32(uploadOpt)
                .writeInt32(0)
                .writeInt32(0)
                .build();
        }

        public static VdbCommand createCmdSetRawDataOption(int dataType) {
            VdbCommand command = new Builder()
                .writeCmdCode(CMD_SetRawDataOption, 0)
                .writeInt32(dataType)
                .build();
            command.setAcknowledgeCode(MSG_RawData);
            return command;
        }


        public static VdbCommand createCmdInsertClip(Clip.ID clipId, long startTimeMs,
                                                     long endTimeMs, int playListId,
                                                     int playlistPos) {
            VdbCommand command = new Builder()
                .writeCmdCode(CMD_InsertClipEx, 0)
                .writeClipId(clipId)
                .writeInt64(startTimeMs)
                .writeInt64(endTimeMs)
                .writeInt32(playListId)
                .writeInt32(playlistPos)
                .build();
            return command;
        }

        public static VdbCommand createCmdClearPlayList(int playlistId) {
            VdbCommand command = new Builder()
                .writeCmdCode(CMD_ClearPlaylist, 0)
                .writeInt32(playlistId)
                .build();
            command.setAcknowledgeCode(MSG_PlaylistCleared);
            return command;
        }


        public static VdbCommand createCmdGetPlaylistPlaybackUrl(int urlType, int playlistId, int
            startMs, int stream) {
            VdbCommand command = new Builder()
                .writeCmdCode(CMD_GetPlaylistPlaybackUrl, 0)
                .writeInt32(playlistId)
                .writeInt32(startMs)
                .writeInt32(stream)
                .writeInt32(urlType)
                .build();
            return command;
        }

        public static VdbCommand createCmdGetPlaylistSetInfo(int flags) {
            VdbCommand command = new Builder()
                .writeCmdCode(CMD_GetAllPlaylists, 0)
                .writeInt32(flags)
                .build();
            return command;
        }

        public static VdbCommand createDummyGetRawData() {
            VdbCommand command = new Builder()
                .build();
            command.setAcknowledgeCode(MSG_RawData);
            return command;
        }

        public static VdbCommand createCmdSetOptions(int option, int hlsSegmentLength) {
            return new Builder()
                .writeCmdCode(CMD_SetOptions, 0)
                .writeInt32(option)
                .writeInt32(hlsSegmentLength)
                .writeInt32(0)
                .writeInt32(0)
                .writeInt32(0)
                .build();
        }

        public static VdbCommand createCmdClipMove(Clip.ID cid, int newPosition) {
            return new Builder()
                .writeCmdCode(CMD_MoveClip, 0)
                .writeClipId(cid)
                .writeInt32(newPosition)
                .build();
        }

        public static VdbCommand createCmdClipDelete(Clip.ID cid) {
            return new Builder()
                .writeCmdCode(CMD_DeleteClip, 0)
                .writeClipId(cid)
                .build();
        }

        public static VdbCommand createCmdAddBookmark(Clip.ID cid, long startTimeMs, long
            endTimeMs) {
            return new Builder()
                .writeCmdCode(CMD_MarkClip, 0)
                .writeClipId(cid)
                .writeInt64(startTimeMs)
                .writeInt64(endTimeMs)
                .build();
        }

    }
}
