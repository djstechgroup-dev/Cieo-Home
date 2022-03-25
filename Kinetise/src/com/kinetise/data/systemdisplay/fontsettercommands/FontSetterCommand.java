package com.kinetise.data.systemdisplay.fontsettercommands;

import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.data.systemdisplay.views.FontSetterCallback;

/**
 * Setter command that sets font typeface on given TextView
 */
public class FontSetterCommand extends AbstractGetSourceCommand<Typeface> {

    private final View mView;
    private final int mFontStyleFlag;

    public FontSetterCommand(String source, View view, int fontStyleFlag) {
        super("", source);
        mView = view;
        mFontStyleFlag = fontStyleFlag;
        if(mView instanceof TextView)
            ((TextView) mView).setTypeface(null, mFontStyleFlag);
    }

    public void postGetSource(Typeface typeface) {
        if(mView instanceof FontSetterCallback)
            ((FontSetterCallback) mView).setTypeface(typeface);
        else if(mView instanceof TextView){
            ((TextView) mView).setTypeface(typeface, mFontStyleFlag);
        }
    }

    public void onError() {
        // nothing to do
    }

    public void cancel() {
        // nothing to do
    }

    public Object[] getParams() {
        // nothing to do
        return null;
    }
}
