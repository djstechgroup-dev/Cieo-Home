package com.kinetise.support.scrolls.scrollManager.scrollStack;

import com.kinetise.data.systemdisplay.views.IScrollable;

/**
 * Wrapper for scroll object, used to unify calculations for scrolls
 */
public class ScrollStackElement {

	private IScrollable mScroll;
	
	public ScrollStackElement(IScrollable scroll){
		mScroll = scroll;
	}

	/**
	 * 
	 * @return wrapped scroll object
	 */
	public IScrollable getScroll() {
		return mScroll;
	}

	
	public void setScroll(IScrollable scroll) {
		this.mScroll = scroll;
	}

	/**
	 * Calculates value in pixels that can be scrolled up.
	 * @return free space left from top of viewport to top of view.
	 */
	public int getFreeSpaceTop() {
		return mScroll.getScrollYValue();
	}
	
	/**
	 * Calculates value in pixels that can be scrolled down.
	 * @return free space left from bottom of viewport to bottom of view.
	 */
	public int getFreeSpaceBottom() {
		return mScroll.getContentHeight() - mScroll.getViewPortHeight() - mScroll.getScrollYValue();
	}
	
	/**
	 * Calculates value in pixels that can be scrolled left.
	 * @return free space left from left side of viewport to left side of view.
	 */
	public int getFreeSpaceLeft() {
		return mScroll.getScrollXValue();
	}
	
	/**
	 * Calculates value in pixels that can be scrolled right.
	 * @return free space left from right side of viewport to right side of view.
	 */
	public int getFreeSpaceRight() {
		return mScroll.getContentWidth() - mScroll.getViewPortWidth() - mScroll.getScrollXValue();
	}
	
	
}
