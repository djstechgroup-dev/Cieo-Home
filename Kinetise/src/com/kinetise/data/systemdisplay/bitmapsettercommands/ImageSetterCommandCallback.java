package com.kinetise.data.systemdisplay.bitmapsettercommands;

import android.graphics.Bitmap;

import com.kinetise.helpers.drawing.CommandCallback;

public interface ImageSetterCommandCallback extends CommandCallback{
    void loadingStarted();
    void setImageSrc(Bitmap b);

}
