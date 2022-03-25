package com.kinetise.support.scrolls.scrollManager;

import android.view.MotionEvent;

/**
 * Wrapper that is holding informations about given motion event
 */
public class ScrollMotionEvent {

	private long mDownTime = 0;
	private long mEventTime = 0;
	private float mRawX = 0;
	private float mRawY = 0;
	private float mX = 0;
	private float mY = 0;

	public ScrollMotionEvent() {
	}

	public ScrollMotionEvent(MotionEvent motionEvent) {
		set(motionEvent);
	}

	public void set(MotionEvent motionEvent) {
		mDownTime = motionEvent.getDownTime();
		mEventTime = motionEvent.getEventTime();
		mRawX = motionEvent.getRawX();
		mRawY = motionEvent.getRawY();
		mX = motionEvent.getX();
		mY = motionEvent.getY();
	}

	public void set(ScrollMotionEvent scrollMotionEvent) {
		mDownTime = scrollMotionEvent.getDownTime();
		mEventTime = scrollMotionEvent.getEventTime();
		mRawX = scrollMotionEvent.getRawX();
		mRawY = scrollMotionEvent.getRawY();
		mX = scrollMotionEvent.getX();
		mY = scrollMotionEvent.getY();
	}

	public void reset() {
		mDownTime = -1;
		mEventTime = -1;
		mRawX = -1;
		mRawY = -1;
		mX = -1;
		mY = -1;
	}

	public long getDownTime() {
		return mDownTime;
	}

	public long getEventTime() {
		return mEventTime;
	}

	public float getRawX() {
		return mRawX;
	}

	public float getRawY() {
		return mRawY;
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

}
