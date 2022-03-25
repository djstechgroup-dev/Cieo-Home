package com.kinetise.data.systemdisplay.helpers;


import android.view.View;
import android.view.ViewGroup;

import com.kinetise.helpers.calcmanagerhelper.CalcManagerHelper;


public class AGViewHelper {

    public static void setHalftransparentIncludingChildren(View v) {
        setAlphaRecursively(v, 0.5f);
    }

    public static void setFullyOpaque(View v){
        setAlphaRecursively(v, 1f);
    }

    private static void setAlphaRecursively(View view, float alpha) {
        view.setAlpha(alpha);

        if (view instanceof ViewGroup) {
            setAlphaOnChildViews((ViewGroup) view, alpha);
        }
    }

    private static void setAlphaOnChildViews(ViewGroup viewGroup, float alpha) {
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            setAlphaRecursively(viewGroup.getChildAt(i), alpha);
        }
    }

    public static int[] calculateMapPinSize() {
        int PIN_SIZE = 180;
        int size = (int) Math.round(CalcManagerHelper.KPXtoPixels(PIN_SIZE));
        return new int[]{size, size};
    }
}