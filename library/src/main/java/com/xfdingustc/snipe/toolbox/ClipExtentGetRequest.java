package com.xfdingustc.snipe.toolbox;


import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipExtent;


/**
 * Created by Richard on 9/15/15.
 */
public class ClipExtentGetRequest extends VdbRequest<ClipExtent> {

    private static final String TAG = "ClipExtentGetRequest";

    private Clip mClip;

    public ClipExtentGetRequest(Clip clip, VdbResponse.Listener<ClipExtent> listener, VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        mClip = clip;
    }

    @Override
    protected VdbCommand createVdbCommand() {
        mVdbCommand = VdbCommand.Factory.createCmdGetClipExtent(mClip);
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<ClipExtent> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).e("ClipExtentGetRequest: failed");
            return null;
        }

        int clipType = response.readi32();
        int clipId = response.readi32();
        int realClipId = response.readi32(); // use this with CLIP_TYPE_REAL to get index picture
        int bufferedClipId = response.readi32();
        Clip.ID bufferedCid = null;
        if (bufferedClipId != 0) {
            bufferedCid = new Clip.ID(Clip.TYPE_BUFFERED, bufferedClipId, null);
        }

        long minClipStartTimeMs = response.readi64();
        long maxClipEndTimeMs = response.readi64();
        long clipStartTimeMs = response.readi64();
        long clipEndTimeMs = response.readi64();

        ClipExtent clipExtent = new ClipExtent(
                new Clip.ID(clipType, clipId, null),
                new Clip.ID(Clip.TYPE_REAL, realClipId, null),
                bufferedCid);
        clipExtent.minClipStartTimeMs = minClipStartTimeMs;
        clipExtent.maxClipEndTimeMs = maxClipEndTimeMs;
        clipExtent.clipStartTimeMs = clipStartTimeMs;
        clipExtent.clipEndTimeMs = clipEndTimeMs;
        return VdbResponse.success(clipExtent);
    }
}
