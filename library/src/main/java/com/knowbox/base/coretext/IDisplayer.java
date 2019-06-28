package com.knowbox.base.coretext;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface IDisplayer {
    int getWidth();
    int getHeight();
    View getWrappedView();
    boolean isCollected();
    Object getTag();
    void setImageBitmap(Bitmap bitmap);
    int getId();
    void setImageDrawable(Drawable drawable);
}
