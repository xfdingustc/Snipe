package com.xfdingustc.snipe.sample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.xfdingustc.snipe.SnipeError;
import com.xfdingustc.snipe.VdbRequestFuture;
import com.xfdingustc.snipe.VdbResponse;
import com.xfdingustc.snipe.toolbox.ClipSetExRequest;
import com.xfdingustc.snipe.toolbox.VdbImageRequest;
import com.xfdingustc.snipe.vdb.Clip;
import com.xfdingustc.snipe.vdb.ClipPos;
import com.xfdingustc.snipe.vdb.ClipSet;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Xiaofei on 2016/8/1.
 */
public class ImageTestActivity extends BaseActivity {

    private ClipSet mClipSet;

    @BindView(R.id.log)
    TextView mLog;

    @OnClick(R.id.btn_start)
    public void onBtnStartClicked() {
        mVdbRequestQueue = Vdb.getVdbRequestQueue();
        fetchBookmark();
    }

    @OnClick(R.id.btn_insert)
    public void onBtnInsertClicked() {
        ClipSetExRequest request = new ClipSetExRequest(Clip.TYPE_MARKED, ClipSetExRequest.FLAG_CLIP_EXTRA, 0, new VdbResponse.Listener<ClipSet>() {
            @Override
            public void onResponse(ClipSet response) {
                mLog.append("Get Inserted response");
                mLog.append("\t");
            }
        }, new VdbResponse.ErrorListener() {
            @Override
            public void onErrorResponse(SnipeError error) {

            }
        });

        mVdbRequestQueue.add(request);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);
    }


    private void fetchBookmark() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    VdbRequestFuture<ClipSet> vdbRequestFuture = VdbRequestFuture.newFuture();
                    ClipSetExRequest request = new ClipSetExRequest(Clip.TYPE_MARKED, ClipSetExRequest.FLAG_CLIP_EXTRA, 0, vdbRequestFuture, vdbRequestFuture);
                    long current = System.currentTimeMillis();
                    mVdbRequestQueue.add(request);
                    mClipSet = vdbRequestFuture.get();
                    subscriber.onNext("Get ClipSet command cost: " + (System.currentTimeMillis() - current));


                    for (int i = 0; i < 20; i++) {
                        ClipPos clipPos = new ClipPos(mClipSet.getClip(0));
                        final int ii = i;
                        VdbImageRequest imageRequest = new VdbImageRequest(clipPos, new VdbResponse.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                subscriber.onNext("get bitmap " + ii);
                            }
                        }, new VdbResponse.ErrorListener() {
                            @Override
                            public void onErrorResponse(SnipeError error) {
                                subscriber.onError(error);
                            }
                        });
                        mVdbRequestQueue.add(imageRequest);
                    }

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(String s) {
                    mLog.append(s);
                    mLog.append("\t");
                }
            });

    }
}
