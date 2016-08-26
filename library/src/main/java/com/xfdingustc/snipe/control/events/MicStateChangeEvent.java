package com.xfdingustc.snipe.control.events;

/**
 * Created by liushuwei on 16/6/24.
 */
public class MicStateChangeEvent {
    private final int mMicState;
    private final int mMicVol;


    public MicStateChangeEvent(int micState, int micVol) {
        this.mMicState = micState;
        this.mMicVol = micVol;
    }

    public int getMicState() {
            return mMicState;
        }

    public int getMicVol() {
            return mMicVol;
        }

}
