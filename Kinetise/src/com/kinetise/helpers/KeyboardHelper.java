package com.kinetise.helpers;

import android.view.View;
import android.widget.FrameLayout;
import com.kinetise.data.systemdisplay.views.AGBodyView;
import com.kinetise.data.systemdisplay.views.AGScreenView;

/**
 * Helper for scrolling to input position when keyboard appears
 */
public class KeyboardHelper {
	
	/**
	 * Scrolls layout to get lastFocused view in the screen
	 * @param mainLayout main view
	 * @param lastFocus last focused view
	 */
    public static void moveToDeepChild(FrameLayout mainLayout, View lastFocus) {
        AGScreenView screenView = (AGScreenView) mainLayout.getChildAt(0);
        if (screenView != null) {
            AGBodyView body = screenView.getBodyView();
            body.scrollToDeepChild(lastFocus);
        }

    }
}
