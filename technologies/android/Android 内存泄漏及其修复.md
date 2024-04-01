@[TOC](Android常见内存泄漏及其修复)
## 一、定义
Android内存泄漏是指：在页面销毁之后，分配的内存未释放完
## 二、案例
### 1.Handler发送延迟消息
```kotlin
handler?.sendEmptyMessageDelayed(123, 10000)
```
#### 解决方案：onDestroy()中移除延迟发送的消息
```kotlin
handler?.removeMessages(123)
```
### 2.Handler执行耗时任务
```kotlin
handler.post {
    Thread(Runnable {
        Thread.sleep(10000)
    }).start()
}
```
#### 解决方案：onDestroy()中移除耗时任务
```kotlin
handler.removeCallbacks(runnable)
```
### 3.RxJava2的internal任务未停止
```kotlin
Observable.interval(0, 1, TimeUnit.SECONDS)
           .subscribeOn(Schedulers.newThread())
           .observeOn(AndroidSchedulers.mainThread())
           .subscribe {
                Log.d("~~~", "$it")
            }
```
#### 解决方案1：onDestroy()中手动关闭任务
```kotlin
disposable?.dispose()
```
#### 解决方案2：绑定RxLifeCycle
```kotlin
.compose(bindUntilEvent(ActivityEvent.DESTROY))
```
### 4.Timer任务未停止
```kotlin
var time = 0
Timer().schedule(object:TimerTask(){
    override fun run() {
        Log.d("~~~", "${time++}")
    }
},0,1000)
```
#### 解决方案：onDestroy()中移除Timer任务
```kotlin
timer.cancel()
```
### 5.static变量内存泄漏：static Context
```kotlin
companion object {
    var context: Context? = null
}
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    context = this
}
```
#### 解决方案：onDestroy()中将静态变量置空
```kotlin
context = null
```
### 6.static变量内存泄漏：static View
```kotlin
companion object {
    var button: Button? = null
}
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    button = btn_test
}
```
#### 解决方案：onDestroy()中将静态变量置空
```kotlin
button = null
```
### 附：静态变量和实例变量的区别
```kotlin
class StaticInstanceClass {
    companion object {
        var staticVar = 0
    }

    private var instanceVar = 0

    init {
        staticVar++
        instanceVar++
        Log.d("~~~", "staticVar = $staticVar, instanceVar = $instanceVar")
    }
}
```
执行以下代码：
```kotlin
for(i in 0..9){
    StaticInstanceClass()
}
```
打印Log如下：
```kotlin
~~~: staticVar = 1, instanceVar = 1
~~~: staticVar = 2, instanceVar = 1
~~~: staticVar = 3, instanceVar = 1
~~~: staticVar = 4, instanceVar = 1
~~~: staticVar = 5, instanceVar = 1
~~~: staticVar = 6, instanceVar = 1
~~~: staticVar = 7, instanceVar = 1
~~~: staticVar = 8, instanceVar = 1
~~~: staticVar = 9, instanceVar = 1
~~~: staticVar = 10, instanceVar = 1
```
这是因为静态变量的内存地址是静态的，创建一次之后，此静态变量就一直存在，如果不手动置空，它的生命周期将贯穿整个app。
所以尽管代码中创建了10个此对象，但staticVar其实都指向同一个变量，每次都在原来的基础上加1；而实例变量的内存地址每次都动态分配，所以instanceVar的值每次都从0开始加1
## 三、内存回收的两种算法
### 1.引用计数法：iOS和Python使用的此方法回收内存
引用计数法就是对每个对象被引用的次数进行计数，当计数为0，则表示没有被引用，判断为可回收状态。
此方法存在的问题是循环引用，即A持有B的引用，B持有A的引用， A、B同时不再使用时，无法回收A、B，就会发生内存泄漏。
#### 图示
![引用计数法图示](https://img-blog.csdnimg.cn/20181222205645445.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
当对象M被A使用时，M的被引用次数+1
当对象M被B使用时，M的被引用次数+1
当A不再使用M后，M的被引用次数-1
当B不再使用M后，M的被引用次数-1
此时M的被引用次数=0，所以M被判定为可回收状态，当回收内存时，M就会被回收了
#### 引用计数法处理循环引用图示
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181222210004456.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
当A和B互相引用时，A和B的被引用次数都为1，如果A和B同时不再需要使用了，由于引用次数不为0，所以内存回收时，无法将A和B进行回收。
#### 解决方式1：根据具体业务逻辑，当其中一个变量不再需要时，将其主动置空，这样就解除了互相引用
#### 解决方式2：将其中一个变量设置为弱引用，弱引用的对象在内存回收时必定被回收，这样就解除了互相引用
### 2.可达性分析法：Android和Java使用的此方法回收内存
可达性分析就是从一些GC Root 对象出发，去遍历包含此对象引用的对象，再依次递归。类似树形结构，从根向叶子标记可达的对象。最后没有标记到的对象即为可回收对象。解决了循环引用的问题。
#### 图示
![可达性分析图示](https://img-blog.csdnimg.cn/20181222210735296.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
如上图，当内存回收时，从GC Roots出发，Object1 ~ 4都是可达的，所以不会被回收，Object5 ~ 7是不可达的，就会被回收掉
#### GC Root
所有正在运行的线程的栈上的引用变量。所有的全局变量。所有ClassLoader等等，具体如下：
```java
1.System Class
2.JNI Local
3.JNI Global
4.Thread Block
5.Thread
6.Busy Monitor
7.Java Local
8.Native Stack
9.Finalizable
10. Unfinalized
11.Unreachable
12.Java Stack Frame
13.Unknown
```
## 四、强引用、弱引用、软引用、虚引用
### 1.强引用（ StrongReference ）：
强引用是使用最普遍的引用。如果一个对象具有强引用，那垃圾回收器绝不会回收它
### 2.软引用（SoftReference）：
如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存。
### 3. 弱引用（WeakReference）：
弱引用与软引用的区别在于：只具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些只具有弱引用的对象。    
### 4.虚引用（PhantomReference）：
顾名思义，就是形同虚设，与其他几种引用都不同，虚引用并不会决定对象的生命周期。如果一个对象仅持有虚引用，那么它就和没有任何引用一样，在任何时候都可能被垃圾回收器回收。
### 表格对比
| 引用类型 | 被垃圾回收时间 | 用途 |生存时间|
|-|-|-|-|
| 强引用 | 从来不会 | 对象的一般状态 |JVM停止运行时终止|
| 软引用 | 在内存不足时 | 对象缓存 |内存不足时终止|
| 弱引用 | 在垃圾回收时 | 对象缓存 |gc运行后终止|
| 虚引用 | Unknown | Unknown |Unknown|
### 图示
![强引用、弱引用、软引用、虚引用](https://img-blog.csdnimg.cn/20181222212005655.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
## 五、内存泄漏检测工具--leakcanary
leakcanary是square公司开源的检测Android程序内存泄漏的工具
Github地址：[https://github.com/square/leakcanary](https://github.com/square/leakcanary)
### 使用方式
在build.gradle中添加依赖库：
```kotlin
dependencies {
  debugImplementation 'com.squareup.leakcanary:leakcanary-android:1.6.2'
  releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.6.2'
}
```
在Application中初始化：
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLeakCanary()
    }

    /**
     * 初始化内存泄漏检测
     */
    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }
}
```
leakcanary-android库是用来检测内存泄漏的库，用debugImplementation导入表示只在编译时导入此库
leakcanary-android-no-op是用releaseImplementation导入的，表示发布正式版本时，导入此库，no-op表示No operation（无操作），导入的主要目的是打包时不需要手动删除leakcanary相关代码，也可以编译通过