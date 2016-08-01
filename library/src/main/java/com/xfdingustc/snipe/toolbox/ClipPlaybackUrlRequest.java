package com.xfdingustc.snipe.toolbox;

import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.urls.PlaybackUrl;


/**
 * Created by Xiaofei on 2015/8/27.
 */
public class ClipPlaybackUrlRequest extends VdbRequest<PlaybackUrl> {
    private static final String TAG = ClipPlaybackUrlRequest.class.getSimpleName();
    protected final Bundle mParameters;
    protected final Clip.ID mCid;

    public static final String PARAMETER_STREAM = "stream";
    public static final String PARAMETER_URL_TYPE = "url_type";
    public static final String PARAMETER_MUTE_AUDIO = "mute_audio";
    public static final String PARAMETER_CLIP_TIME_MS = "clip_time_ms";

    public ClipPlaybackUrlRequest(Clip.ID cid, Bundle parameters, VdbResponse.Listener<PlaybackUrl>
            listener, VdbResponse.ErrorListener errorListener) {
        this(0, cid, parameters, listener, errorListener);
    }

    public ClipPlaybackUrlRequest(int method, Clip.ID cid, Bundle parameters, VdbResponse.Listener<PlaybackUrl>
            listener, VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        this.mCid = cid;
        this.mParameters = parameters;
    }

    @Override
    protected VdbCommand createVdbCommand() {
        int stream = mParameters.getInt(PARAMETER_STREAM);
        int urlType = mParameters.getInt(PARAMETER_URL_TYPE);
        boolean muteAudio = mParameters.getBoolean(PARAMETER_MUTE_AUDIO);
        long clipTimeMs = mParameters.getLong(PARAMETER_CLIP_TIME_MS);
        mVdbCommand = VdbCommand.Factory.createCmdGetClipPlaybackUrl(mCid, stream, urlType,
                muteAudio, clipTimeMs, 0);
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<PlaybackUrl> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).e("ackGetPlaybackUrl: failed");
            return null;
        }

        int clipType = response.readi32();
        int clipId = response.readi32();
        int stream = response.readi32();
        int urlType = response.readi32();
        long realTimeMs = response.readi64();
        int lengthMs = response.readi32();
        boolean bHasMore = response.readi32() != 0;
        String url = response.readString();

        String vdbId = null;

        Clip.ID cid = new Clip.ID(clipType, clipId, vdbId);
        PlaybackUrl playbackUrl = new PlaybackUrl(cid);

        playbackUrl.stream = stream;
        playbackUrl.urlType = urlType;
        playbackUrl.realTimeMs = realTimeMs;
        playbackUrl.lengthMs = lengthMs;
        playbackUrl.bHasMore = bHasMore;
        playbackUrl.url = url;
        playbackUrl.offsetMs = 0;

        return VdbResponse.success(playbackUrl);
    }
}
