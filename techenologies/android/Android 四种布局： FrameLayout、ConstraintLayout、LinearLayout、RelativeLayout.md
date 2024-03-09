@[TOC]
# 一、ConstraintLayout（约束布局）
约束布局是谷歌推荐使用的布局，官方文档：[https://developer.android.google.cn/reference/android/support/constraint/ConstraintLayout.html](https://developer.android.google.cn/reference/android/support/constraint/ConstraintLayout.html)
## 效果图
![](https://img-blog.csdn.net/20180313120040417?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

## 代码实现
```xml
<android.support.constraint.ConstraintLayout xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:android="http://schemas.android.com/apk/res/android" >

    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="居中效果"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="左上角"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintLeft_toLeftOf="parent" />

    <Button
        android:id="@+id/button3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="右上角"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintRight_toRightOf="parent" />

    <Button
        android:id="@+id/button4"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="左下角"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"/>
    <Button
        android:id="@+id/button5"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="右下角"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
    <Button
        android:id="@+id/button6"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="在按钮上面"
        app:layout_constraintBottom_toTopOf="@id/button1"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        />
    <Button
        android:id="@+id/button7"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="在按钮下面"
        app:layout_constraintTop_toBottomOf="@id/button1"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        />
    <Button
        android:id="@+id/button8"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="在按钮左边"
        app:layout_constraintRight_toLeftOf="@id/button1"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        />
    <Button
        android:id="@+id/button9"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="在按钮右边"
        app:layout_constraintLeft_toRightOf="@id/button1"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        />
    <Button
        android:id="@+id/button10"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="水平20%，垂直20%"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintHorizontal_bias="0.20"
        app:layout_constraintVertical_bias="0.20"
        />
</android.support.constraint.ConstraintLayout>
```
## 解析
  约束布局根据控件的上下左右约束关系确定位置，如果不添加任何约束，默认位置在屏幕左上角

![](https://img-blog.csdn.net/20180313135445450?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

每一个控件都有上下左右四个点，可以为控件添加点对点的约束，父布局同样有上下左右四个点。


### 1.控件与父布局约束
控件左边的点和父布局左边的点添加约束的写法为：
```xml
app:layout_constraintLeft_toLeftOf="parent"
```
layout_constraintLeft_toLeftOf中的第一个Left是指控件左边的点，第二个Left是指父布局左边的点。所以这一句的意思就是控件左边的点被父布局左边的点约束。
同理，控件右边的点和父布局右边的点添加约束的写法为：
```xml
app:layout_constraintRight_toRightOf="parent"
```
控件上面的点和父布局上面的点添加约束的写法为：
```xml
app:layout_constraintTop_toTopOf="parent"
```
 控件下面的点和父布局下面的点添加约束的写法为：
```xml
 app:layout_constraintBottom_toBottomOf="parent"
 ```
 当一个控件上下左右都被父布局上下左右约束的时候，控件位置位于父布局中心。
 
 
### 2.一个方向上只添加一个约束实现贴边效果
当一个控件左右两个点（或上下两个点）只有其中一个点添加了约束，则贴着有约束的这一边。
位于左上角：
```xml
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintLeft_toLeftOf="parent" 
```
位于右上角：
```xml
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintRight_toRightOf="parent"
```
位于左下角：
```xml
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintLeft_toLeftOf="parent"
```
位于右下角：
```xml
app:layout_constraintBottom_toBottomOf="parent"
app:layout_constraintRight_toRightOf="parent"
```

### 3.控件和控件的点添加约束实现相对布局的效果

在按钮上面，将控件下面的点和“按钮”上面的点添加约束：
```xml
app:layout_constraintBottom_toTopOf="@id/button1"
```
在按钮下面，将控件上面的点和“按钮”下面的点添加约束：
```xml
app:layout_constraintTop_toBottomOf="@id/button1"
```
在按钮左边，将控件右边的点和“按钮”左面的点添加约束：
```xml
app:layout_constraintRight_toLeftOf="@id/button1"
```
在按钮右边，将控件上面的点和“按钮”下面的点添加约束：
```xml
app:layout_constraintLeft_toRightOf="@id/button1"
```
### 4.百分比布局

 当一个控件添加左右约束之后，可以通过水平百分比设置控件相对于左右约束的比例，属性是layout_constraintHorizontal_bias，左边为0，右边为1，设置水平20%的属性写法为：
```xml
app:layout_constraintHorizontal_bias="0.20"
```
  同理，当一个控件添加上下约束之后，通过layout_constraintVertical_bias设置垂直百分比，上面为0，下面为1，设置垂直20%的属性写法为：
```xml
  app:layout_constraintVertical_bias="0.20"
  ```
  
### 5.ConstraintLayout实现权重布局
![](https://img-blog.csdn.net/20180313151615927?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
先设置两个按钮“靠近的边”互相约束，再将按钮的宽度设置为0dp，就可以设置水平权重了，这时候控件宽度会按照两个控件“远离的边”的距离分配权重。

同理可得垂直权重写法：
![](https://img-blog.csdn.net/20180313153329503?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

### 6.“链”的概念
![](https://img-blog.csdnimg.cn/20190207112347987.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =800x)
当多个控件除了头和尾之外彼此之间互相约束，他们之间就形成了一条链，由头和尾决定链的约束位置。

如上图所示，按钮1和按钮2之间互相约束，按钮2和按钮3之间互相约束，他们就形成了一条链。按钮1是头，按钮3是尾，按钮1的左边和父布局左边约束，按钮3的右边和父布局右边约束。所以按钮1，2，3形成的链在父布局水平居中。

在一条链的头可以设置链的风格，对于水平方向的链，属性是：
```xml
app:layout_constraintHorizontal_chainStyle="spread"
```
对于垂直方向的链，属性是：
```xml
app:layout_constraintVertical_chainStyle="spread"
```
spread是默认的风格，也就是上图的效果，链之间的间距由头和尾与各个控件平均分配。除此之外还有另外两种风格:packed和spread_inside，效果如下：

* packed：控件之间没有间距，链之间的间距由头和尾平均分:
![packed](https://img-blog.csdnimg.cn/20190207113456135.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =800x)
* spread_inside:头和尾没有间距，链之间的间距由各个控件平均分配:
![spread_inside](https://img-blog.csdnimg.cn/20190207113537155.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =800x)

### 7.组的概念

约束布局号称一层布局实现所有效果，但是之前我们使用相对布局或者线性布局嵌套布局时，并不一定是为了实现效果，有时候是因为某些控件总是在一起使用，所以需要包装起来。而在约束布局中，对于总在一起使用的控件我们并不需要再包一层，因为约束布局中有组的概念。

Group代码示例：
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/button1"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="按钮1"
        app:layout_constraintEnd_toStartOf="@id/button2"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="按钮2"
        app:layout_constraintEnd_toStartOf="@id/button3"
        app:layout_constraintStart_toEndOf="@id/button1" />

    <Button
        android:id="@+id/button3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="按钮3"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@id/button2" />

    <android.support.constraint.Group
        android:id="@+id/group_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:constraint_referenced_ids="button1,button2,button3" />
</android.support.constraint.ConstraintLayout>
```

如代码所示，约束布局中有一个Group控件，constraint_referenced_ids属性指定需要分组的id，Group属性也拥有自己的id。Group组件是没有UI的，他的存在像他的名字一样，仅仅是为了分组。分组之后，在代码中使用Group的id即可操作Group中包含的所有控件。为他们设置统一的点击监听、显示、隐藏等等。

需要注意的是，**将控件加入 Group 之后，再单独为此控件设置显示、隐藏就无效了。也就是说，Group 内的所有控件都只能由 Group 统一控制显示或隐藏。**

### 8.扇形布局
![](https://img-blog.csdnimg.cn/20190207115659818.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =800x)
代码如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/button"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="圆心"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/button1"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="北"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button2"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="东北"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="45"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button3"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="东"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="90"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button4"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="东南"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="135"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button5"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="南"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="180"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button6"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="西南"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="225"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button7"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="西"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="270"
        app:layout_constraintCircleRadius="120dp" />

    <Button
        android:id="@+id/button8"
        android:layout_width="80dp"
        android:layout_height="80dp"
        android:text="西北"
        app:layout_constraintCircle="@id/button"
        app:layout_constraintCircleAngle="315"
        app:layout_constraintCircleRadius="120dp" />

</android.support.constraint.ConstraintLayout>
```
使用layout_constraintCircle属性指定圆心，layout_constraintCircleAngle属性指定角度，layout_constraintCircleRadius指定半径，即可实现扇形布局。
# 二、FrameLayout
![](https://img-blog.csdn.net/20180313104459221?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
优点是简单，后面的控件会覆盖前一个控件，可以控制控件的层次
缺点是功能单一
# 三、LinearLayout
### 1.水平排列线性布局
![](https://img-blog.csdn.net/20180313100321127?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
### 2.垂直排列线性布局
![](https://img-blog.csdn.net/20180313100427984?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
### 3.权重分配
![](https://img-blog.csdn.net/20180313103450496?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQWxwaW5pc3RXYW5n/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
优点是简单，用起来很容易；权重分配非常实用。
缺点也是简单，实现复杂的布局往往需要一层套一层。
# 四、RelativeLayout
## 效果图

![](https://img-blog.csdn.net/20180313152543462?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

## 代码实现
```xml
<RelativeLayout android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:android="http://schemas.android.com/apk/res/android" >
    <Button
        android:id="@+id/button1"
        android:text="在父布局中居中"
        android:layout_centerInParent="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <Button
        android:id="@+id/button2"
        android:text="贴着父布局顶部"
        android:layout_alignParentTop="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <Button
        android:id="@+id/button3"
        android:text="贴着父布局底部"
        android:layout_alignParentBottom="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
    <Button
        android:id="@+id/button4"
        android:text="贴着父布局左边"
        android:layout_alignParentLeft="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
    <Button
        android:id="@+id/button5"
        android:text="贴着父布局右边"
        android:layout_alignParentRight="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
    <Button
        android:id="@+id/button6"
        android:text="在按钮下面，水平居中"
        android:layout_below="@id/button1"
        android:layout_centerHorizontal="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
    <Button
        android:id="@+id/button7"
        android:text="在按钮上面，水平居中"
        android:layout_above="@+id/button1"
        android:layout_centerHorizontal="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
    <Button
        android:id="@+id/button8"
        android:text="在按钮左边，垂直居中"
        android:layout_toLeftOf="@+id/button1"
        android:layout_centerVertical="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
    <Button
        android:id="@+id/button9"
        android:text="在按钮右边，垂直居中"
        android:layout_toRightOf="@id/button1"
        android:layout_centerVertical="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />
</RelativeLayout>
```
相对布局是在约束布局出现之前，使用最多的布局。满足了绝大多数布局效果。但是现在，请使用约束布局吧！相对布局能实现的，约束布局能实现，相对布局不能实现的，约束布局也能实现。

笔者已在公司的项目中全部使用约束布局(ConstraintLayout)与协调布局（CoordinatorLayout），如果您在使用中有任何问题，欢迎在留言区与我交流，共同进步。