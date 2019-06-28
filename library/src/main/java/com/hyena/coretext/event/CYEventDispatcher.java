package com.hyena.coretext.event;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/9.
 */
public class CYEventDispatcher {

    private List<CYLayoutEventListener> mLayoutListeners;

    public CYEventDispatcher() {
    }

    public void set(CYEventDispatcher dispatcher) {
        clear();
        mLayoutListeners.addAll(dispatcher.mLayoutListeners);
    }

    public void addLayoutEventListener(CYLayoutEventListener listener) {
        if (mLayoutListeners == null)
            mLayoutListeners = new ArrayList<CYLayoutEventListener>();
        if (!mLayoutListeners.contains(listener)) {
            mLayoutListeners.add(listener);
        }
    }

    public void removeLayoutEventListener(CYLayoutEventListener listener) {
        if (mLayoutListeners == null)
            return;
        mLayoutListeners.remove(listener);
    }

    public void requestLayout() {
        requestLayout(true);
    }

    public void requestLayout(boolean force) {
        if (mLayoutListeners == null || mLayoutListeners.isEmpty())
            return;

        for (int i = 0; i < mLayoutListeners.size(); i++) {
            CYLayoutEventListener listener = mLayoutListeners.get(i);
            listener.doLayout(force);
        }
    }

    public void postInvalidate(Rect rect) {
        if (mLayoutListeners == null || mLayoutListeners.isEmpty())
            return;
        for (int i = 0; i < mLayoutListeners.size(); i++) {
            CYLayoutEventListener listener = mLayoutListeners.get(i);
            listener.onInvalidate(rect);
        }
    }

    public void postPageBuild() {
        if (mLayoutListeners == null || mLayoutListeners.isEmpty())
            return;
        for (int i = 0; i < mLayoutListeners.size(); i++) {
            CYLayoutEventListener listener = mLayoutListeners.get(i);
            listener.onPageBuild();
        }
    }

    public void clear() {
        if (mLayoutListeners != null) {
            mLayoutListeners.clear();
        }
    }
}
