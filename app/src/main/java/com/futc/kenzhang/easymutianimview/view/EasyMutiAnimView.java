package com.futc.kenzhang.easymutianimview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.futc.kenzhang.easymutianimview.entity.AnimEntity;
import com.futc.kenzhang.easymutianimview.entity.AnimPoint;
import com.futc.kenzhang.easymutianimview.entity.ImagePackage;
import com.futc.kenzhang.easymutianimview.utils.AnimQueue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * 1、surface动画，绘图完全于子线程处理，性能较好，不会阻塞主线程，但在性能较低时会出现拖帧情况。
 * 2、传统动画无法适应多并发动画，虽现在无需求。
 * 3、轨道自由，通过path类设定轨道，可使用各种不规则轨道，虽无需求。
 *
 * @author kenzhang
 */

public class EasyMutiAnimView extends SurfaceView implements SurfaceHolder.Callback {

    public static int FPS = 24;
    private final int BITMAP_MAX_CACHE_TIME = 10000;
    private int minWaitTime = 1000 / FPS;

    private CanvasThread thread;
    private RectF drawRectF = new RectF();

    private SparseArray<ImagePackage> mImageMap = new SparseArray< >();
    private DisplayImageOptions options;
    public boolean hasInit;
    private boolean mIsWaiting;
    private final Object mLock = new Object();
    private Matrix matrix = new Matrix();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public EasyMutiAnimView(Context context) {
        super(context);
    }

    public EasyMutiAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyMutiAnimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new CanvasThread(holder);
        thread.setRun(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread.setRun(false);
        thread = null;
        AnimQueue.getInstance().clear();
        if (mIsWaiting) {
            synchronized (mLock) {
                mLock.notify();
                mIsWaiting = false;
            }
        }
    }

    public void init() {
        hasInit = true;
        this.options = new DisplayImageOptions.Builder()
                .cacheOnDisk(false).cacheInMemory(false).bitmapConfig(Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();

        //holder绘图线程初始化
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);
    }

    public void addAnimToQueen(final AnimEntity p) {
        addAnimToQueen(p, 0);
    }

    public void addAnimToQueen(final AnimEntity p, int delay) {
        if (p == null)
            return;

        if (mIsWaiting) {
            synchronized (mLock) {
                mLock.notify();
                mIsWaiting = false;
            }
        }

        AnimQueue.getInstance().addToQueue(p, delay);
    }

    private class CanvasThread extends Thread {

        private boolean isRun;
        private SurfaceHolder holder;
        private Paint paint;
        private PorterDuffXfermode clear;
        private PorterDuffXfermode src;
        private int waitCount;

        CanvasThread(SurfaceHolder holder) {
            this.holder = holder;
            this.paint = new Paint();
            clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            src = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        }

        @Override
        public void run() {
            while (isRun) {
                if (AnimQueue.getInstance().size() != 0) {
                    long startTime = System.currentTimeMillis();

                    Canvas c = holder.lockCanvas();
                    if (c != null) {
                        paint.setXfermode(clear);
                        c.drawPaint(paint);
                        paint.setXfermode(src);

                        ArrayList<AnimEntity> queue = AnimQueue.getInstance().getQueue();
                        for (final AnimEntity p : queue) {

                            ImagePackage imagePackage = mImageMap.get(p.info.key);
                            if (imagePackage == null) {
                                imagePackage = new ImagePackage();
                                mImageMap.put(p.info.key, imagePackage);
                            }

                            if (prepareImage(p, imagePackage)) {

                                imagePackage.lastUseedTime = startTime;
                                Bitmap bitmap = imagePackage.images[0];
                                int flameCount = p.info.points.size();

                                if (p.position < flameCount) {
                                    AnimPoint animPoint = p.info.points.get(p.position);
                                    if (animPoint.width == 0 || animPoint.height == 0) {
                                        animPoint.width = bitmap.getWidth();
                                        animPoint.height = bitmap.getHeight();
                                    }

                                    drawRectF.set(animPoint.x, animPoint.y, animPoint.x + animPoint.width, animPoint.y + animPoint.height);
                                    matrix.reset();
                                    matrix.setTranslate(animPoint.x, animPoint.y);
                                    matrix.postScale(animPoint.scale, animPoint.scale, drawRectF.centerX(), drawRectF.centerY());
                                    matrix.postRotate(animPoint.rotate, drawRectF.centerX(), drawRectF.centerY());

                                    paint.setAlpha(animPoint.alpha);
                                    c.drawBitmap(bitmap, matrix, paint);
                                    p.position++;
                                } else {
                                    //动画完成移除队列
                                    AnimQueue.getInstance().removeFromQueue(p);
                                }
                            } else {
                                continue;
                            }
                        }

                        holder.unlockCanvasAndPost(c);
                        try {
                            long wait = System.currentTimeMillis() - startTime;
//                            Log.e("z", "wait:" + wait + " min:" + minWaitTime);

                            wait = wait > minWaitTime ? 0 : minWaitTime - wait;
                            Thread.sleep(wait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        if (waitCount < 10) {
                            waitCount++;
                            Thread.sleep(700);
                            clearImgCache(false);
                        } else {
                            waitCount = 0;
                            synchronized (mLock) {
                                Log.e("z", "surface_wait---------------------->");
                                mIsWaiting = true;
                                mLock.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.e("z", "surface_end---------------------->");
            clearImgCache(true);
        }

        public void setRun(boolean isRun) {
            this.isRun = isRun;
        }

        private void clearImgCache(boolean isImmediate) {
            int size = mImageMap.size();
            for (int i = 0; i < size; i++) {
                ImagePackage p = mImageMap.valueAt(i);
                long currentTimeMillis = System.currentTimeMillis();
                if (p != null && (p.lastUseedTime != 0 && currentTimeMillis - p.lastUseedTime > BITMAP_MAX_CACHE_TIME || isImmediate)) {
                    Bitmap[] bs = p.images;
                    if (bs != null) {
                        for (Bitmap flame : bs) {
                            if (flame != null && !flame.isRecycled())
                                flame.recycle();
                        }
                        p.images = null;
                    }
                    mImageMap.removeAt(i);
                }
            }
        }

    }

    public boolean prepareImage(final AnimEntity p, final ImagePackage imagePackage) {
        if (!imagePackage.isOnImageLoading) {
            imagePackage.isOnImageLoading = true;
            if (!ImageLoader.getInstance().isInited()) {
                ImageLoaderConfiguration defaultConfiguration = ImageLoaderConfiguration.createDefault(getContext());
                ImageLoader.getInstance().init(defaultConfiguration);
            }

            int size = p.info.imgResource.size();
            imagePackage.images = new Bitmap[size];
            for (int i = 0; i < size; i++) {
                final int index = i;
                String res = p.info.imgResource.get(i);
                if (res.startsWith("http")) {
                    ImageLoader.getInstance().loadImage(res, options,
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    Log.e("z", index + "bitmap");
                                    imagePackage.images[index] = loadedImage;
                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    Log.e("z", index + "bitmap fail");
                                    AnimQueue.getInstance().removeFromQueue(p);
                                }
                            });
                } else {
                    imagePackage.images[i] = BitmapFactory.decodeResource(getResources(), Integer.valueOf(res));
                }
            }
        }

        boolean result = false;


        if (!imagePackage.isLoadCompleted) {
            if (p.info.imgResource.size() > 0) {
                for (Bitmap b : imagePackage.images) {
                    if (b != null) {
                        result = true;
                        imagePackage.isLoadCompleted = true;
                    } else {
                        result = false;
                        break;
                    }
                }
            }
        }

        return result || imagePackage.isLoadCompleted;

    }
}
