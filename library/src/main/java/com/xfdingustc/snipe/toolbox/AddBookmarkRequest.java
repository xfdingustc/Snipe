package com.xfdingustc.snipe.toolbox;


import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;


public class AddBookmarkRequest extends VdbRequest<Integer> {
    private static final String TAG = AddBookmarkRequest.class.getSimpleName();

    private final Clip.ID mClipId;
    private final long mStartTimeMs;
    private final long mEndTimeMs;

    public AddBookmarkRequest(Clip.ID cid, long startTimeMs, long endTimeMs, VdbResponse
        .Listener<Integer> listener, VdbResponse.ErrorListener errorListener) {
        super(0, listener, errorListener);
        this.mClipId = cid;
        this.mStartTimeMs = startTimeMs;
        this.mEndTimeMs = endTimeMs;
    }

    @Override
    protected VdbCommand createVdbCommand() {
        mVdbCommand = VdbCommand.Factory.createCmdAddBookmark(mClipId, mStartTimeMs, mEndTimeMs);
        return mVdbCommand;
    }

    @Override
    protected VdbResponse<Integer> parseVdbResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).d("response: " + response.getRetCode());
            return null;
        }

        int error = response.readi32();
        return VdbResponse.success(error);
    }
}