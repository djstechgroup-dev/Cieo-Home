package com.kinetise.support.scrolls.scrollManager.scrollStack;

import java.util.Vector;

public class ScrollStack {

	private static Vector<ScrollStackElement> mElements = new Vector<ScrollStackElement>();

	public static Vector<ScrollStackElement> getStack() {
		return mElements;
	}

	public static void clear() {
		mElements.clear();
	}

}