package com.hyena.coretext.event;

import android.graphics.Rect;

/**
 */
public interface CYLayoutEventListener {

    void doLayout(boolean force);

    void onInvalidate(Rect rect);

    void onPageBuild();
}
