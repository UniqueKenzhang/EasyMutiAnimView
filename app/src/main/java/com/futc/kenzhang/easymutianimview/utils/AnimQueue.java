package com.futc.kenzhang.easymutianimview.utils;

import android.os.Handler;
import android.os.HandlerThread;

import com.futc.kenzhang.easymutianimview.entity.AnimEntity;

import java.util.ArrayList;

/**
 * Created by kenzhang on 2016/12/13.
 */

public class AnimQueue {
    private final String TAG = "anim_queue";

    public static final int MAX_QUEUE_SIZE = 10;

    private static AnimQueue instance;
    private final Handler mPostHandler;
    private ArrayList<AnimEntity> queue = new ArrayList<AnimEntity>();
    private ArrayList<AnimEntity> workThreadTouchableQueue = new ArrayList<AnimEntity>();

    private AnimQueue() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mPostHandler = new Handler(handlerThread.getLooper());
    }

    public static AnimQueue getInstance() {
        if (instance == null) {
            synchronized ("lock") {
                if (instance == null) {
                    instance = new AnimQueue();
                }
            }
        }
        return instance;
    }

    public void addToQueue(final AnimEntity p, final int delay) {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                AnimEntity voidObject = AnimEntity.getVoidObject();
                voidObject.info = p.info;
                queue.add(voidObject);
                if (p.info.count > 0) {
                    p.info.count--;
                    mPostHandler.postDelayed(this, delay);
                }
            }
        };
        mPostHandler.post(task);
    }

    public ArrayList<AnimEntity> getQueue() {
        workThreadTouchableQueue.clear();
        workThreadTouchableQueue.addAll(queue);
        return workThreadTouchableQueue;
    }

    public int size() {
        return queue.size();
    }

    public boolean isToLarge() {
        return queue.size() > MAX_QUEUE_SIZE;
    }

    public void removeFromQueue(AnimEntity p) {
        queue.remove(p);
        p.recycle();
    }

    public void clear() {
        queue.clear();
    }
}
