package com.kinetise.helpers.drawing;

import android.graphics.Bitmap;

public interface BackgroundSetterCommandCallback extends CommandCallback{
    void setBackgroundBitmap(Bitmap bitmap);
}
