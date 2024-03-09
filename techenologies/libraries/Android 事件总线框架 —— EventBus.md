 @[TOC](目录)
 # 简介
EventBus是一个事件总线框架，类似广播，在需要传递数据的时候使用EventBus post数据，所有注册了EventBus的地方都会收到数据。类似微信公众号发一个文章之后，所有关注了这个公众号的人都会收到这篇文章。典型的观察者模式。

Github 地址：[https://github.com/greenrobot/EventBus](https://github.com/greenrobot/EventBus)

# 一、效果图
[外链图片转存失败(img-qlKYrhBo-1569317362388)(//img-blog.csdn.net/20180319203939528?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)]
# 二、代码实现
1.在app模块的build.gradle的dependencies中引入EventBus
```xml
implementation 'org.greenrobot:eventbus:3.1.1'
```
2.注册和销毁EventBus
在onCreate()中
```java
//注册EventBus
EventBus.getDefault().register(this);
```
在onDestroy()中
```java
if (EventBus.getDefault().isRegistered(this)) {
    EventBus.getDefault().unregister(this);
}
```
3.处理EventBus post的数据
```java
//收到EventBus发的消息并处理
@Subscribe(threadMode = ThreadMode.MAIN)
public void onEvent(String string) {
    mTextView.setText(string);
}
```
MainActivity完整代码为：
```java
public class MainActivity extends AppCompatActivity {
    TextView mTextView;
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text_view);
        mButton = findViewById(R.id.button);

        //注册EventBus
        EventBus.getDefault().register(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });
    }

    //收到EventBus发的消息并处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String string) {
        mTextView.setText(string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
```

4.SecondActivity中点击按钮发送数据
```java
//发送消息
String str = "Hello, EventBus";
EventBus.getDefault().post(str);
```

SecondActivity完整代码为：
```java
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送消息
                String str = "Hello, EventBus";
                EventBus.getDefault().post(str);
            }
        });
    }
}
```
这样就实现了EventBus传递数据

# 三、扩展：传递对象
和传递字符串一样，把字符串改成对象就可以了

## 效果图

[外链图片转存失败(img-LOoNoWd0-1569317362390)(//img-blog.csdn.net/20180319205510711?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)]

## 代码实现

1.新建Message类，这个对象有一个字符串，一张图片
```java
public class Message {
    private String text;
    private Drawable picture;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }
}
```
2.注册和销毁EventBus
在onCreate()中
```java
//注册EventBus
EventBus.getDefault().register(this);
```
在onDestroy()中
```java
if (EventBus.getDefault().isRegistered(this)) {
    EventBus.getDefault().unregister(this);
}
```
3.处理EventBus post的数据
```java
//收到EventBus发的消息并处理
@Subscribe(threadMode = ThreadMode.MAIN)
public void onEvent(Message message) {
    mTextView.setText(message.getText());
    mImageView.setImageDrawable(message.getPicture());

}
```
MainActivity完整代码为：
```java
public class MainActivity extends AppCompatActivity {
    TextView mTextView;
    ImageView mImageView;
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text_view);
        mImageView = findViewById(R.id.image_view);
        mButton = findViewById(R.id.button);

        //注册EventBus
        EventBus.getDefault().register(this);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });
    }

    //收到EventBus发的消息并处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Message message) {
        mTextView.setText(message.getText());
        mImageView.setImageDrawable(message.getPicture());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
```

4.SecondActivity发送数据
```java
//发送消息
Message message = new Message();
message.setText("Hello,EventBus");
message.setPicture(getResources().getDrawable(R.mipmap.ic_launcher));
EventBus.getDefault().post(message);
```
SecondActivity完整代码为：
```java
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送消息
                Message message = new Message();
                message.setText("Hello,EventBus");
                message.setPicture(getResources().getDrawable(R.mipmap.ic_launcher));
                EventBus.getDefault().post(message);
            }
        });
    }
}
```
这样就实现了EventBus传递对象

注：EventBus 不能post简单数据类型，当需要传递int，float，double，boolean值时，请将接收的参数类型设置为Integer，Float，Double，Boolean.

# 四、Sticky消息
在上一个例子中，我们在MainActivity中接收消息，然后启动SecondActivity，在SecondActivity中发送消息。这是因为EventBus发送的消息默认只能在Activity注册了EventBus之后才能收到。EventBus发送消息后再注册EventBus的Activity无法收到之前发送的消息。
也就是说，如果在MainActivity中发送消息，然后启动SecondActivity，在SecondActivity中接收消息。这样SecondActivity是接收不到消息的。
Sticky消息就是用来解决这一点的。如果在MainActivity中发送Sticky消息，然后启动SecondActivity，在SecondActivity中接收此Sticky消息时，将sticky设置为true。这样SecondActivity就能接收到此消息了。
EventBus会保存所有的Sticky消息，所以在不需要使用时，应该手动调用以下代码移除Sticky消息：
```kotlin
EventBus.getDefault().removeStickyEvent(data)
```
## 效果图
![](https://img-blog.csdnimg.cn/20190612184258150.gif)
## 代码如下
MainActivity中：
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSendSticky.setOnClickListener {
            EventBus.getDefault().postSticky("Hello Second, I'm Main.")
        }
        btnJumpToSecond.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }
    }
}
```
SecondActivity中：
```kotlin
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onEvent(data: String) {
        tvSecond.text = data
        EventBus.getDefault().removeStickyEvent(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}
```
注：同时发送多个同类型Sticky消息时，接收处只能收到最后一次发送的消息

我们还可以使用`getStickyEvent`方法获取Sticky消息，传入Sticky消息的类型即可。例如以上的SecondActivity也可以写成：
```kotlin
class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val data = EventBus.getDefault().getStickyEvent(String::class.java)
        EventBus.getDefault().removeStickyEvent(data)
        tvSecond.text = data
    }
}
```
# 五、接收EventBus的消息的方法上注解的含义

1.@Subscribe(threadMode = ThreadMode.MAIN)
表示该方法在主线程中执行，众所周知，在主线程中才可以进行UI操作

2.@Subscribe(threadMode = ThreadMode.BACKGROUND)
表示该方法在后台线程中执行

3.@Subscribe(threadMode = ThreadMode.POSTING)
 这是默认的模式，表示该方法的线程和执行post数据的线程相同

4.@Subscribe(threadMode = ThreadMode.ASYNC)
表示无论post数据的线程是什么，该方法始终会新建一个子线程运行

注：BACKGROUN和ASYNC不同的地方在于，如果执行post数据的线程是在子线程，BACKGROUND模式会直接在这个子线程中运行。而ASYNC模式仍然会新建一个子线程再运行

# 后记
现在我已经尽量不用EventBus了，使用RxJava中的Observable替换EventBus往往更好。因为EventBus无法跟踪消息是从哪里发出来的，如果这种“匿名消息”太多，往往造成调试困难。而Observable和Observer绑定相当于定点传送，可以跟踪来源和目的，所以我现在更推荐用Observable。
关于RxJava的使用可参考我的另一篇博客：
RxJava RxAndroid RxLifecycle基本使用（使用Kotlin语言）:[https://blog.csdn.net/AlpinistWang/article/details/83107659](https://blog.csdn.net/AlpinistWang/article/details/83107659)