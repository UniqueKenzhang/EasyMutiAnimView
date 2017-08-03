package com.futc.kenzhang.easymutianimview.entity;

import com.futc.kenzhang.easymutianimview.utils.AnimInfoFactory;

/**
 * Created by kenzhang on 2016/12/7.
 */

public class PathKeyPoint {

    public static final int LINEAR = 0;
    public static final int QUADRATIC = 1;
    public static final int ARC = 2;

    public float x;
    public float y;

    /**
     * 上一个点到该点的时长
     */
    public int duration;
    public int alpha = 255;
    public float scale = 1f;
    public int rotate;

    /**
     * linkStyle = QUADRATIC :应力点
     * linkStyle = ARC :圆心
     */
    public float controlX;
    public float controlY;

    /**
     * LINEAR,QUADRATIC,ARC
     */
    public int linkStyle;
    public int width = AnimInfoFactory.WARP_CONTENT;
    public int height = AnimInfoFactory.WARP_CONTENT;
}
