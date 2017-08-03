# EasyMutiAnimView
用于展示多并发，多钟变换符合动画的view。使用简单，编写更易阅读的代码。

## 效果
![](https://github.com/UniqueKenzhang/EasyMutiAnimView/blob/master/raw/easy_anim.gif)

## 使用

### 1.构建轨道
本控件使用关键点来构建轨道
```java
public class PathKeyPoint {

    /**
     *上一个点于该点的轨道连接方式
     */
    public static final int LINEAR = 0;//直线轨道
    public static final int QUADRATIC = 1;//贝塞尔曲线轨道
    public static final int ARC = 2;//未实现，暂时同直线轨道
    
    //相对于父控件的x，y
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
    
    //暂不设置
    public int width = AnimInfoFactory.WARP_CONTENT;
    public int height = AnimInfoFactory.WARP_CONTENT;
}

```
我个人写法
```java
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
                //point.alpha = i * 20;
                //point.scale = 1f - (0.1f * i / 2);
                //point.rotate = 1080 * i / 2;
                //point.linkStyle = PathKeyPoint.QUADRATIC;
            } else {
                point.x = xy[i];
            }
        }

```

## 2、设置动画详情
```java
        AnimInfo info = new AnimInfo();
        info.addImageResource(R.mipmap.star);//添加图片资源，暂只支持一张
        //info.addImageUrl(url);  暂使用imageloader加载图片
        info.count = Integer.MAX_VALUE;//该动画执行的数量，这里是伪无限
```

## 3、上面二者交给工厂，生成可执行动画
### xml
```xml
 <com.futc.kenzhang.easymutianimview.view.EasyMutiAnimView
        android:id="@+id/content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true" />
```
### java
```java
 mEasyMutiAnimView = (EasyMutiAnimView) findViewById(R.id.content);
 AnimEntity anim = AnimInfoFactory.getAnimInfo(points, info);//获得的便是可执行动画 AnimEntity
 //要开始显示动画的时候调用下面方法
 mEasyMutiAnimView.addAnimToQueen(anim, 300);//这里的300意思：是当Animinfo设置的数量>1的时候，每个动画的间隔时间。设置为0时即为同时开始。
```


## 初衷
现行许多直播软件中的礼物特效，往往需要对某个图片资源进行多项基础变换来组成动效。</br>
使用android系统动画进行一些基础变换代码显得复杂而且不易阅读。加之如需触发多项动画需要多个view来实现，有所消耗。</br>
所以想写一个使用编码简洁，能对图片进行基础变换,且支持多项并发的动画控件。</br>

