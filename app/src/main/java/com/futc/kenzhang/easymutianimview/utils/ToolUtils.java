
package com.futc.kenzhang.easymutianimview.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ToolUtils {

    private static Drawable drawable = null;

    private static int screenWidth, screenHeight;


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
