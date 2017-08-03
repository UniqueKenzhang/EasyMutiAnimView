package com.futc.kenzhang.easymutianimview.entity;

import java.util.ArrayList;

public class AnimInfo {

    private final int MAX_CACHE_COUNT = 20;

    public ArrayList<String> imgResource = new ArrayList<String>();
    public int key = imgResource.hashCode();
    public int count;
    public int width;
    public int spaceFlame = 160;//帧动画间隔


    public ArrayList<AnimPoint> points = new ArrayList<AnimPoint>();

    public void addImageResource(int id) {
        imgResource.add(String.valueOf(id));
    }

//    public void setDurationPerFlame(int duration) {
//        if (imgResource.size() > 0) {
//            spaceFlame = duration / (1000 / EasyMutiAnimView.FPS);
//            flameAnimFlame = (spaceFlame * flameUrl.length + 1) - 1;
//        }
//    }

}
