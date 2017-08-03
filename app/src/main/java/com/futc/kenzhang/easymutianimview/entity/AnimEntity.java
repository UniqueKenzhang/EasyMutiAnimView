package com.futc.kenzhang.easymutianimview.entity;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kenzhang on 2017/8/2.
 */

public class AnimEntity {
    private final int MAX_CACHE_COUNT = 20;

    public AnimInfo info;
    public int position;
    public int spaceFlame = 160;//帧动画间隔帧数
    public boolean isOnImageLoading;


    private static ArrayList<AnimEntity> mCache = new ArrayList<AnimEntity>();

    private AnimEntity() {
    }

    public synchronized static AnimEntity getVoidObject() {
        if (mCache.size() > 0) {
            Log.e("Z", "get present cache");
            return mCache.remove(0);
        } else {
            return new AnimEntity();
        }
    }

    public synchronized static AnimEntity getObject() {
        if (mCache.size() > 0) {
            Log.e("Z", "get present cache");
            return mCache.remove(0);
        } else {
            return new AnimEntity();
        }
    }

    public void recycle() {
        isOnImageLoading = false;
        position = 0;
        mCache.add(this);
    }

//    public void setDurationPerFlame(int duration) {
//        if (flameUrl != null) {
//            spaceFlame = duration / (1000 / EasyMutiAnimView.FPS);
//            flameAnimFlame = (spaceFlame * flameUrl.length + 1) - 1;
//        }
//    }
}
