package com.xfdingustc.snipe.toolbox;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;


/**
 * Created by Xiaofei on 2016/1/27.
 */
public class PlaylistEditRequest extends VdbRequest<Integer> {
    private static final String TAG = PlaylistEditRequest.class.getSimpleName();
    public static final int METHOD_INSERT_CLIP = 0;
    public static final int METHOD_CLEAR_PLAYLIST = 1;
    private final Clip mClip;
    private final int mPlayListID;
    private final long mStartTimeMs;
    private final long mEndTimeMs;
    private final int mIndex;

    public PlaylistEditRequest(Clip clip, long startTimeMs, long endTimeMs, int playListID,
                               VdbResponse.Listener<Integer> listener, VdbResponse.ErrorListener errorListener) {
        this(METHOD_INSERT_CLIP, clip, startTimeMs, endTimeMs, -1, playListID, listener, errorListener);

    }

    public PlaylistEditRequest(int method, Clip clip, long startTimeMs, long endTimeMs, int index,
                               int playListID, VdbResponse.Listener<Integer> listener,
                               VdbResponse.ErrorListener errorListener) {
        super(method, listener, errorListener);
        this.mClip = clip;
        this.mStartTimeMs = startTimeMs;
        this.mEndTimeMs = endTimeMs;
        mIndex = index;
        mPlayListID = playListID;
    }

    public static PlaylistEditRequest getClearPlayListRequest(int playListID,
                                                              VdbResponse.Listener<Integer> listener,
                                                              VdbResponse.ErrorListener errorListener) {
        return new PlaylistEditRequest(METHOD_CLEAR_PLAYLIST, null, 0, 0, 0, playListID, listener, errorListener);
    }

    @Override
    protected VdbCommand createVdbCommand() {
        switch (mMethod) {
            case METHOD_INSERT_CLIP:
                mVdbCommand = VdbCommand.Factory.createCmdInsertClip(mClip.cid, mStartTimeMs,
                    mEndTimeMs, mPlayListID, mIndex);
                break;
            case METHOD_CLEAR_PLAYLIST:
                mVdbCommand = VdbCommand.Factory.createCmdClearPlayList(mPlayListID);

                break;
        }
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<Integer> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).e("PlaylistEditRequest: failed");
            return null;
        }
        int error = response.readi32();
        return VdbResponse.success(error);
    }
}
