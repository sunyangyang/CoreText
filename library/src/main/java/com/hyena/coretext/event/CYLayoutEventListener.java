package com.hyena.coretext.event;

import android.graphics.Rect;

/**
 * Created by yangzc on 16/4/9.
 */
public interface CYLayoutEventListener {

    void doLayout(boolean force);

    void onInvalidate(Rect rect);

    void onPageBuild();
}
