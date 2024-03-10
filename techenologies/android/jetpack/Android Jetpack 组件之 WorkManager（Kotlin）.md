@[TOC]

# 一、简介

WorkManager 用于处理 Android 后台任务。我们只需要设置好任务内容、何时执行，剩下的工作就可以完全交给系统处理。它会自动向下兼容，在不同的 Android 版本上采用不同的实现方案。

由于是交给系统调度的，它可以保证应用退出甚至手机重启后，任务依然能够得到执行。WorkManager 很适合执行一些定期和服务器交互的任务，比如周期性的同步数据等等。并且，WorkManager 还支持周期性任务、链式任务。

需要注意的是，WorkManager 不能保证任务一定能够准时执行，这是因为系统为了减少电量消耗，会将触发事件临近的几个任务放在一起执行，以减少 CPU 被唤醒的次数，延长电池使用时间。

另外，在国产手机上 WorkManager 可能无法正常运行，这是因为绝大多数手机厂商定制 Android 系统时，会添加一个“一键关闭”的功能，这样被杀死后的应用程序，既无法接收广播，也不能运行 WorkManager 的后台任务。国产手机增加此功能也是迫于无奈，主要是因为市面上有太多的恶意应用想要霸占后台。所以，我们在国产手机上不要使用 WorkManager 去实现核心功能。

# 二、导入
在 app/build.gradle 中添加依赖：
```gradle
implementation 'androidx.work:work-runtime:2.3.2'
```
# 三、基本使用
WorkManager 的用法分为三步：
* 定义一个后台任务
* 配置任务运行条件
* 将任务传给 WorkManager
## 3.1 定义后台任务
创建一个 SimpleWorker 类，继承自 Worker：
```kotlin
class SimpleWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("~~~", "do something")
        return Result.success()
    }
}
```
在 doWork 方法中实现任务逻辑，返回的 Result 是一个系统类，主要有三个方法供我们使用：
* Result.success() 表示任务执行成功
* Result.failure() 表示任务执行失败
* Result.retry() 表示任务需要重试。这个方法需要配合任务重试配置一起使用

## 3.2 配置任务运行条件
### 3.2.1 只需执行一次的任务
使用 OneTimeWorkRequest 构建只需执行一次的任务
```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).build()
```
### 3.2.2 周期性执行的任务
使用 PeriodicWorkRequest 构建周期性执行的任务
```kotlin
val request = PeriodicWorkRequest.Builder(SimpleWorker::class.java, 15, TimeUnit.MINUTES).build()
```
为了减少耗电量，PeriodicWorkRequest 要求任务执行周期不得短于十五分钟，查看源码可以发现，如果传入的值短于十五分钟，系统会打印一条警告，然后自动将周期设置成十五分钟：
```java
public static final long MIN_PERIODIC_INTERVAL_MILLIS = 15 * 60 * 1000L; // 15 minutes.

/**
 * Sets the periodic interval for this unit of work.
 *
 * @param intervalDuration The interval in milliseconds
 */
public void setPeriodic(long intervalDuration) {
    if (intervalDuration < MIN_PERIODIC_INTERVAL_MILLIS) {
        Logger.get().warning(TAG, String.format(
                "Interval duration lesser than minimum allowed value; Changed to %s",
                MIN_PERIODIC_INTERVAL_MILLIS));
        intervalDuration = MIN_PERIODIC_INTERVAL_MILLIS;
    }
    setPeriodic(intervalDuration, intervalDuration);
}
```
## 3.3 将任务传给 WorkManager
```kotlin
WorkManager.getInstance(this).enqueue(request)
```
这就是 WorkManager 的基本使用。
# 四、高级配置
## 4.1 设置任务延迟执行
通过 setInitialDelay 方法设置延迟时间
 ```kotlin
 val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .setInitialDelay(5, TimeUnit.MINUTES)
    .build()
```
## 4.2 给任务添加标签
通过 addTag 方法添加标签：
```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .addTag("simple")
    .build()
```
添加标签的作用是，方便我们根据标签取消任务。
## 4.3 取消任务
### 4.3.1 根据标签取消任务
```kotlin
WorkManager.getInstance(this).cancelAllWorkByTag("simple")
```
### 4.3.2 根据 request 的 id 取消任务
```kotlin
WorkManager.getInstance(this).cancelWorkById(request.id)
```
### 4.3.3 取消所有任务
```kotlin
WorkManager.getInstance(this).cancelAllWork()
```
## 4.4 任务重试
通过 setBackoffCriteria 配置任务重试：
```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .build()
```
前文说到，`Result.retry() 表示任务需要重试，这个方法需要配合任务重试配置一起使用`。任务重试配置就是指这个 setBackoffCriteria  方法，它传入了三个值，第二个值和第三个值表示重试时间配置。第一个值表示重试延迟的形式，有两个值可供选择：
* BackoffPolicy.LINEAR 重试时间每次呈线性增长，按照此例中的配置，每次重试时间就是 10s，20s，30s，40s...
* BackoffPolicy.EXPONENTIAL 重试时间每次呈指数级增长，按照此例中的配置，每次重试时间就是 10s，20s，40s，80s...
## 4.5 监听任务结果
```kotlin
WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id).observe(this) {
    Log.d("~~~", "state = ${it.state}, tags = ${it.tags.toList()}")
    when (it.state) {
        WorkInfo.State.SUCCEEDED -> Log.d("~~~", "success")
        WorkInfo.State.FAILED -> Log.d("~~~", "fail")
        WorkInfo.State.RUNNING -> Log.d("~~~", "running")
        WorkInfo.State.ENQUEUED -> Log.d("~~~", "enqueued")
        WorkInfo.State.CANCELLED -> Log.d("~~~", "cancelled")
        WorkInfo.State.BLOCKED -> Log.d("~~~", "blocked")
    }
}
```
首先通过 getWorkInfoByIdLiveData 获得任务信息的 `LiveData<WorkInfo>` 数据，然后观察此数据即可。也可以通过 getWorkInfosByTagLiveData 获得相同 Tag 的 `LiveData<List<WorkInfo>>`，观察这个任务信息列表。通过 WorkInfo 的 getState 方法获取任务状态，主要用到的状态有 `WorkInfo.State.SUCCEEDED` 和 `WorkInfo.State.FAILED`，标志着任务的成功和失败。
## 4.6 传递数据
```kotlin
val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .setInputData(Data.Builder().apply {
        putString("key", "value")
    }.build())
    .build()
```
SimpleWorker 类中读取此数据：
```kotlin
inputData.getString("key")
```
## 4.7 链式任务
```kotlin
val first = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .build()
val second = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .build()
val third = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
    .build()
WorkManager.getInstance(this)
    .beginWith(first)
    .then(second)
    .then(third)
    .enqueue()
```
通过 beginWith 发起链式任务，然后后缀 then 即可，任务会按照连接顺序依次执行。WorkManager 要求必须在上一个任务执行成功后，才会执行下一个任务。也就是说任何一个任务的失败都会导致链式任务的中断。

# 参考文章
[《第一行代码》（第三版）- 第 13 章 13.6 WorkManager](https://blog.csdn.net/guolin_blog/article/details/105233078)