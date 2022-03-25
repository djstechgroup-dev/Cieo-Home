/*package com.funandmobile.kinetise.support.scrolls.scrollManager;

import com.funandmobile.kinetise.support.scrolls.IScrollable;

public class ScrollFreeSpaces {

	private int mFreeSpaceTop;
	private int mFreeSpaceBottom;
	private int mFreeSpaceLeft;
	private int mFreeSpaceRight;
	
	public ScrollFreeSpaces(IScrollable scroll){
		init(scroll);
	}
	
	public void init(IScrollable scroll){
	
		//Vertical
		mFreeSpaceTop = scroll.getScrollYValue();
		mFreeSpaceBottom = scroll.getContentHeight() - scroll.getViewPortHeight() - scroll.getScrollYValue();
		
		//Horizontal
		mFreeSpaceLeft = scroll.getScrollXValue();
		mFreeSpaceRight = scroll.getContentWidth() - scroll.getViewPortWidth() - scroll.getScrollXValue();
	
	}
	
	public int getFreeSpaceTop() {
		return mFreeSpaceTop;
	}
	public int getFreeSpaceBottom() {
		return mFreeSpaceBottom;
	}
	public int getFreeSpaceLeft() {
		return mFreeSpaceLeft;
	}
	public int getFreeSpaceRight() {
		return mFreeSpaceRight;
	}
	
}
*/