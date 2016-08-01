package com.xfdingustc.snipe.toolbox;

import com.orhanobut.logger.Logger;
import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipSet;


/**
 * Created by Xiaofei on 2015/8/18.
 */
public class ClipSetExRequest extends VdbRequest<ClipSet> {
    public static final int FLAG_UNKNOWN = -1;
    public static final int FLAG_CLIP_EXTRA = 1;
    public static final int FLAG_CLIP_VDB_ID = 1 << 1;
    public static final int FLAG_CLIP_DESC = 1 << 2;
    public static final int FLAG_CLIP_ATTR = 1 << 3;
    public static final int METHOD_GET = 0;
    public static final int METHOD_SET = 1;
    private final static String TAG = ClipSetExRequest.class.getSimpleName();
    private static final int UUID_LENGTH = 36;
    private final int mClipType;
    private final int mFlag;
    private final int mAttr;

    public ClipSetExRequest(int type, int flag, VdbResponse.Listener<ClipSet> listener,
                            VdbResponse.ErrorListener errorListener) {
        this(METHOD_GET, type, flag, 0, listener, errorListener);
    }

    public ClipSetExRequest(int type, int flag, int attr, VdbResponse.Listener<ClipSet> listener,
                            VdbResponse.ErrorListener errorListener) {
        this(METHOD_GET, type, flag, attr, listener, errorListener);
    }

    public ClipSetExRequest(int method, int type, int flag, int attr, VdbResponse.Listener<ClipSet> listener,
                            VdbResponse.ErrorListener errorListener) {
        super(method, listener, errorListener);
        this.mClipType = type;
        this.mAttr = attr;
        this.mFlag = flag;
    }

    @Override
    protected VdbCommand createVdbCommand() {
        switch (mMethod) {
            case METHOD_GET:
                mVdbCommand = VdbCommand.Factory.createCmdGetClipSetInfoEx(mClipType, mFlag);
                break;
            case METHOD_SET:
                break;
            default:
                break;
        }

        return mVdbCommand;
    }

    @Override
    protected VdbResponse<ClipSet> parseVdbResponse(VdbAcknowledge response) {
        switch (mMethod) {
            case METHOD_GET:
                return parseGetClipSetResponse(response);
            case METHOD_SET:
                break;
        }
        return null;
    }

    private VdbResponse<ClipSet> parseGetClipSetResponse(VdbAcknowledge response) {
        if (response.getRetCode() != 0) {
            Logger.t(TAG).e("ackGetClipSetInfo: failed");
            return null;
        }

        ClipSet clipSet = new ClipSet(response.readi32());

        int totalClips = response.readi32();

        response.readi32(); // TODO - totalLengthMs

        Clip.ID liveClipId = new Clip.ID(Clip.TYPE_BUFFERED, response.readi32(), null);
        clipSet.setLiveClipId(liveClipId);

        for (int i = 0; i < totalClips; i++) {
            int clipId = response.readi32();
            int clipDate = response.readi32();
            int duration = response.readi32();
            long startTimeMs = response.readi64();
            Clip clip = new Clip(clipSet.getType(), clipId, null, clipDate, startTimeMs, duration);

            int numStreams = response.readi16();
            int flag = response.readi16();
            //Log.e("test", "Flag: " + flag);

            if (numStreams > 0) {
                readStreamInfo(clip, 0, response);
                if (numStreams > 1) {
                    readStreamInfo(clip, 1, response);
                    if (numStreams > 2) {
                        response.skip(16 * (numStreams - 2));
                    }
                }
            }
            response.readi32(); //int clipType
            int extraSize = response.readi32(); //int extraSize

            int offsetSize = 0;

            if ((flag & FLAG_CLIP_EXTRA) > 0) {
                String guid = new String(response.readByteArray(UUID_LENGTH));
                clip.cid.setExtra(guid);

                response.readi32(); //int ref_clip_date
                clip.gmtOffset = response.readi32();
                int realClipId = response.readi32(); //int real_clip_id
                clip.realCid = new Clip.ID(Clip.TYPE_BUFFERED, realClipId, guid);

                offsetSize += UUID_LENGTH + 3 * 4;
//                response.skip(extraSize - offsetSize);


            }

            if ((flag & FLAG_CLIP_VDB_ID) > 0) {
                String extraString = new String();
                offsetSize += response.readStringAlignedReturnSize(extraString);

                clip.cid.setExtra(extraString);
                response.skip(extraSize - offsetSize);
            }

            if ((flag & FLAG_CLIP_ATTR) > 0) {
//                Logger.t(TAG).d("flag : " + flag );
                int attr = response.readi32();
                offsetSize += 4;
                if ((attr & mAttr) > 0) {
                    clipSet.addClip(clip);
                }



            } else {
                clipSet.addClip(clip);
            }
            response.skip(extraSize - offsetSize);
        }
        return VdbResponse.success(clipSet);
    }

    private void readStreamInfo(Clip clip, int index, VdbAcknowledge response) {
        Clip.StreamInfo info = clip.streams[index];
        info.version = response.readi32();
        info.video_coding = response.readi8();
        info.video_framerate = response.readi8();
        info.video_width = response.readi16();
        info.video_height = response.readi16();
        info.audio_coding = response.readi8();
        info.audio_num_channels = response.readi8();
        info.audio_sampling_freq = response.readi32();
    }


}
