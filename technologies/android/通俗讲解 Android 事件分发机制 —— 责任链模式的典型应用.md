@[TOC](目录)
# 一、家族规矩
Android的控件家族的家里有个规矩：

	1.当一个 帅气的小哥哥 or 美丽的小姐姐 给Android控件家族送来一个蛋糕

	---每次都是爷爷先拿到蛋糕，爷爷来发蛋糕，按照老规矩，爷爷会把这个蛋糕发给爸爸，让爸爸来发蛋糕；

	---爸爸拿到蛋糕，经过妈妈的同意后，把蛋糕发给儿子，让儿子来发蛋糕；

	---儿子拿到蛋糕，发给自己，然后Android控件家族开始准备吃蛋糕；

	---儿子先决定吃不吃蛋糕，不吃的话就还给爸爸；

	---爸爸又拿到了蛋糕，决定自己吃不吃，不吃的话就还给爷爷。

	---爷爷决定自己吃掉，或者丢掉

	2.这是祖祖辈辈的老规矩，如果谁想吃蛋糕，就在中间环节打断。

这个规矩是很民主的，每个人都有发蛋糕和吃蛋糕的权利，所以每个人都有机会吃到蛋糕。


	在这个例子中：
	爷爷是Activity
	爸爸是ViewGroup
	儿子是View
	妈妈是onInterceptTouchEvent()
	蛋糕就是点击事件
	帅气的小哥哥 or 美丽的小姐姐就是屏幕前的各位。


# 二、事件分发机制分析
## 1.爷爷先拿到蛋糕，爷爷来发蛋糕，按老规矩是发给爸爸；但是他也可以选择自己直接吃掉，发蛋糕活动就结束了；爷爷还可以选择不往下面发蛋糕了，爷爷自己又不吃，就决定给自己的父辈吃，但爷爷是Android家族里最年长的，上面没有父辈，所以发蛋糕活动也结束了。这个蛋糕就被浪费了。

等同于：Activity收到一个点击事件，用dispatchTouchEvent分发这个事件，按照老规矩，也就是return super.dispatchTouchEvent()，把点击事件发给ViewGroup；Activity也可以选择自己消费掉，也就是在 dispatchTouchEvent 中 return true，点击事件分发就终止了；Activity还可以选择不往下面下发了，也就是在 dispatchTouchEvent 中 return false，这个点击事件会回到上一级，但是Activity没有上一级，所以事件分发也终止了。



## 2.要是爷爷按照老规矩发给了爸爸。爸爸按照老规矩是发给儿子；爸爸也可以选择自己吃掉，发蛋糕活动就结束了；爸爸还可以选择不往下面发蛋糕了，这个蛋糕就会还给父辈，也就是爷爷，给爷爷决定吃掉或者丢掉。

等同于：Activity调用了return super.dispatchTouchEvent()把点击事件给了ViewGroup，ViewGroup用dispatchTouchEvent决定怎么分发这个事件，按照老规矩，也就是return super.dispatchTouchEvent()，把点击事件发给View；ViewGroup也可以选择自己消费掉，也就是在 dispatchTouchEvent 中 return true，点击事件分发就终止了；ViewGroup还可以选择不往下面下发了，这个点击事件会回到上一级，也就是Activity，在Activity中的onTouchEvent()中消费掉或者不处理。



## 3.爸爸发给儿子之前会先经过妈妈的拦截。要是爸爸想要直接把蛋糕发给自己吃，需要经过妈妈的同意，如果妈妈同意了，就不再分蛋糕。爸爸直接开始决定吃不吃蛋糕。妈妈按老规矩是不同意爸爸直接吃，而是应该分给儿子。

等同于：ViewGroup下发给View的过程中会经过onInterceptTouchEvent()的拦截，如果onInterceptTouchEvent()中返回true，则不再分发事件，直接到达ViewGroup的onTouchEvent()中处理点击事件。如果onInterceptTouchEvent()中返回false或返回super.onInterceptTouchEvent()，则按照老规矩把点击事件分发给View。


## 4.要是爸爸按照老规矩发给了儿子，儿子按照老规矩是发给自己，然后开始吃蛋糕活动；同样，儿子可以选择直接吃掉，发蛋糕活动就结束了；儿子也可以选择不往下面发蛋糕了，这个蛋糕就会还给父辈，也就是爸爸，爸爸会决定吃掉或者给爷爷。

等同于：ViewGroup调用了return super.dispatchTouchEvent()把点击事件给了View，View按照老规矩是发给自己，也就是在dispatchTouchEvent 中 return super.dispatchTouchEvent() 的话，事件分发就结束了，然后开始决定由谁消费这个点击事件；同样，View也可以选择直接消费掉，也就是在 dispatchTouchEvent 中 return true，点击事件分发就终止了；View也可以选择不往下面下发了，这个点击事件会回到上一级，也就是ViewGroup，在ViewGroup中的onTouchEvent()中消费掉或者传到Activity。

## 5.要是儿子按照老规矩发给了自己，开始吃蛋糕活动。儿子决定自己吃不吃，自己吃掉的话，吃蛋糕活动结束；按照老规矩是自己不吃，拿给爸爸决定吃不吃。

等同于：View调用了return super.dispatchTouchEvent()把点击事件发给了自己。开始决定谁消费点击事件。在View的onTouchEvent()中return true或者false，如果View选择消费掉，也就是return true，事件分发结束。return super.onTouchEvent()和return false一样，都是交给ViewGroup处理。


## 6.爸爸又拿到了蛋糕，决定自己吃不吃，自己吃掉的话，吃蛋糕活动结束；按照老规矩是自己不吃，拿给爷爷决定吃不吃。

等同于：ViewGroup又拿到了点击事件。决定是否消费这个点击事件。如果View选择消费掉，也就是在ViewGroup的onTouchEvent()中return true，事件分发结束。return super.onTouchEvent()和return false一样，都是交给Activity处理。


## 7.爷爷又拿到了蛋糕，爷爷决定自己吃掉，或者丢掉。

等同于：Activity又拿到了点击事件，选择在onTouchEvent()中return true，消费掉；或者return false 或return super.onTouchEvent()，不做任何处理。


# 三、事件分发机制伪代码
在《Android开发艺术探索》一书中有一段伪代码，将dispatchTouchEvent，onInterceptTouchEvent和onTouchEvent的关系表现得“淋漓尽致”，伪代码如下：
```java
public boolean dispatchTouchEvent(MotionEvent ev){
    boolean consume = false;
    if(onInterceptTouchEvent(ev)){
        consume = onTouchEvent(ev);
    }else{
        consume = child.dispatchTouchEvent(ev);
    }
    return consume;
}
```
原理和上文的解释是一样的。以上，就是Android 的View分发机制，在设计模式中属于典型的责任链模式。

整个机制的示意图：
![事件分发机制](https://img-blog.csdnimg.cn/20191121103449953.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =800x)

# 四、一个使用事件分发机制解决滑动冲突的例子

## 效果图
![](https://img-blog.csdn.net/20180322231805473?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

这是笔者所在公司最近一个项目的部分UI设计。我采用了一个垂直的ViewPager+ScrollView来实现。



## 代码实现

1.两个fragment，由于不是本次讲解的重点，所以直接上代码

上面的主页面HomeFragment：

```java
public class HomeFragment extends Fragment {
    private View rootView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }
}
```


布局fragment_home.xml

 ```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <TextView
        android:id="@+id/tv_temperature"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="22°C"
        android:textSize="64sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        android:layout_margin="16dp" />
</android.support.constraint.ConstraintLayout>
```

下面的天气详情页面DetailFragment：

```java
public class DetailFragment extends Fragment {
    private View rootView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }
}
```

布局fragment_detail.xml：

 ```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/sv_detail"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.constraint.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <!--weather 卡片-->
        <android.support.v7.widget.CardView
            android:id="@+id/card_weather"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="16dp"
            app:cardBackgroundColor="#4f000000"
            app:cardCornerRadius="3dp"
            app:layout_constraintTop_toTopOf="parent">

            <android.support.constraint.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent">

                <TextView
                    android:id="@+id/tv_weather"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="3dp"
                    android:text="WEATHER"
                    android:textColor="@android:color/white"
                    app:layout_constraintLeft_toLeftOf="parent"
                    app:layout_constraintRight_toRightOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <View
                    android:id="@+id/v_temp_line"
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:layout_marginLeft="22dp"
                    android:layout_marginRight="22dp"
                    android:layout_marginTop="3dp"
                    android:background="@android:color/white"
                    app:layout_constraintTop_toBottomOf="@id/tv_weather" />

                <TextView
                    app:layout_constraintTop_toBottomOf="@id/v_temp_line"
                    android:layout_width="wrap_content"
                    android:layout_height="300dp" />

            </android.support.constraint.ConstraintLayout>
        </android.support.v7.widget.CardView>

        <!--detail 卡片-->
        <android.support.v7.widget.CardView
            android:id="@+id/card_detail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="16dp"
            app:cardBackgroundColor="#4f000000"
            app:cardCornerRadius="3dp"
            app:layout_constraintTop_toBottomOf="@id/card_weather">

            <android.support.constraint.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent">

                <TextView
                    android:id="@+id/tv_detail"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="3dp"
                    android:text="DETAIL"
                    android:textColor="@android:color/white"
                    app:layout_constraintLeft_toLeftOf="parent"
                    app:layout_constraintRight_toRightOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <View
                    android:id="@+id/v_detail_line"
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:layout_marginLeft="22dp"
                    android:layout_marginRight="22dp"
                    android:layout_marginTop="3dp"
                    android:background="@android:color/white"
                    app:layout_constraintTop_toBottomOf="@id/tv_detail" />

                <TextView
                    app:layout_constraintTop_toBottomOf="@id/v_detail_line"
                    android:layout_width="wrap_content"
                    android:layout_height="300dp" />
            </android.support.constraint.ConstraintLayout>
        </android.support.v7.widget.CardView>

        <!--climate 卡片-->
        <android.support.v7.widget.CardView
            android:id="@+id/card_climate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="16dp"
            app:cardBackgroundColor="#4f000000"
            app:cardCornerRadius="3dp"
            app:layout_constraintTop_toBottomOf="@id/card_detail">

            <android.support.constraint.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent">

                <TextView
                    android:id="@+id/tv_climate"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="3dp"
                    android:text="CLIMATE"
                    android:textColor="@android:color/white"
                    app:layout_constraintLeft_toLeftOf="parent"
                    app:layout_constraintRight_toRightOf="parent"
                    app:layout_constraintTop_toTopOf="parent" />

                <View
                    android:id="@+id/v_climate_line"
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:layout_marginLeft="22dp"
                    android:layout_marginRight="22dp"
                    android:layout_marginTop="3dp"
                    android:background="@android:color/white"
                    app:layout_constraintTop_toBottomOf="@id/tv_climate" />

                <TextView
                    app:layout_constraintTop_toBottomOf="@id/v_climate_line"
                    android:layout_width="wrap_content"
                    android:layout_height="300dp" />

            </android.support.constraint.ConstraintLayout>
        </android.support.v7.widget.CardView>
    </android.support.constraint.ConstraintLayout>
</ScrollView>
```

由于用到了Material Design的CardView，在Module: app的dependencied{}中引入Material Design：
```xml
implementation 'com.android.support:cardview-v7:27.1.0'
```

2.垂直ViewPager是采用的Github上的一个开源项目,原理是把垂直滑动的手势，转换成了横向滑动的手势，以达到垂直滑动切换ViewPager页面的效果，再给 ViewPager 设置一个垂直切换的 Transfromer，就达到了垂直ViewPager的效果。


转换了手势的VerticalViewPager代码：


```java
public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(Context context) {
        super(context);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();
        event.setLocation((event.getY() / height) * width, (event.getX() / width) * height);
        return event;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapTouchEvent(MotionEvent.obtain(ev)));
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(swapTouchEvent(MotionEvent.obtain(event)));
    }
}
```

垂直Transformer代码：

```java
public class DefaultTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        float alpha = 0;
        if (0 <= position && position <= 1) {
            alpha = 1 - position;
        } else if (-1 < position && position < 0) {
            alpha = position + 1;
        }
        view.setAlpha(alpha);
        view.setTranslationX(view.getWidth() * -position);
        float yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }
}
```

在MainActivity中设置一下：

```java
viewPager.setPageTransformer(true, new DefaultTransformer());
viewPager.setOverScrollMode(OVER_SCROLL_NEVER);
```
这样就实现了垂直ViewPager。

3.MainActivity布局：


```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <com.cassin.toucheventstudy.VerticalViewPager
        android:id="@+id/view_pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</android.support.constraint.ConstraintLayout>
```


在MainActivity设置好ViewPager，完整代码：




```java
public class MainActivity extends AppCompatActivity {
    private ArrayList<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VerticalViewPager viewPager = findViewById(R.id.view_pager);
        list.add(new HomeFragment());
        list.add(new DetailFragment());
        viewPager.setOffscreenPageLimit(2);//缓存相邻的两个页面，防止切换时重新加载fragment
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        viewPager.setPageTransformer(true, new DefaultTransformer());
        viewPager.setOverScrollMode(OVER_SCROLL_NEVER);
    }
}
```


4.按照上面的步骤设置好后，就会发现ScrollView和ViewPager存在滑动冲突，滑动到DetailFragment顶部之后，下拉时有时候会无法返回到HomeFragment。如下图：

![](https://img-blog.csdn.net/20180323005950305?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)


## 解决滑动冲突

在VertivalViewPager的onInterceptTouchEvent()中拦截事件：


```java
private float yDown = 0;//记录手指按下时的y坐标值
private boolean isDown = false;//记录是否是在ScrollView顶部下拉

@Override
public boolean onInterceptTouchEvent(MotionEvent event) {
    Activity activity = (Activity) getContext();
    ScrollView scrollView = activity.findViewById(R.id.sv_detail);
    if(scrollView.getScrollY() == 0){//ScrollView在顶部时判断手势
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDown = false;//初始化isDown
                yDown = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if((event.getY() - yDown) > 0){
                    isDown = true;//如果手指移动时的y坐标值大于手指按下时的坐标值，表示在下拉
                }
                break;
        }
        if(isDown){
            return true;//如果是在ScrollView顶部下拉，ViewPager拦截点击事件
        }else{
            return super.onInterceptTouchEvent(swapTouchEvent(MotionEvent.obtain(event)));
        }
    }
    return super.onInterceptTouchEvent(swapTouchEvent(MotionEvent.obtain(event)));
}
```
当页面是在ScrollView顶部下拉的时候，ViewPager拦截点击事件。否则的话就按照老规矩，即 return super.onInterceptTouchEvent() ，交给ViewPager的孩子ScrollView处理。

# 源码已上传
[https://github.com/wkxjc/TouchEventStudy](https://github.com/wkxjc/TouchEventStudy)


参考：

[图解Android事件分发机制：https://www.jianshu.com/p/e99b5e8bd67b](https://www.jianshu.com/p/e99b5e8bd67b)

[VerticalViewPager: https://github.com/kaelaela/VerticalViewPager](https://github.com/kaelaela/VerticalViewPager)