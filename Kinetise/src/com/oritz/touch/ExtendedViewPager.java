package com.oritz.touch;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;


public class ExtendedViewPager extends ViewPager {

	public ExtendedViewPager(Context context) {
	    super(context);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
	    if (v instanceof TouchImageView) {
	    	//
	    	// canScrollHorizontally is not supported for Api < 14. To get around this issue,
	    	// ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
	    	// canScrollHorizontally supporting Api >= 8, is called.
	    	//
	        return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
	        
	    } else {
	        return super.canScroll(v, checkV, dx, x, y);
	    }
	}

}
