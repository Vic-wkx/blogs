@[TOC]

[本文已授权郭霖公众号独家发布](https://mp.weixin.qq.com/s/KXhtNwZh_e10toKI6HDQkg)
# 简介
受郭神鼓舞，我也参加了一下 Android 开发者挑战赛。本周的题目是用 Compose 写一个 Countdown Timer。
![](https://img-blog.csdnimg.cn/img_convert/f3c7b3e540a928c3fc09517fa74663ee.png)
# 效果图
虽然是个小项目，但 Compose 的资料实在是太少了，不断地摸索，加上同事的帮助，花费了一天的工夫才做出来，效果如下：
![](https://img-blog.csdnimg.cn/img_convert/56bc95909fa52a141bb0a10a33749e4f.png)
# 数据结构
首先分析数据结构，我们需要保存用户设置的总时间、当前倒计时剩余时间。除此之外就没有其他数据需要保存了。

TimerViewModel 类如下：
```kotlin
// Max input length limit, it's used to prevent number grows too big.
const val MAX_INPUT_LENGTH = 5

class TimerViewModel : ViewModel() {

    /**
     * Total time user set in seconds
     */
    var totalTime: Long by mutableStateOf(0)

    /**
     * Time left during countdown in seconds
     */
    var timeLeft: Long by mutableStateOf(0)

    /**
     * Update value when EditText content changed
     * @param text new content in EditText
     */
    fun updateValue(text: String) {
        // Just in case the number is too big
        if (text.length > MAX_INPUT_LENGTH) return
        // Remove non-numeric elements
        var value = text.replace("\\D".toRegex(), "")
        // Zero cannot appear in the first place
        if (value.startsWith("0")) value = value.substring(1)
        // Set a default value to prevent NumberFormatException
        if (value.isBlank()) value = "0"
        totalTime = value.toLong()
        timeLeft = value.toLong()
    }
}
```
其中，`updateValue` 函数用于当用户输入倒计时总秒数后，更新 TimerViewModel 中的 totalTime 和 timeLeft 的值。

为了防止数字过大，我们只允许用户输入 5 位数，并且用正则表达式过滤掉用户输入的小数点、负号、逗号分隔符等非数值。并且数字首位不允许出现 0。经过层层处理，将安全的数值赋给 totalTime 和 timeLeft。
# 倒计时功能
实现倒计时有很多种方式，比如:

* 我们熟悉的 `handler.postDelayed` 的方式
* 在协程中 `repeat + delay` 的方式
* 使用 ValueAnimator 的方式

我采用的是第三种方式，因为动画相对来说较容易控制，pause、resume、cancel 函数都是现成的，可以很方便的实现暂停、继续、停止等功能。

AnimatorController 类如下：
```kotlin
class AnimatorController(private val viewModel: TimerViewModel) {

    private var valueAnimator: ValueAnimator? = null

    fun start() {
        if (viewModel.totalTime == 0L) return
        if (valueAnimator == null) {
            // Animator: totalTime -> 0
            valueAnimator = ValueAnimator.ofInt(viewModel.totalTime.toInt(), 0)
            valueAnimator?.interpolator = LinearInterpolator()
            // Update timeLeft in ViewModel
            valueAnimator?.addUpdateListener {
                viewModel.timeLeft = (it.animatedValue as Int).toLong()
            }
            valueAnimator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    complete()
                }
            })
        } else {
            valueAnimator?.setIntValues(viewModel.totalTime.toInt(), 0)
        }
        // (LinearInterpolator + duration) aim to set the interval as 1 second.
        valueAnimator?.duration = viewModel.totalTime * 1000L
        valueAnimator?.start()
    }

    fun pause() {
        valueAnimator?.pause()
    }

    fun resume() {
        valueAnimator?.resume()
    }

    fun stop() {
        valueAnimator?.cancel()
        viewModel.timeLeft = 0
    }

    fun complete() {
        viewModel.totalTime = 0
    }
}
```
在这个类中，我们处理了动画的启动、暂停、恢复、停止和完成五个功能。通过将 ValueAnimator 设置为在 `totalTime` 秒内从 `totalTime` 线性变化到 `0` 的方式设置出动画的间隔时间为 `1s`。

为了方便使用，我们将创建的 AnimatorController 对象放到 TimerViewModel 中：
```kotlin
class TimerViewModel : ViewModel() {
    //...

    var animatorController = AnimatorController(this)
}
```

# 状态模式
分析可知，倒计时 App 可分为四个状态：

* 尚未开始
* 已经开始
* 暂停
* 完成

由此，我们很容易想到用状态模式来设计此 App，只要为每个状态创建一个状态类，就可以减少大量的 `if-else` 语句和 `when` 语句。

> 状态模式（State Pattern）：当一个对象的内在状态改变时允许改变其行为，这个对象看起来像是改变了其类。

在不同状态下，App 的表现和行为是不同的，我们先将这些不同的部分提取到接口中，大致有如下几个方法：

```kotlin
interface IStatus {
    /**
     * The content string displayed in Start Button.
     * include: Start, Pause, Resume.
     */
    fun startButtonDisplayString(): String

    /**
     * The behaviour when click Start Button.
     */
    fun clickStartButton()

    /**
     * Stop Button enable status
     */
    fun stopButtonEnabled(): Boolean

    /**
     * The behaviour when click Stop Button.
     */
    fun clickStopButton()

    /**
     * Show or hide EditText
     */
    fun showEditText(): Boolean
}
```
接口中抽出了五个函数，对应 App 在不同状态下的表现和行为：

* `fun startButtonDisplayString(): String` 用于控制 Start 按钮上的文字显示，在`	尚未开始`/`完成`状态下，按钮显示的文字为 "Start"，在`已经开始`状态下，按钮显示文字为 "Pause"，在`暂停`状态下，按钮显示文字为 "Resume"。
* `fun clickStartButton()` 用于控制 Start 按钮的点击事件，在`	尚未开始`/`完成`状态下，点击 Start 按钮启动 ValueAnimator，在`已经开始`状态下，点击 Start 按钮暂停 ValueAnimator，在`暂停`状态下，点击 Start 按钮恢复 ValueAnimator。
* `fun stopButtonEnabled(): Boolean` 用于控制 Stop 按钮是否可点击，在`尚未开始`/`完成`状态下，Stop 按钮不可点击，在`已经开始`/`暂停`状态下，Stop 按钮可点击。
* `fun clickStopButton()` 用于控制 Stop 按钮的点击事件，在`尚未开始`/`完成`状态下，Stop 按钮不可点击，点击事件为空，在`已经开始`/`暂停`状态下，点击 Stop 按钮停止 ValueAnimator。
* `fun showEditText(): Boolean` 用于控制 EditText 的显示和隐藏，在`尚未开始`/`完成`状态下，EditText 显示，在`已经开始`/`暂停`状态下，EditText 隐藏。

通过以上分析，我们可以写出以下四个状态类：

`尚未开始`状态：
```kotlin
class NotStartedStatus(private val viewModel: TimerViewModel) : IStatus {

    override fun startButtonDisplayString() = "Start"

    override fun clickStartButton() = viewModel.animatorController.start()

    override fun stopButtonEnabled() = false

    override fun clickStopButton() {}

    override fun showEditText() = true
}
```
`已经开始`状态：
```kotlin
class StartedStatus(private val viewModel: TimerViewModel) : IStatus {

    override fun startButtonDisplayString() = "Pause"

    override fun clickStartButton() = viewModel.animatorController.pause()

    override fun stopButtonEnabled() = true

    override fun clickStopButton() = viewModel.animatorController.stop()

    override fun showEditText() = false
}
```
`暂停`状态：
```kotlin
class PausedStatus(private val viewModel: TimerViewModel) : IStatus {

    override fun startButtonDisplayString() = "Resume"

    override fun clickStartButton() = viewModel.animatorController.resume()

    override fun stopButtonEnabled() = true

    override fun clickStopButton() = viewModel.animatorController.stop()

    override fun showEditText() = false
}
```
`完成`状态：
```kotlin
class CompletedStatus(private val viewModel: TimerViewModel) : IStatus {

    override fun startButtonDisplayString() = "Start"

    override fun clickStartButton() = viewModel.animatorController.start()

    override fun stopButtonEnabled() = false

    override fun clickStopButton() {}

    override fun showEditText() = true
}
```
同样地，将状态值保存到 ViewModel 中：
```kotlin
class TimerViewModel : ViewModel() {
    //...
    
    var status: IStatus by mutableStateOf(NotStartedStatus(this))
}
```
最后，因为四种状态的改变和动画的状态是息息相关的，所以我们可以将状态转移的代码添加到 AnimatorController 类中：
```kotlin
class AnimatorController(private val viewModel: TimerViewModel) {
    //...

    fun start() {
        //...
        viewModel.status = StartedStatus(viewModel)
    }

    fun pause() {
        //...
        viewModel.status = PausedStatus(viewModel)
    }

    fun resume() {
        //...
        viewModel.status = StartedStatus(viewModel)
    }

    fun stop() {
        //...
        viewModel.status = NotStartedStatus(viewModel)
    }

    fun complete() {
        //...
        viewModel.status = CompletedStatus(viewModel)
    }
}
```
# Compose 布局
整个布局中，除了时钟的绘制稍微复杂一点外，其他的 UI 都还比较简单。时钟的绘制我们稍后再讲，先从简单的讲起。

## TimeLeftText
Compose 中，对应 TextView 的函数是 Text，展示剩余时间的 Text 如下：
```kotlin
@Composable
private fun TimeLeftText(viewModel: TimerViewModel) {
    Text(
        text = TimeFormatUtils.formatTime(viewModel.timeLeft),
        modifier = Modifier.padding(16.dp)
    )
}
```
通过 text 属性为其设置文字，modifier 属性为其添加了一个 16dp 的 padding。

其中，TimeFormatUtils 工具类代码如下：
```kotlin
object TimeFormatUtils {

    fun formatTime(time: Long): String {
        var value = time
        val seconds = value % 60
        value /= 60
        val minutes = value % 60
        value /= 60
        val hours = value % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
```
此工具用于格式化时间，测试类：
```kotlin
class TimeFormatUtilsTest : TestCase() {
    @Test
    fun test() {
        Assert.assertEquals("00:00:00", TimeFormatUtils.formatTime(0))
        Assert.assertEquals("00:00:30", TimeFormatUtils.formatTime(30))
        Assert.assertEquals("00:01:00", TimeFormatUtils.formatTime(60))
        Assert.assertEquals("00:10:30", TimeFormatUtils.formatTime(630))
        Assert.assertEquals("01:40:00", TimeFormatUtils.formatTime(6000))
        Assert.assertEquals("27:46:39", TimeFormatUtils.formatTime(99999))
    }
}
```
## EditText
Compose 中，对应 EditText 的函数是 TextField，本例中，TextField 用于提供给用户输入倒计时总秒数。代码如下：
```kotlin
@Composable
private fun EditText(viewModel: TimerViewModel) {
    Box(
        modifier = Modifier
            .size(300.dp, 120.dp),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.status.showEditText()) {
            TextField(
                modifier = Modifier
                    .size(200.dp, 60.dp),
                value = if (viewModel.totalTime == 0L) "" else viewModel.totalTime.toString(),
                onValueChange = viewModel::updateValue,
                label = { Text("Countdown Seconds") },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}
```
其中，我们通过状态类中的 `showEditText` 函数来控制是否需要绘制 TextField，需要注意的是，Compose 中没有 View 的可见行这一概念（也可能是我没找到...)，只能通过 if 条件句来控制 View 是否绘制。这在实现 GONE 效果时非常方便，View 也比以前的 GONE 效果消失得更彻底，但本例中，我想实现的效果其实是 INVISIBLE，因为 TextField 的 GONE 效果会导致 Column 中其他控件移位。在 Compose 中，想要实现 INVISIBLE 效果就只能在 View 外层加一层嵌套，使其仍然占有之前控件的位置。Compose 中的 Box 函数类似之前的 FrameLayout 效果。（如果读者有更好的实现方案欢迎指正。）

当 TextField 中的值将要发生改变时，onValueChange 代码块就会被调用，我们通过 viewModel 的 updateValue 函数干预此过程，保证 viewModel 中只会赋上安全的值。

keyboardOptions 属性用于控制输入类型，虽然这里指定为输入 Number 类型了，但小数点、负号、逗号分隔符无法被过滤掉，这也是 updateValue 中使用正则表达式对输入的字符再过滤一次的原因。

## StartButton
在 Compose 中，Button 不再是一个单纯的 View，Button 函数的最后一个参数是 RowScope，它更像是一个 ViewGroup，其中的内容由我们自己定义：
```kotlin
@Composable
private fun StartButton(viewModel: TimerViewModel) {
    Button(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp),
        enabled = viewModel.totalTime > 0,
        onClick = viewModel.status::clickStartButton
    ) {
        Text(text = viewModel.status.startButtonDisplayString())
    }
}
```
比如此例中，我们在 Button 中绘制了一个 Text，这样去实现一个普通的 Button 效果。Compose 中的 Button 使用起来更为灵活，也就是说，为 XXLayout 添加点击事件的时代已经过去，在 Compose 中，可以直接使用 Button 应付这类场景。

## StopButton
StopButton 和 StartButton 是类似的：
```kotlin
@Composable
private fun StopButton(viewModel: TimerViewModel) {
    Button(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp),
        enabled = viewModel.status.stopButtonEnabled(),
        onClick = viewModel.status::clickStopButton
    ) {
        Text(text = "Stop")
    }
}
```
读者可能会产生疑惑，我们在写这几个控件时，只在初始化的时候定义了一下控件的状态，如显示的文字、enabled 状态等。却没有看到 viewModel.status 改变后，更新控件状态的代码。但实际上此时控件的状态已经会自动随着 viewModel.status 的改变而改变了。Compose 是如何做到这点的呢？

魔法就在这一句中：
```kotlin
var status: IStatus by mutableStateOf(NotStartedStatus(this))
```
只要是通过 `by mutableStateOf` 代理的属性，Compose 中使用了此属性的组件就会与此属性自动建立起订阅关系，当此属性发生改变时，Compose 中使用此属性的位置就会自动更新。这和 DataBinding、LiveData 等是类似的，都是观察者模式的运用。

## Scaffold
以上各个控件都已经写好，只需将他们组合起来就行了：
```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release memory
        viewModel.animatorController.stop()
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    val viewModel: TimerViewModel = viewModel()
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name)
                    )
                }
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimeLeftText(viewModel)
            EditText(viewModel)
            Row {
                StartButton(viewModel)
                StopButton(viewModel)
            }
        }
    }
}
```

其中，Scaffold 是 Material Design 中的概念，它通常包含一个 topBar、一个 bottomBar、一个 floatingActionButton ，剩余部分称之为 body。

在此例中，我们给 topBar 声明为带一个 Text 的 TopAppBar，实现之前 ActionBar 的效果。

控件的布局采用了基础的 Column 和 Row（列和行），分别对应之前 LinearLayout 的 `android:orientation="vertical"` 和 `android:orientation="horizontal"`。

Ok，到这里我们可以先运行一下看看效果了，如下图所示：

![](https://img-blog.csdnimg.cn/img_convert/f10e6beceabdbd9bfe31c989a3639e6e.gif)
可以看出，我们需要的功能都成功实现了。

接下来我们在中心区域绘制一个时钟，装饰一下我们的 App，顺便看下如何用 Compose 自定义 View。

## 绘制时钟
绘制时钟前，需要先在 IStatus 中新增一个方法：
```kotlin
interface IStatus {
    //...
    
    /**
     * Sweep angle of progress circle
     */
    fun progressSweepAngle(): Float
}
```
此方法用于表示圆环扫过的度数值。

在 StartedStatus 和 PausedStatus 中，此数值的计算方式为：
```kotlin
override fun progressSweepAngle() = viewModel.timeLeft * 1.0f / viewModel.totalTime * 360
```

在 NotStartedStatus 中，此数值为：
```kotlin
override fun progressSweepAngle() = if (viewModel.totalTime > 0) 360f else 0f
```
在 CompletedStatus 中，此数值为 0f：
```kotlin
override fun progressSweepAngle() = 0f
```
然后再通过此数值绘制对应的形状即可。

Compose 中自定义 View 需要使用 `androidx.compose.foundation.Canvas` 类，其中的 drawXXX 方法与之前 Android 中的 Canvas 都是类似的。Compose 之所以要写一个自己的 Canvas 类，而不是直接使用 Android 中现成的 Canvas 类，就是为了使 Compose 不需要依赖 Android 平台，为以后实现跨平台先铺好路。

```kotlin
@Composable
fun ProgressCircle(viewModel: TimerViewModel) {
    // Circle diameter
    val size = 160.dp
    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier.size(size)
        ) {
            val sweepAngle = viewModel.status.progressSweepAngle()
            // Circle radius
            val r = size.toPx() / 2
            // The width of Ring
            val stokeWidth = 12.dp.toPx()
            // Draw dial plate
            drawCircle(
                color = Color.LightGray,
                style = Stroke(
                    width = stokeWidth,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(1.dp.toPx(), 3.dp.toPx())
                    )
                )
            )
            // Draw ring
            drawArc(
                brush = Brush.sweepGradient(
                    0f to Color.Magenta,
                    0.5f to Color.Blue,
                    0.75f to Color.Green,
                    0.75f to Color.Transparent,
                    1f to Color.Magenta
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = stokeWidth
                ),
                alpha = 0.5f
            )
            // Pointer
            val angle = (360 - sweepAngle) / 180 * Math.PI
            val pointTailLength = 8.dp.toPx()
            drawLine(
                color = Color.Red,
                start = Offset(r + pointTailLength * sin(angle).toFloat(), r + pointTailLength * cos(angle).toFloat()),
                end = Offset((r - r * sin(angle) - sin(angle) * stokeWidth / 2).toFloat(), (r - r * cos(angle) - cos(angle) * stokeWidth / 2).toFloat()),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = Color.Red,
                radius = 5.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx()
            )
        }
    }
}
```
可以看到，我们先通过设置 drawCircle 函数中的 pathEffect 参数，绘制出底部灰色的刻度盘，其中，intervals 中的第一个 Float 参数指刻度的宽度，第二个 Float 参数指刻度的间距。

然后从 -90° 开始绘制圆环，通过 sweepAngle 的变化使得圆环从 360° 减到 0°，通过 brush 参数设置出渐变色。

中间的指针部分通过正弦函数、余弦函数的变换计算出起点和终点，绘制出一条直线和两个圆，组成指针的形状。

然后将此控件添加到 TimeLeftText 下方：
```kotlin
Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    TimeLeftText(viewModel)
    ProgressCircle(viewModel)
    EditText(viewModel)
    Row {
        StartButton(viewModel)
        StopButton(viewModel)
    }
}
```
运行效果如下：
![](https://img-blog.csdnimg.cn/img_convert/e897db6213124aa586e924ab8867be2c.gif)
总体看起来还不错，但指针的动画效果还不够丝滑。这是因为我们每隔 `1s` 才会去更新一次指针扫过的角度，当时间很短时，指针的变化看起来就很突兀。

解决方案也很简单，将更新指针的时间设置得短一些就可以了。前文说到，我们通过将 ValueAnimator 设置为在 `totalTime` 秒内从 `totalTime` 线性变化到 `0` 的方式设置出动画的间隔时间为 `1s`。想要加快更新的频率，我们可以将动画的初始值扩大一个倍数，总时长保持不变。这时，就不能再使用 timeLeft 来计算扫过的角度了，我们需要一个新的值来保存动画过程中的值。
```kotlin
class TimerViewModel : ViewModel() {
    //...

    /**
     * Temp value when anim is active
     */
    var animValue: Float by mutableStateOf(0.0f)
}
```

在 AnimatorController 中，加快动画更新的频率：
```kotlin
// Control how many times the pointer will be updated in a second
const val SPEED = 100

class AnimatorController(private val viewModel: TimerViewModel) {
    //...
    
    fun start() {
        if (viewModel.totalTime == 0L) return
        if (valueAnimator == null) {
            // Animator: totalTime -> 0
            valueAnimator = ValueAnimator.ofInt(viewModel.totalTime.toInt() * SPEED, 0)
            valueAnimator?.interpolator = LinearInterpolator()
            // Update timeLeft in ViewModel
            valueAnimator?.addUpdateListener {
                viewModel.animValue = (it.animatedValue as Int) / SPEED.toFloat()
                viewModel.timeLeft = (it.animatedValue as Int).toLong() / SPEED
            }
            valueAnimator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    complete()
                }
            })
        } else {
            valueAnimator?.setIntValues(viewModel.totalTime.toInt() * SPEED, 0)
        }
        // (LinearInterpolator + duration) aim to set the interval as 1 second.
        valueAnimator?.duration = viewModel.totalTime * 1000L
        valueAnimator?.start()
        viewModel.status = StartedStatus(viewModel)
    }
}
```
在 StartedStatus 和 PausedStatus 中，使用这个 Float 值来更新 progressSweepAngle：
```kotlin
override fun progressSweepAngle() = viewModel.animValue / viewModel.totalTime * 360
```
这里我们将 SPEED 设置为 100，表示每秒钟更新 100 次指针的位置。修改后效果如下（实际效果比 gif 流畅很多，已经看不出卡顿）：
![](https://img-blog.csdnimg.cn/img_convert/1bca4e8e96e37c27bdc4ba83a10bbee0.gif)
## CompletedText
最后，我们在倒计时结束时添加一个回调，一般的倒计时 App 会在结束时播放一段铃声，简单起见，我们在结束时展示一个 "Completed!" 文字即可。

同样地，为了避免状态判断，我们利用状态模式的优点，在 IStatus 中添加一个方法：
```kotlin
interface IStatus {
    //...

    /**
     * Completed string
     */
    fun completedString(): String
}
```
在 CompletedStatus 中，重载此方法为：
```kotlin
override fun completedString() = "Completed!"
```
在其他 Status 子类中，重载此方法为：
```kotlin
override fun completedString() = ""
```
创建 CompletedText 控件：
```kotlin
@Composable
private fun CompletedText(viewModel: TimerViewModel) {
    Text(
        text = viewModel.status.completedString(),
        color = MaterialTheme.colors.primary
    )
}
```
然后，将这个控件添加到 TimeLeftText 上方：
```kotlin
Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    CompletedText(viewModel)
    TimeLeftText(viewModel)
    ProgressCircle(viewModel)
    EditText(viewModel)
    Row {
        StartButton(viewModel)
        StopButton(viewModel)
    }
}
```
最后，在 EditText 更新时，如果状态是`完成`，则将其修改为`尚未开始`状态，因为当用户编辑 EditText 时，说明新一轮倒计时即将到来。

```kotlin
fun updateValue(text: String) {
    //...
    // After user clicks EditText, CompletedStatus turns to NotStartedStatus.
    if (status is CompletedStatus) status = NotStartedStatus(this)
}
```
最终运行效果如下：
![](https://img-blog.csdnimg.cn/img_convert/9efa611694b3e9b9d7cdd8bb58caacaf.gif)
这样，我们就使用 Compose 实现了一个完整的 Countdown Timer。源码已上传 Github：[https://github.com/wkxjc/CountdownTimer](https://github.com/wkxjc/CountdownTimer)。