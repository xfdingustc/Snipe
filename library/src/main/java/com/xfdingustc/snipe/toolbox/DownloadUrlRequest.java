package com.xfdingustc.snipe.toolbox;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipDownloadInfo;
import com.xfdingustc.snipe.vdb.ClipSegment;


/**
 * Created by Xiaofei on 2015/8/27.
 */
public class DownloadUrlRequest extends VdbRequest<ClipDownloadInfo> {
    private static final String TAG = DownloadUrlRequest.class.getSimpleName();
    private ClipSegment mClipSegment;
    private int mStreamIndex;
    private Clip.ID cid;

    private long mStartTime;
    private int mLength;

    public static final int DOWNLOAD_OPT_MAIN_STREAM = (1 << 0);
    public static final int DOWNLOAD_OPT_SUB_STREAM_1 = (1 << 1);
    public static final int DOWNLOAD_OPT_INDEX_PICT = (1 << 2);
    public static final int DOWNLOAD_OPT_PLAYLIST = (1 << 3);
    public static final int DOWNLOAD_OPT_MUTE_AUDIO = (1 << 4);



    public DownloadUrlRequest(ClipSegment clipSegment, VdbResponse.Listener<ClipDownloadInfo> listener,
                              VdbResponse.ErrorListener errorListener) {
        this(clipSegment, DOWNLOAD_OPT_SUB_STREAM_1, listener, errorListener);
    }

    public DownloadUrlRequest(ClipSegment clipSegment, int streamIndex, VdbResponse.Listener<ClipDownloadInfo> listener,
                              VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        this.mClipSegment = clipSegment;
        this.mStreamIndex = streamIndex;
    }

    public DownloadUrlRequest(Clip.ID cid, long start, int length, VdbResponse.Listener<ClipDownloadInfo> listener,
                              VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        this.cid = cid;
        this.mStartTime = start;
        this.mLength = length;
//        this.mStreamIndex = streamIndex;
    }


    @Override
    protected VdbCommand createVdbCommand() {
        int downloadOption = DOWNLOAD_OPT_MAIN_STREAM | DOWNLOAD_OPT_SUB_STREAM_1 | DOWNLOAD_OPT_PLAYLIST;
        mVdbCommand = VdbCommand.Factory.createCmdGetClipDownloadUrl(cid, mStartTime, mLength, downloadOption, true);
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<ClipDownloadInfo> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).e("ackGetDownloadUrl: failed: " + response.getRetCode());

            //return null;
        }

        int clipType = response.readi32();
        int clipId = response.readi32();
        Clip.ID cid = new Clip.ID(clipType, clipId, null);
        ClipDownloadInfo clipDownloadInfo = new ClipDownloadInfo(cid);

        int download_opt = response.readi32();
        clipDownloadInfo.opt = download_opt;

        if ((download_opt & DOWNLOAD_OPT_MAIN_STREAM) != 0) {
            clipDownloadInfo.main.clipDate = response.readi32();
            clipDownloadInfo.main.clipTimeMs = response.readi64();
            clipDownloadInfo.main.lengthMs = response.readi32();
            clipDownloadInfo.main.size = response.readi64();
            clipDownloadInfo.main.url = response.readString();
        }

        if ((download_opt & DOWNLOAD_OPT_SUB_STREAM_1) != 0) {
            clipDownloadInfo.sub.clipDate = response.readi32();
            clipDownloadInfo.sub.clipTimeMs = response.readi64();
            clipDownloadInfo.sub.lengthMs = response.readi32();
            clipDownloadInfo.sub.size = response.readi64();
            clipDownloadInfo.sub.url = response.readString();
        }

        if ((download_opt & DOWNLOAD_OPT_INDEX_PICT) != 0) {
            int pictureSize = response.readi32();
            clipDownloadInfo.posterData = new byte[pictureSize];
            response.readByteArray(clipDownloadInfo.posterData, pictureSize);
        }


        return VdbResponse.success(clipDownloadInfo);
    }
}
