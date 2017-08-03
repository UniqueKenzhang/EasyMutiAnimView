package com.futc.kenzhang.easymutianimview.utils;

import android.graphics.Path;
import android.graphics.PathMeasure;

import com.futc.kenzhang.easymutianimview.entity.AnimEntity;
import com.futc.kenzhang.easymutianimview.entity.AnimInfo;
import com.futc.kenzhang.easymutianimview.entity.AnimPoint;
import com.futc.kenzhang.easymutianimview.entity.PathKeyPoint;
import com.futc.kenzhang.easymutianimview.view.EasyMutiAnimView;

import java.util.ArrayList;

/**
 * Created by kenzhang on 2016/12/8.
 * 生成动画轨道。
 */

public class AnimInfoFactory {

    public static final int WARP_CONTENT = 0;

    private static PathMeasure measureUtil = new PathMeasure();
    private static Path mCalculatePath = new Path();
    private static float[] location = new float[2];


//    public static AnimEntity randomPath(ArrayList<PathKeyPoint> keyPoints,info) {
//        getRandomPath(keyPoints, 4, 40);
//        return getAnimInfo(keyPoints);
//    }


    /**
     * @param keyPoints 轨道关键点
     * @return
     */

    public static AnimEntity getAnimInfo(ArrayList<PathKeyPoint> keyPoints, AnimInfo info) {
        AnimEntity entity = AnimEntity.getVoidObject();
        if (keyPoints.size() < 2) {
            return entity;
        }

        mCalculatePath.reset();
        PathKeyPoint lastPoint = keyPoints.get(0);
        mCalculatePath.moveTo(lastPoint.x, lastPoint.y);
        ArrayList<AnimPoint> points = info.points;


        for (int j = 1; j < keyPoints.size(); j++) {
            PathKeyPoint p = keyPoints.get(j);
            switch (p.linkStyle) {
                case PathKeyPoint.LINEAR:
                    mCalculatePath.lineTo(p.x, p.y);
                    break;
                case PathKeyPoint.QUADRATIC:
                    if (p.controlX != 0 && p.controlY != 0) {
                        mCalculatePath.quadTo(p.controlX, p.controlY, p.x, p.y);
                    } else {
                        mCalculatePath.quadTo(p.x, lastPoint.y, p.x, p.y);
                    }
                    break;
                case PathKeyPoint.ARC:
                    arcTo(mCalculatePath, p);
                    break;
            }

            measureUtil.setPath(mCalculatePath, false);
            float length = measureUtil.getLength();
            //总数代表运行帧数，也代表总时间
            int flameCount = p.duration / (1000 / EasyMutiAnimView.FPS);
            int detaDegree = p.rotate - lastPoint.rotate;
            int detaAlpha = p.alpha - lastPoint.alpha;
            float detaScale = p.scale - lastPoint.scale;

            for (float i = 0; i < flameCount; i++) {
                AnimPoint animPoint = new AnimPoint();
                measureUtil.getPosTan(length / flameCount * i, location, null);
                animPoint.scale = lastPoint.scale + detaScale / flameCount * i;
                animPoint.alpha = (int) (lastPoint.alpha + detaAlpha / flameCount * i);
                animPoint.rotate = (int) (lastPoint.rotate + detaDegree / flameCount * i);

                animPoint.x = location[0];
                animPoint.y = location[1];
                animPoint.width = p.width;
                animPoint.height = p.height;

                points.add(animPoint);
            }
            mCalculatePath.reset();
            mCalculatePath.moveTo(p.x, p.y);
            lastPoint = p;
        }

        entity.info = info;

        return entity;
    }

    /**
     * 外切圆弧轨道：未实现
     *
     * @param path
     * @param p
     */
    private static void arcTo(Path path, PathKeyPoint p) {
        path.lineTo(p.x, p.y);
    }

    private static ArrayList<PathKeyPoint> getRandomPath(ArrayList<PathKeyPoint> keyPoints, int factor, int range) {
        if (keyPoints.size() < 2) {
            return null;
        }

        PathKeyPoint start = keyPoints.get(0);
        PathKeyPoint end = keyPoints.remove(keyPoints.size() - 1);
        for (int i = 0; i < factor; i++) {
            PathKeyPoint p = new PathKeyPoint();
            float dy = Math.abs(start.y - end.y) * ((float) (i + 1) / (float) (factor + 1));
            p.y = start.y > end.y ? end.y + dy : start.y + dy;
            float miny = start.x > end.x ? end.x : start.x;
            float maxy = start.x < end.x ? end.x : start.x;
            p.x = (float) ((Math.random() * (maxy - miny) + miny) + Math.random() * range * Math.pow(-1, (int) (Math.random() * 2) + 1));
            p.linkStyle = (int) (Math.random() * 2);
            keyPoints.add(p);
        }
        keyPoints.add(end);
        return keyPoints;
    }
}
