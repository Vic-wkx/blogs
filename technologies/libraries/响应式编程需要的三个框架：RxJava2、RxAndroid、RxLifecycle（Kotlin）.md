@[TOC]
## RxJava
### 简介
RxJava是基于响应式编程的框架，响应式编程的思想就是一个事件发生后，监听着这个事件的监听器马上做出响应。类似于平常的开关灯。当我们打开灯的开关时，灯马上亮了；当我们关闭灯的开关时，灯马上熄了。这个过程中，灯对我们控制开关的事件做出了响应。在Android中，设置按钮监听器也用到了响应式的思想，当有点击事件发生时，OnClickListener马上执行。也就是说OnClickListener时刻观察着按钮，当按钮被点击时，OnClickListener马上做出了响应。

RxJava中的三个基本概念：观察者Observer，被观察者Observable，订阅subscribe。当观察者订阅了被观察者，被观察者有事件发生时，观察者可以做出响应。

RxAndroid是JakeWharton对RxJava做的一个扩展，主要是为了在Android中更方便的切换到主线程

RxJava的Github地址为：[https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)

RxAndroid的Github地址为：[https://github.com/ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid)

### 使用
1.在app模块的build.gradle的dependencies中引入RxJava和RxAndroid：

```kotlin
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
implementation 'io.reactivex.rxjava2:rxjava:2.2.7'
```
2.在MainActivity中创建被观察者和观察者，然后建立订阅，例：（本例中布局非常简单，只有一个id为text的TextView，故不再给出布局代码）

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，发出整型数据
        val observable = createObservable()
        //创建一个观察者，接收整型数据
        val observer = createObserver()
        //建立订阅
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    private fun createObservable(): Observable<Int> {
        return Observable.create {
            for (i in 0..9) {
                //通知观察者执行onNext()方法
                it.onNext(i)
            }
            //通知观察者数据发送完成
            it.onComplete()
        }
    }

    private fun createObserver(): Observer<Int> {
        return object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(integer: Int) {
                text.append("$integer\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
注：

（1）被观察者需要重写subscribe()方法，在此方法中使用emitter发出数据，Kotlin对单方法、单参数的函数提供了一个语法糖，只需要使用it就能代表此参数emitter

（2）观察者需要重写onSubscribe()、onNext()、onError()、onComplete()方法。

onSubscribe()：订阅开始时调用

onNext()：执行发射器发出的事件

onError()：当程序出错时，执行onError，订阅结束

onComplete()：当程序完成后，执行onComplete，订阅结束

（3）使用observable.subscribe(observer)建立订阅。subscribeOn(Scheduler scheduler)决定被观察者发射事件的线程，observeOn(Scheduler scheduler)决定观察者接收事件的线程。

常用的线程类型有：

Schedulers.newThread()：新线程

Schedulers.io()：IO线程

Schedulers.computation()：计算线程

AndroidSchedulers.mainThread()：主线程(RxAndroid库中的)

执行程序，可以看到如下结果：
![](https://img-blog.csdn.net/20180520093953849?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =300x)

可以看到observable中发出的事件被observer依次执行了
### just操作符
使用just操作符可以很方便的发射有限个数据，例如可将上述代码中的createObservable()方法替换为：
```kotlin
/**
 * 创建第一个被观察者，发射0~9
 */
private fun createObservable(): Observable<Int> {
    return Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
}
```
效果与之前的createObservable()方法是等价的
### map操作符
3.使用map在数据传递过程中进行数据类型转换

例：在MainActivity中创建发出整型数据的被观察者、接收字符串数据的观察者，使用map将整型数据转换成字符串，建立订阅
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，发出整型数据
        val observable = createObservable()
        //创建一个观察者，接收字符串数据
        val observer = createObserver()
        //建立订阅
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map{
                    "string $it"
                }
                .subscribe(observer)
    }

    private fun createObservable(): Observable<Int> {
        return Observable.create {
            for (i in 0..9) {
                //通知观察者执行onNext()方法
                it.onNext(i)
            }
            //通知观察者数据发送完成
            it.onComplete()
        }
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(string: String) {
                text.append("$string\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
注：map()函数中需要传入一个Function<T1,T2>进行类型转换，Function中需要重写apply函数，这里同样使用了kotlin的语法糖，it表示传进来的整数，map函数最后一行作为返回值

T2 apply(T1 data)：将T1类型的数据传入，返回T2类型数据，实现数据类型转换

执行程序，可以看到如下结果：
![map之后](https://img-blog.csdn.net/20181017115355813?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =300x)

### flatMap操作符和concatMap操作符
4.使用flatMap将发射的一列数据列展开，单独发射
例：使用flatMap的代码如下：
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，发出整型数据
        val observable = createObservable()
        //创建一个观察者，接收字符串数据
        val observer = createObserver()
        //建立订阅
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { origin ->
                    Observable.create<String> {
                        //将原始的一列数据展开，单独用一个被观察者发射出去
                        it.onNext("new emitter:$origin")
                        //当展开的所有被观察者都完成后，原始被观察者才会完成
                        it.onComplete()
                    }
                }
                .subscribe(observer)
    }

    private fun createObservable(): Observable<Int> {
        return Observable.create {
            for (i in 0..9) {
                //通知观察者执行onNext()方法
                it.onNext(i)
            }
            //通知观察者数据发送完成
            it.onComplete()
        }
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(string: String) {
                text.append("$string\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
上面的代码原始发射数据是0~9的一列数字，flatMap传入一个原始数据，输出一个被观察者，也就是将单个数字单独用一个被观察者发射出去。执行以上程序，显示如下：
![flatMap之后](https://img-blog.csdn.net/20181017144903467?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =300x)

当展开的所有被观察者都完成后，原始的观察者才会完成。

flatMap有个类似的方法concatMap，使用上没有差别，区别在于flatMap展开后是无序的，concatMap展开后是有序的，使用concatMap的话，展开的一列被观察者将会按照原始数据的顺序依次发射

### internal操作符
使用internal操作符实现Timer的效果，internal操作传入三个参数：
initialDelay ： 延迟多长时间开始
period ： 间隔多长时间
unit ： 时间单位
例：
```kotlin
class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，间隔1秒发射数据
        val observable = createObservable()
        //创建一个观察者，接收数据
        val observer = createObserver()
        //建立订阅
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)

    }

    private fun createObservable(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
    }

    private fun createObserver(): Observer<Long> {
        return object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
                disposable = d
            }

            override fun onNext(data: Long) {
                text.append("$data\n")
                if (data >= 9) {
                    disposable?.dispose()
                    onComplete()
                }
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
运行以上程序，显示如下：
![internal操作符](https://img-blog.csdn.net/20181017152954845?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =300x)

代码中可以看出，onSubscribe中的Disposable代表建立的订阅，需要取消订阅时，使用disposable.dispose方法即可。如果不取消订阅，internal的onNext回调将一直执行，直到当前程序进程被杀死。
由此可以看出，RxJava使用时一定要及时关闭订阅，否则会导致内存泄漏问题，有一个比较方便的框架RxLifecycle，可以帮助我们在Activity生命周期中关闭订阅。
使用internal操作后，线程会切到子线程。如果使用internal发射数据后需要更新UI，请务必记住要切换到主线程。
### delay操作符
delay操作使得被观察者延迟发射数据。
例如：
```kotlin
private fun createObservable(): Observable<String> {
    return Observable.just("1", "2", "3")
            .delay(1, TimeUnit.SECONDS)
}
```
和internal操作符一样，使用delay操作后，线程也会切到子线程。如果使用delay发射数据后需要更新UI，请务必记住要切换到主线程。
### take操作符
take操作符可以用来指定internal操作执行多少次，例如：
```kotlin
private fun createObservable(): Observable<Long> {
    return Observable.interval(0, 1, TimeUnit.SECONDS)
            .take(10)
}
```
表示间隔1s发射数据，发射10次，也就是依次发射0~9的数据。
### timer操作符
timer操作延迟发射一个0L，例如：
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，发出Long数据
        val observable = createObservable()
        //创建一个观察者，接收Long数据
        val observer = createObserver()
        //建立订阅
        observable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun createObservable(): Observable<Long> {
        return Observable.timer(1, TimeUnit.SECONDS)
    }

    private fun createObserver(): Observer<Long> {
        return object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(long: Long) {
                text.append("$long\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190403134325174.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
### zip操作符
zip操作将两个被观察者发射的数据合并为一个数据发射。例如：
```kotlin
class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建第一个被观察者，发射1，2，3
        val observable1 = createObservable1()
        // 创建第二个被观察者，发射"a","b","c"
        val observable2 = createObservable2()
        // 创建一个观察者，接收字符串数据
        val observer = createObserver()
        // 建立订阅，使用zip操作将两个被观察者发射的数据组合成一个数据
        Observable.zip(observable1, observable2, object : BiFunction<Long, String, String> {
            override fun apply(t1: Long, t2: String): String {
                return t1.toString() + t2
            }
        }).subscribe(observer)
    }


    private fun createObservable1(): Observable<Long> {
        return Observable.just(1, 2, 3)
    }

    private fun createObservable2(): Observable<String> {
        return Observable.just("a", "b", "c")
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(data: String) {
                text.append("$data\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
zip操作接收三个参数，分别是第一个被观察者observable1、第二个被观察者observable1、结合方式BiFunction。
BiFunction中有三个参数类型，分别代表observable1发射的数据类型，observable2发射的数据类型，数据合并后发射的数据类型。
BiFunction中的apply函数代表合并的方式。
本例中，observable1发射Long类型数据，observable2发射String类型数据，在apply中的结合方式为：
```kotlin
override fun apply(t1: Long, t2: String): String {
    return t1.toString() + t2
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326010434787.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
zip操作会随着其中任何一个Observable终止而终止，也就是说，如果本例中将observable1的参数改为发射4个整数：
```kotlin
private fun createObservable1(): Observable<Long> {
    return Observable.just(1, 2, 3, 4)
}
```
observable2不变，仍然发射三个字符"a","b","c"，运行结果仍然和上图一样。因为当observable1终止时，zip操作也就终止了，不会再接收observable2的数据
### zipWith操作符
zipWith和zip的作用是一样的，不同之处是zip操作传入两个被观察者，zipWith是一个被观察者加上另一个被观察者。
例如，上述zip操作符可替换为zipWith：
```kotlin
// 建立订阅，使用zipWith添加第二个被观察者
observable1.zipWith(observable2, object : BiFunction<Long, String, String> {
    override fun apply(t1: Long, t2: String): String {
        return t1.toString() + t2
    }
}).subscribe(observer)
```
### merge操作符
merge操作将多个被观察者的数据合并发射：
```kotlin
class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建第一个被观察者，发射"1","2","3","4","5"
        val observable1 = createObservable1()
        // 创建第二个被观察者，发射"a","b","c"
        val observable2 = createObservable2()
        // 创建一个观察者，接收字符串数据
        val observer = createObserver()
        // 建立订阅，使用merge操作将两个被观察者发射的数据合并发射
        Observable.merge(observable1, observable2).subscribe(observer)
    }

    private fun createObservable1(): Observable<String> {
        return Observable.just("1", "2", "3", "4", "5")
    }

    private fun createObservable2(): Observable<String> {
        return Observable.just("a", "b", "c")
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(data: String) {
                text.append("$data\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326011629232.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
可以看出，merge操作将两个被观察者的数据合并发射了。merge操作是没有顺序的，例如修改代码如下：
```kotlin
class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建第一个被观察者，延迟1秒发射"1","2","3","4","5"
        val observable1 = createObservable1()
        // 创建第二个被观察者，发射"a","b","c"
        val observable2 = createObservable2()
        // 创建一个观察者，接收字符串数据
        val observer = createObserver()
        // 建立订阅，使用merge操作将两个被观察者发射的数据合并发射
        Observable.merge(observable1, observable2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    private fun createObservable1(): Observable<String> {
        return Observable.just("1", "2", "3", "4", "5")
                .delay(1, TimeUnit.SECONDS)
    }

    private fun createObservable2(): Observable<String> {
        return Observable.just("a", "b", "c")
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(data: String) {
                text.append("$data\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
使用delay操作符延迟1秒发射observable1，前文已经说到，由于delay操作会将线程切到子线程，而我们之后要在TextView中显示UI，所以这里加上了切换到主线程操作:
```kotlin
.observeOn(AndroidSchedulers.mainThread())
```
此时运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326012731107.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
可以看出，merge操作并没有按照传入的先observable1，再observable2的次序发送数据，而是先到先执行。如果需要按照次序发射数据，请使用concat操作符

### mergeWith操作符
merge和mergeWith的关系与zip和zipWith的关系一样。merge是传入两个被观察者，mergeWith是一个被观察者加上另一个被观察者。
例如，上述例子中的merge操作符可替换为mergeWith：
```kotlin
// 建立订阅，使用mergeWith操作添加第二个被观察者
observable1.mergeWith(observable2).subscribe(observer)
```
### concat操作符
concat操作符和merge操作符很像，只是concat操作符是按照顺序执行的。merge和concat的关系类似flatMap和concatMap的关系。
将之前的代码中的merge替换为concat：
```kotlin
class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建第一个被观察者，延迟1秒发射"1","2","3","4","5"
        val observable1 = createObservable1()
        // 创建第二个被观察者，发射"a","b","c"
        val observable2 = createObservable2()
        // 创建一个观察者，接收字符串数据
        val observer = createObserver()
        // 建立订阅，使用concat操作将两个被观察者发射的数据合并发射
        Observable.concat(observable1, observable2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    private fun createObservable1(): Observable<String> {
        return Observable.just("1", "2", "3", "4", "5")
                .delay(1, TimeUnit.SECONDS)
    }

    private fun createObservable2(): Observable<String> {
        return Observable.just("a", "b", "c")
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(data: String) {
                text.append("$data\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326013249244.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
可以看出，在concat中是严格按照传入的顺序，先执行observable1，再执行observable2。虽然observable1延迟了一秒执行，observable2仍然是按照顺序，等待observable1完成后才执行。
### concatWith操作符
同理，concatWith与concat效果一样，concat是传入两个被观察者，concatWith是一个被观察者加上另一个被观察者。例如，上例的concat可替换为concatWith：
```kotlin
// 建立订阅，使用concatWith操作添加第二个被观察者
observable1.concatWith(observable2)
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(observer)
```
### buffer操作符
buffer的意思是缓冲，buffer(count)是指收集到count个结果后，再一起传给Observer，例如：
```kotlin
class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建一个被观察者，发射"1","2","3","4","5", 并缓冲3次
        val observable = createObservable()
        // 创建一个观察者，接收字符串数组
        val observer = createObserver()
        // 建立订阅
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    private fun createObservable(): Observable<List<String>> {
        return Observable.just("1", "2", "3", "4", "5")
                .buffer(3)
    }

    private fun createObserver(): Observer<List<String>> {
        return object : Observer<List<String>> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(data: List<String>) {
                text.append("收到了buffer 3次的数据\n")
                data.forEach {
                    text.append("$it\n")
                }
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326100718342.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
使用buffer操作符，Observer中收到的数据是一个列表，如果订阅结束，数据不足三个，剩余数据列表也会传到Observer中。
### filter操作符
filter的意思是过滤，这里是指从数据源中过滤出符合条件的数据（不是过滤掉）。例如：
```kotlin
class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 创建一个被观察者，发射"1","2","3","4","5", 并过滤出等于1的数据
        val observable = createObservable()
        // 创建一个观察者，接收字符串数组
        val observer = createObserver()
        // 建立订阅
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    private fun createObservable(): Observable<String> {
        return Observable.just("1", "2", "3", "4", "5")
                .filter {
                    it == "1"
                }
    }

    private fun createObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
            }

            override fun onNext(data: String) {
                text.append("$data\n")
            }

            override fun onComplete() {
                text.append("complete.")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
            }
        }
    }
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326101250811.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
Observable发射了 "1","2","3","4","5" 五个数据，通过filter操作符过滤出满足条件 it == "1" 的数据，只有"1"满足要求，所以只有数据"1"到达了Observer，其他数据都被舍弃了。
### 简写
RxJava在Kotlin中有一个语法糖，在subscribe中可以直接传入四个回调方法，例如：
```kotlin
@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，间隔1秒发射数据
        val observable = createObservable()
        //建立订阅
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // onNext回调
                    text.append("$it\n")
                }, {
                    // onError回调
                    text.append(it.message)
                }, {
                    // onComplete回调
                    text.append("complete.")
                }, {
                    // onSubscribe回调
                    text.append("subscribe\n")
                })
    }

    private fun createObservable(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(10)
    }
}
```
传入的四个方法回调分别为：onNext，onError，onComplete，onSubscribe。使用这种写法省去了创建observer的步骤。
### Consumer
大多数时候，我们只关心onNext回调，使用observer每次都需要重写四个回调确实比较麻烦，Consumer（意思是消费者）只提供一个回调onNext，效果和observer中的onNext效果一样，如果你不需要关心onError，onComplete，onSubscribe三个回调，可以使用Consumer，使用如下：
```kotlin
@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，间隔1秒发射数据
        val observable = createObservable()
        //建立订阅
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<Long> {
                    override fun accept(t: Long?) {
                        text.append("$t\n")
                    }
                })
    }

    private fun createObservable(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(10)
    }
}
```
前文已提到，Kotlin对单方法、单参数的函数提供了一个语法糖，只需要使用it就能代表此参数，所以可以将Consumer进一步简化为：
```kotlin
@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，间隔1秒发射数据
        val observable = createObservable()
        //建立订阅
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    text.append("$it\n")
                }
    }

    private fun createObservable(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(10)
    }
}
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326103135302.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
### 多次切换线程
RxJava可以很方便的多次切换线程，例如：
```kotlin
@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 建立订阅
        Observable.just(1,2,3)
                // subscribeOn指定其之前的代码所在的线程，所以just操作在新线程中执行
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe {
                    text.append("在主线程中执行:doOnSubscribe\n")
                }
                // subscribeOn指定其之前的代码所在的线程，所以doOnSubscribe在主线程执行
                .subscribeOn(AndroidSchedulers.mainThread())
                // observeOn指定其之后的代码所在的线程，所以map操作在新线程中执行
                .observeOn(Schedulers.newThread())
                .map {
                    "在新线程中将其转化成了string$it"
                }
                // observeOn指定其之后的代码所在的线程，所以subscribe操作在主线程中执行
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    text.append("在主线程中执行:$it\n")
                }
    }
}
```
subscribeOn指定其之前的代码所在的线程，直到遇到另一个subscribeOn。
observeOn指定其之后的代码所在的线程，直到遇到另一个observeOn。
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190326095520882.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
### onErrorReturn
在马上要走onError的时候将其拦截，返回新值，例如
```kotlin
Observable.just("a")
    .map {
        it.toInt()
    }
    .onErrorReturn {
        text.append("$it\n")
        -1
    }
    .subscribe {
        text.append(it.toString())
    }
```
因为"a"字符串是不能转换成 Int 的，所以map时会走onError，使用onErrorReturn将其拦截，在onErrorReturn中能够拿到错误信息，我们将错误信息展示在TextView上，并返回 -1。运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190402180526415.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =400x)
### onErrorResumeNext
在马上要走onError的时候将其拦截，返回新的被观察者，例如
```kotlin
Observable.just("a")
    .map {
        it.toInt()
    }
    .onErrorResumeNext(Function {
        text.append("$it\n")
        Observable.just(-1)
    })
    .subscribe {
        text.append(it.toString())
    }
```
运行程序，显示和上图的onErrorReturn一样。
### error操作符
使用error操作符，发送一个异常到观察者。例如：
```kotlin
Observable.error<Int>(Exception())
    .subscribe({
        // it表示正常流程下的Int数据，这里不会执行
        text.append(it.toString())
    }, {
        // it表示异常信息
        text.append(it.toString())
    })
```
单独使用error时，需要传入一个泛型，表示正常流程下发射的数据类型。运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190403092618669.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
#### error和onErrorResumeNext结合使用
同时使用error和onErrorResumeNext，可以在即将走onError的时候，将错误信息拦截，并返回我们自己的自定义异常。例如，先定义MyException：
```kotlin
class MyException(private val throwable: Throwable) : Exception() {
    override val message: String?
        get() = if (throwable is NumberFormatException) {
            "数字格式不正确"
        } else {
            super.message
        }
}
```
使用onErrorResumeNext拦截异常，使用error包装异常：
```kotlin
Observable.just("a")
    .map {
        it.toInt()
    }
    .onErrorResumeNext(Function {
        text.append("$it\n\n")
        Observable.error(MyException(it))
    })
    .subscribe({
        text.append(it.toString())
    }, {
        text.append("$it")
    })
```
运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190403093209822.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
### empty操作符
使用empty操作符建立一个特殊的订阅，仅执行onStart和onComplete：
```kotlin
Observable.empty<Int>()
    .subscribe({
        text.append("onNext:$it\n")
    }, {
        text.append("onError:$it\n")
    }, {
        text.append("onComplete\n")
    }, {
        text.append("onStart\n")
    })
```
和error操作符类似，单独使用empty时，需要传入一个泛型，表示正常流程下发射的数据类型。运行程序，显示如下：
![](https://img-blog.csdnimg.cn/20190403093854799.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
### retry操作符
当执行onError后，重新发起订阅。直接使用retry()会无限重试，所以我们一般使用retry(n)重试n次，例如：
```kotlin
var count = 1
Observable.just("a")
    .map {
        Log.d("~~~", "第${count++}次map")
        it.toInt()
    }
    .retry(2)
    .subscribe({
        text.append(it.toString())
    }, {
        text.append(it.toString())
    })
```
运行程序，Log控制台输出如下：
```xml
~~~: 第1次map
~~~: 第2次map
~~~: 第3次map
```
可以看出重试了两次。
### repeat操作符
当执行onComplete后，重新发起订阅。repeat和retry很相似，直接使用repeat()会无限重试，所以我们一般使用repeat(n)重复n次，例如：

```kotlin
var count = 1
Observable.just("1")
    .map {
        Log.d("~~~", "第${count++}次map")
        it.toInt()
    }
    .repeat(2)
    .subscribe()
```
运行程序，Log控制台输出如下：
```xml
~~~: 第1次map
~~~: 第2次map
```
可以看出重复了两次。

### repeatWhen
当订阅将要走onComplete时，如果满足一定条件，就拷贝一个当前订阅并重复，例如：
```kotlin
var a = 10
Observable.just(a)
    .repeatWhen {
        it.takeUntil {
            a <= 5
        }
    }
    .subscribe({
        Log.d("~~~", "onNext:it = $it, a = $a")
        a--
    }, {
        Log.d("~~~", "onError:$it")
    }, {
        Log.d("~~~", "onComplete")
    }, {
        Log.d("~~~", "onSubscribe")
    })
```
运行程序，Log控制台输出如下：
```xml
~~~: onSubscribe
~~~: onNext:it = 10, a = 10
~~~: onNext:it = 10, a = 9
~~~: onNext:it = 10, a = 8
~~~: onNext:it = 10, a = 7
~~~: onNext:it = 10, a = 6
~~~: onComplete
```
可以看到，a>5时继续重复订阅，a<=5时订阅停止。并且重复发送的数据是10，和第一次发送的一模一样，说明是拷贝的当前订阅并重复，而不是重新发送的a。重复时不会再回调onSubscribe，重复完成后才回调onComplete。
注：repeatWhen重复时会回调doOnSubscribe和doOnComplete
### retryWhen
当订阅将要走onError时，如果满足一定条件，就重试。例如：
```kotlin
var retryCount = 0
Observable.just("a")
    .map {
        it.toInt()
    }
    .retryWhen {
        it.flatMap { error ->
            if (retryCount++ < 5) {
                Log.d("~~~", "收到错误:$error,开始第${retryCount}次重试")
                Observable.timer(1, TimeUnit.SECONDS)
            } else {
                Observable.error(Throwable("重试结束"))
            }
        }
    }
    .subscribe({
        Log.d("~~~", "onNext")
    }, {
        Log.d("~~~", "onError:error = $it, a = $retryCount")
    }, {
        Log.d("~~~", "onComplete")
    }, {
        Log.d("~~~", "onSubscribe")
    })
```
运行程序，Log控制台输出如下：
```xml
14:24:50 ~~~: onSubscribe
14:24:50 ~~~: 收到错误:java.lang.NumberFormatException: For input string: "a",开始第1次重试
14:24:51 ~~~: 收到错误:java.lang.NumberFormatException: For input string: "a",开始第2次重试
14:24:52 ~~~: 收到错误:java.lang.NumberFormatException: For input string: "a",开始第3次重试
14:24:53 ~~~: 收到错误:java.lang.NumberFormatException: For input string: "a",开始第4次重试
14:24:54 ~~~: 收到错误:java.lang.NumberFormatException: For input string: "a",开始第5次重试
14:24:55 ~~~: onError:error = java.lang.Throwable: 重试结束, a = 6
```
此例中，由于a无法转换成Int，所以会走onError，此时由于重试次数小于5，retryWhen返回了一个延迟一秒的订阅Observable.timer(1, TimeUnit.SECONDS)，当延迟一秒后，开始重试。直至重试次数大于5，抛出异常，结束重试。
注：笔者对repeatWhen和retryWhen理解尚不透彻，但本例中的用法已经能够满足笔者日常需要。若有不对之处，欢迎读者讨论指正。
## RxLifecycle
### 简介
使用RxJava时，在界面关闭后，如果订阅未执行完，订阅不会自动停止。需要手动取消订阅。使用RxLifecycle可以在建立订阅时就指定在Activity 的onDestroy中（或者或者Fragment的onDestroyView中）取消订阅，省去了自己手动取消订阅的步骤。

RxLifecycle的Github地址为：[https://github.com/trello/RxLifecycle](https://github.com/trello/RxLifecycle)
### 使用
1.在build.gradle中导入RxLifecycle
```kotlin
    implementation 'com.trello.rxlifecycle3:rxlifecycle-components:3.0.0'
```
注：RxLifecycle中包含了RxJava包，所以导入了RxLifecycle之后就可以不用导入RxJava了，但是RxLifecycle不包含RxAndroid，所以还是需要导入RxAndroid

2.我们先写一个不加入RxLifecycle的订阅，测试一下RxJava带来的内存泄漏：
```kotlin
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，间隔1秒发射数据
        val observable = createObservable()
        //创建一个观察者，接收数据
        val observer = createObserver()
        //建立订阅
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }


    private fun createObservable(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.SECONDS)

    }

    private fun createObserver(): Observer<Long> {
        return object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
                Log.d(TAG, "onSubscribe")
            }

            override fun onNext(data: Long) {
                text.append("$data\n")
                Log.d(TAG, "$data")
            }

            override fun onComplete() {
                text.append("complete.")
                Log.d(TAG, "onComplete")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
                Log.d(TAG, "onError")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}
```
运行程序，可以看到如下Log：
```kotlin
com.example.studyrxjava D/MainActivity: onSubscribe
com.example.studyrxjava D/MainActivity: 0
com.example.studyrxjava D/MainActivity: 1
com.example.studyrxjava D/MainActivity: 2
com.example.studyrxjava D/MainActivity: 3
com.example.studyrxjava D/MainActivity: 4
com.example.studyrxjava D/MainActivity: 5
com.example.studyrxjava D/MainActivity: 6
com.example.studyrxjava D/MainActivity: 7
com.example.studyrxjava D/MainActivity: 8
com.example.studyrxjava D/MainActivity: onDestroy
com.example.studyrxjava D/MainActivity: 9
com.example.studyrxjava D/MainActivity: 10
com.example.studyrxjava D/MainActivity: 11
com.example.studyrxjava D/MainActivity: 12
com.example.studyrxjava D/MainActivity: 13
com.example.studyrxjava D/MainActivity: 14
com.example.studyrxjava D/MainActivity: 15
com.example.studyrxjava D/MainActivity: 16
com.example.studyrxjava D/MainActivity: 17
...
```
由此可见，在Activity已经onDestroy之后，RxJava的internal订阅仍在运行，这就是内存泄漏了。
3.加上RxLifecycle修复此内存泄漏，代码如下
```kotlin
class MainActivity : RxAppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //创建一个被观察者，间隔1秒发射数据
        val observable = createObservable()
        //创建一个观察者，接收数据
        val observer = createObserver()
        //建立订阅
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(observer)
    }


    private fun createObservable(): Observable<Long> {
        return Observable.interval(0, 1, TimeUnit.SECONDS)

    }

    private fun createObserver(): Observer<Long> {
        return object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                text.append("subscribe\n")
                Log.d(TAG, "onSubscribe")
            }

            override fun onNext(data: Long) {
                text.append("$data\n")
                Log.d(TAG, "$data")
            }

            override fun onComplete() {
                text.append("complete.")
                Log.d(TAG, "onComplete")
            }

            override fun onError(e: Throwable) {
                text.append(e.message)
                Log.d(TAG, "onError")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
}
```
首先将继承类由AppCompatActivity改为RxAppCompatActivity，这是使用RxLifecycle必需的，然后建立订阅时加入.compose(bindUntilEvent(ActivityEvent.DESTROY))即可，这行代码需要加在subscribe(observer)调用之前

运行以上加入了RxLifecycle的代码，Log显示如下：
```kotlin
com.example.studyrxjava D/MainActivity: onSubscribe
com.example.studyrxjava D/MainActivity: 0
com.example.studyrxjava D/MainActivity: 1
com.example.studyrxjava D/MainActivity: 2
com.example.studyrxjava D/MainActivity: 3
com.example.studyrxjava D/MainActivity: 4
com.example.studyrxjava D/MainActivity: 5
com.example.studyrxjava D/MainActivity: 6
com.example.studyrxjava D/MainActivity: onComplete
com.example.studyrxjava D/MainActivity: onDestroy
```
可以看到，onDestroy之后RxJava的internal订阅就结束了。

以上，就是RxJava RxAndroid RxLifecycle基本使用。