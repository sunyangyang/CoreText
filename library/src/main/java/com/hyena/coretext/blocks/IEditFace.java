package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by yangzc on 17/6/28.
 */

public interface IEditFace {

    /**
     * 绘制
     * @param canvas canvas
     * @param blockRect 整体大小
     * @param contentRect 内容大小
     */
    void onDraw(Canvas canvas, Rect blockRect, Rect contentRect);

    /**
     * 是否进入编辑状态
     * @param edit 编辑状态
     */
    void setInEditMode(boolean edit);

    /**
     * 重新开始
     */
    void restart();

    /**
     * 停止
     */
    void stop();
}
