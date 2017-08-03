package com.futc.kenzhang.easymutianimview.entity;

import com.futc.kenzhang.easymutianimview.utils.AnimInfoFactory;

/**
 * Created by kenzhang on 2016/12/7.
 */

public class PathKeyPoint {

    /**
     *上一个点于该点的轨道连接方式
     */
    public static final int LINEAR = 0;//直线轨道
    public static final int QUADRATIC = 1;//贝塞尔曲线轨道
    public static final int ARC = 2;//未实现，暂时同直线轨道

    public float x;
    public float y;

    public int duration;//上一个点到该点所经历的时长

    /**
     * 相对于原始资源的值
     */
    public int alpha = 255;//此时的alpha值，默认255（100%）
    public float scale = 1f;//此时的scale，默认1f(无放大缩小)
    public int rotate;//此时的旋转角度

    /**
     * 选择设置
     * linkStyle = LINEAR:无用
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
