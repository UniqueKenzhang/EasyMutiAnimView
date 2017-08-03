package com.example.kenzhang.easymutianimview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.futc.kenzhang.easymutianimview.entity.AnimEntity;
import com.futc.kenzhang.easymutianimview.entity.AnimInfo;
import com.futc.kenzhang.easymutianimview.utils.AnimInfoFactory;
import com.futc.kenzhang.easymutianimview.view.EasyMutiAnimView;
import com.futc.kenzhang.easymutianimview.entity.PathKeyPoint;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EasyMutiAnimView mEasyMutiAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEasyMutiAnimView = (EasyMutiAnimView) findViewById(R.id.content);
        mEasyMutiAnimView.init();

        final ArrayList<PathKeyPoint> points = new ArrayList<>();
        int[] xy = {
                0, 300, 900, 300,
                200, 1000, 450, 0,
                700, 1000, 0, 300
        };

        PathKeyPoint point = new PathKeyPoint();
        for (int i = 0; i < xy.length; i++) {
            if (i % 2 == 1) {
                point.y = xy[i];
                points.add(point);
                point = new PathKeyPoint();
                point.duration = 2000;
                point.alpha = i * 20;
                point.scale = 1f - (0.1f * i / 2);
                point.rotate = 1080 * i / 2;
//                point.linkStyle = PathKeyPoint.QUADRATIC;
            } else {
                point.x = xy[i];
            }
        }
        Log.e("z", points.size() + "");


        AnimInfo info = new AnimInfo();
        info.addImageResource(R.mipmap.star);
//        info.addImageUrl(url);
        info.count = Integer.MAX_VALUE;
        AnimEntity animInfo = AnimInfoFactory.getAnimInfo(points, info);
        mEasyMutiAnimView.addAnimToQueen(animInfo, 300);
    }
}
