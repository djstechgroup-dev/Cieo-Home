package com.kinetise.data.systemdisplay.views;

import com.kinetise.support.scrolls.scrollManager.EventDirection;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

/**
 * Interface used to help managing scrolling
 */
public interface IScrollable {
	/**
	 * Gets scroll type
	 * @return {@link ScrollType#HORIZONTAL}, {@link ScrollType#VERTICAL}, or {@link ScrollType#FREESCROLL}
	 */
	public ScrollType getScrollType();

	/**
	 * Gets scroll position
	 * @return value of scroll in horizontal axis
	 */
	public int getScrollXValue();

	/**
	 * Gets scroll position
	 * @return value of scroll in vertical axis
	 */
	public int getScrollYValue();

	/**
	 * Gets viewPortWidth, viewport is area through which user sees children of Scrollable
	 * @return width of viewport
	 */
	public int getViewPortWidth();

	/**
	 * Gets viewPortHeight, viewport is area through which user sees children of Scrollable
	 * @return Height of viewport
	 */
	public int getViewPortHeight();

	/**
	 * Gets width of this scrollable's content. Content can be bigger than scroll
	 * @return content width
	 */
	public int getContentWidth();

	/**
	 * Gets height of this scrollable's content. Content can be bigger than scroll
	 * @return content height
	 */
	public int getContentHeight();

    public String getTag();

    /**
     * Gets event direction
     * @return {@link EventDirection#VERTICAL}, {@link EventDirection#HORIZONTAL} or {@link EventDirection#UNKNOWN}
     */
    public EventDirection getEventDirectionForScrollType();

	void scrollViewTo(int x, int y);

	void restoreScroll();
}
