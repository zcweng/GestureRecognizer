package com.pierce.ges;

/**
 * Created by izhaowo on 16/9/2.
 */
public interface GestureListener {
    /**
     * 单击事件
     */
    void onClick(float x, float y);

    /**
     * 正在滑动
     */
    void onScroll(float sx, float sy, float ex, float ey);

    void onRotate(double angle, float cx, float cy);

    void onScale(double scale, float cx, float cy);
}
