package com.kinetise.data.systemdisplay.views.scrolls;

import android.view.View;
import android.view.ViewGroup;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

public abstract class AGDataFeedScrollView extends AGScrollView {

    public AGDataFeedScrollView(SystemDisplay display, AbstractAGContainerDataDesc desc, ScrollType scrollType) {
        super(display, desc, scrollType);
    }

    protected void attachChild(View view, boolean isRecycled) {
        Logger.d("Adapter", "attachChild");
        LayoutParams p;
        p = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0);

        //0 dlatego ze nie ma znaczenia ktory index ma dziecko w rodzicu,a w przypadku recyclingu widokow index nigdy nie bedzie wiekszy niz maksymalna ilosc dzieci na ekranie.
        if (isRecycled && view.getHandler() != null) {
            attachViewToParent(view, 0, p);
        } else {
            addViewInLayout(view, 0, p, true);
        }
    }

    /**
     * Metoda odpowiedzialna za wyswietlenie dziecka na liscie, tworzy dla niego LayoutParams
     * podczepia mu parenta, wywoluje layout (UWAGA!!! w kinetise parametry layout sa bardzo czesto ignorowane, a zamiast tego korzysta ona z calc deskryptora
     *
     * @param view  widok do dodania na liste
     */
    protected void layoutChild(View view) {
        Logger.d("Adapter","LayoutChild");
        AbstractAGViewDataDesc v = (AbstractAGViewDataDesc) ((IAGView)view).getDescriptor();
        AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
        AGViewCalcDesc childCalcDesc = v.getCalcDesc();

        // sizes
        int width = (int) (Math.round(childCalcDesc.getWidth() + Math.round(childCalcDesc.getBorder().getHorizontalBorderWidth())));
        int height = (int) (Math.round(childCalcDesc.getHeight() + Math.round(childCalcDesc.getBorder().getVerticalBorderHeight())));

        int left = (int) Math.round(calcDesc.getPaddingLeft() + calcDesc.getBorder().getLeftAsInt() + Math.round(childCalcDesc.getPositionX()) + childCalcDesc.getMarginLeft());
        int top = (int) Math.round(calcDesc.getPaddingTop() + calcDesc.getBorder().getTopAsInt() + Math.round(childCalcDesc.getPositionY()) + childCalcDesc.getMarginTop());
        int right = left + width;
        int bottom = top + height;
        view.layout(left, top, right, bottom);
        invalidate();
    }

    public void detachView(View view) {
        detachViewFromParent(view);
    }

}
