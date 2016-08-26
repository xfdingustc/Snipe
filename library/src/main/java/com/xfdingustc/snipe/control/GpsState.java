package com.xfdingustc.snipe.control;

import android.util.Log;

public class GpsState {

	public static final boolean DEBUG = false;
	public static final String TAG = "GpsState";

	public static final int STATE_GPS_UNKNOWN = -1;
	public static final int STATE_GPS_ON = 0;
	public static final int STATE_GPS_READY = 1;
	public static final int STATE_GPS_OFF = 2;

	public int mStateSN = 0;
	public boolean mbSchedule = false;

	public int mGpsState = STATE_GPS_UNKNOWN;

	// default states when not available
	public static final GpsState nullState = new GpsState();

	// sync our states to users
	synchronized public boolean syncStates(GpsState user) {
		if (mStateSN == user.mStateSN) {
			if (DEBUG) {
				Log.d(TAG, "-- syncStates: no change ---");
			}
			return false;
		} else {
			if (DEBUG) {
				Log.d(TAG, "-- syncStates ---");
			}
		}

		user.mStateSN = mStateSN;
		user.mGpsState = mGpsState;

		return true;
	}

	private final void stateChanged() {
		mStateSN++;
		mbSchedule = true;
	}

	synchronized public void setGpsState(int state) {
		if (mGpsState != state) {
			if (DEBUG) {
				Log.d(TAG, "setGpsState: " + state);
			}
			mGpsState = state;
			stateChanged();
		}
	}

}
