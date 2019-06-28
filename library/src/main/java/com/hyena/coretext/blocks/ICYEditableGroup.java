/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import java.util.List;

/**
 * Created by yangzc on 17/2/8.
 */
public interface ICYEditableGroup {

    ICYEditable findEditable(float x, float y);

    ICYEditable getFocusEditable();

    ICYEditable findEditableByTabId(int tabId);

    List<ICYEditable> findAllEditable();
}
