package com.xfdingustc.snipe.toolbox;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView.ScaleType;

import com.xfdingustc.snipe.VdbAcknowledge;
import com.xfdingustc.snipe.VdbCommand;
import com.xfdingustc.snipe.VdbRequest;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipPos;


/**
 * Created by Xiaofei on 2015/8/25.
 */
public class VdbImageRequest extends VdbRequest<Bitmap> {
    private final static String TAG = VdbImageRequest.class.getSimpleName();

    private final Config mDecoderConfig;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private final ScaleType mScaleType;
    private final ClipPos mClipPos;
    String mCacheKey; //


    public VdbImageRequest(ClipPos clipPos, VdbResponse.Listener<Bitmap> listener, VdbResponse.ErrorListener errorListener) {
        this(clipPos, listener, errorListener, 0, 0, ScaleType.CENTER_INSIDE, Config.RGB_565, null);
    }

    public VdbImageRequest(ClipPos clipPos, VdbResponse.Listener<Bitmap> listener, VdbResponse.ErrorListener errorListener,
                           int maxWidth, int maxHeight, ScaleType scaleType, Config decodeConfig, String cacheKey) {
        super(0, listener, errorListener);
        this.mClipPos = clipPos;
        this.mDecoderConfig = decodeConfig;
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mScaleType = scaleType;
        mCacheKey = cacheKey;
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    public boolean isIgnorable() {
        return true;
    }

    @Override
    protected VdbCommand createVdbCommand() {
        mVdbCommand = VdbCommand.Factory.createCmdGetIndexPicture(mClipPos);
        return mVdbCommand;
    }


    @Override
    protected VdbResponse<Bitmap> parseVdbResponse(VdbAcknowledge response) {

        if (response.getRetCode() != 0) {
            Log.e(TAG, "ackGetIndexPicture: failed");
            return null;
        }

        int clipType = response.readi32();
        int clipId = response.readi32();
        int clipDate = response.readi32();
        int type = response.readi32();
        boolean bIsLast = (type & ClipPos.F_IS_LAST) != 0;
        type &= ~ClipPos.F_IS_LAST;
        long timeMs = response.readi64();
        long clipStartTime = response.readi64();
        int clipDuration = response.readi32();

        int pictureSize = response.readi32();
        byte[] data = new byte[pictureSize];
        response.readByteArray(data, pictureSize);

        String vdbId = null;

        if (false) {
            vdbId = response.readStringAligned();
        }

        Clip.ID cid = new Clip.ID(clipType, clipId, vdbId);
        ClipPos clipPos = new ClipPos(vdbId, cid, clipDate, timeMs, type, bIsLast);
        clipPos.setRealTimeMs(clipStartTime);
        clipPos.setDuration(clipDuration);

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inPreferredConfig = mDecoderConfig;
        Bitmap bitmap = null;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        } else {
            bitmap = decodeSampledBitmap(data, mMaxWidth, mMaxHeight, decodeOptions);
        }


        if (bitmap != null) {
            return VdbResponse.success(bitmap);
        } else {
            return null;
        }
    }

    public int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmap(byte[] data, int reqWidth, int reqHeight, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public String getCacheKey() {
        return mCacheKey;
    }
}
