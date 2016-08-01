package com.xfdingustc.snipe.toolbox;

import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.urls.UploadUrl;


/**
 * Created by Richard on 11/19/15.
 */
public class ClipUploadUrlRequest extends VdbRequest<UploadUrl> {
    private static final String TAG = ClipUploadUrlRequest.class.getSimpleName();
    public static final String PARAM_IS_PLAY_LIST = "is_play_list";
    public static final String PARAM_UPLOAD_OPT = "upload.opt";
    public static final String PARAM_CLIP_TIME_MS = "clip_time_ms";
    public static final String PARAM_CLIP_LENGTH_MS = "clip_length_ms";


    private Clip.ID mCid;
    private Bundle mParams;

    public ClipUploadUrlRequest(Clip.ID cid, Bundle params, VdbResponse.Listener<UploadUrl> listener,
                                VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        mCid = cid;
        mParams = params;
    }

    @Override
    protected VdbCommand createVdbCommand() {
        boolean isPlayList = mParams.getBoolean(PARAM_IS_PLAY_LIST);
        long clipTimeMs = mParams.getLong(PARAM_CLIP_TIME_MS);
        int clipLengthMs = mParams.getInt(PARAM_CLIP_LENGTH_MS);
        int uploadOpt = mParams.getInt(PARAM_UPLOAD_OPT);
        mVdbCommand = VdbCommand.Factory.createCmdGetUploadUrl(mCid, isPlayList, clipTimeMs, clipLengthMs, uploadOpt);
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<UploadUrl> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).e("ackGetUploadUrl: failed");
            return null;
        }

        boolean isPlayList = response.readi32() != 0;
        int clipType = response.readi32();
        int clipId = response.readi32();
        long realTimeMs = response.readi64();
        int lengthMs = response.readi32();
        int uploadOpt = response.readi32();
        int reserved1 = response.readi32();
        int reserved2 = response.readi32();
        String url = response.readString();

        Clip.ID cid = new Clip.ID(clipType, clipId, null);
        UploadUrl uploadUrl = new UploadUrl(isPlayList, cid, realTimeMs, lengthMs, uploadOpt, url);
        return VdbResponse.success(uploadUrl);
    }
}
