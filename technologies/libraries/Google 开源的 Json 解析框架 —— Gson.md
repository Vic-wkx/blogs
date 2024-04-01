@[TOC](目录)
# 简介
 Gson是一款帮助解析Json，JsonArray和将Java对象转换为Json格式的框架。

Github地址：[https://github.com/google/gson](https://github.com/google/gson)

# 使用方式
## 1.在app模块的build.gradle的dependencies中引入Gson
```xml
implementation 'com.google.code.gson:gson:2.8.2'
```
## 2.在AndroidManifest中添加网络权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

# 一、将json解析为java对象

## 效果图

![](https://img-blog.csdn.net/20180411200659527?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

## 代码实现
Gson解析Json对象非常简单，只需要将Json格式对应的Java对象传入即可。

本例中通过网址  [https://wkxjc.github.io/test_json.json](https://wkxjc.github.io/test_json.json) 请求了一个json对象，对象格式为
```xml
{"id":"1","name":"name_one","age":"11"}
```

分析此对象格式，创建对应的Java对象


```java
public class Message {
    private String id;
    private String name;
    private String age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "id = "+id+
                "\nname = "+name+
                "\nage = "+age;
    }
}
```
我在这个对象中重写了toString()方法，以方便后面查看此对象

然后使用Gson的fromJson()方法将请求到的Json对象解析成对应的Java对象


```java
Gson gson = new Gson();
Message message = gson.fromJson(jsonString,Message.class);
```
完整代码如下，这里使用了Volley获取Json对象，对Volley的使用尚不熟悉的读者，可以先去看Volley的基本使用：[https://blog.csdn.net/AlpinistWang/article/details/86773278](https://blog.csdn.net/AlpinistWang/article/details/86773278)，Volley同样是一个非常简单的框架。


```java
public class MainActivity extends AppCompatActivity {
    
    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text_view);

        requestJson();//Volley请求Json
    }
    private void requestJson() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://wkxjc.github.io/test_json.json",
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJsonWithGson(response.toString());//Gson解析Json对象
                    }
                }, null);
                mQueue.add(jsonObjectRequest);
            }
        });
    }

    private void parseJsonWithGson(String jsonString) {
        Gson gson = new Gson();
        final Message message = gson.fromJson(jsonString,Message.class);
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(message.toString());
            }
        });
    }

}

```
# 二、将jsonArray解析为ArrayList

## 效果图
![](https://img-blog.csdn.net/20180411202527837?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

## 代码实现
Gson解析JsonArray时，使用TypeToken将数组类型传入到framJson()中即可

本例中通过https://wkxjc.github.io/test_json_array.json获取了一段JsonArray数据：


```xml
[
    { "id":"1","name":"Name One","age":"1 year" },
    { "id":"2","name":"Name Two","age":"2 years" }
]
```


```xml
Gson gson = new Gson();
List<Message> messageList = gson.fromJson(jsonArrayString, new TypeToken<List<Message>>(){}.getType());
```
完整代码


```java
public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text_view);

        requestJsonArray();//Volley请求JsonArray
    }
    private void requestJsonArray() {
        textView.setText("");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://wkxjc.github.io/test_json_array.json",
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(final JSONArray response) {
                                parseJsonArrayWithGson(response.toString());//Gson解析JsonArray
                            }
                        },null);
                mQueue.add(jsonArrayRequest);
            }
        });
    }

    private void parseJsonArrayWithGson(String jsonArrayString) {
        Gson gson = new Gson();
        final List<Message> messageList = gson.fromJson(jsonArrayString, new TypeToken<List<Message>>(){}.getType());
        textView.post(new Runnable() {
            @Override
            public void run() {
                for(Message message:messageList){
                    textView.append(message.toString()+"\n");
                }
            }
        });
    }

}
```
# 三、解析Json套Json、Json套JsonArray、JsonArray套Json、JsonArray套JsonArray数据
## 效果图
![](https://img-blog.csdn.net/20180411204245181?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)
## 代码实现
本例中通过https://wkxjc.github.io/test_json_fixed.json获取了一段Json套Json的数据，数据格式如下：

```xml

{
  "house":"402",
  "number":"13",
  "message":
    {"age":"1 year","id":"1","name":"Name One"}
}
```
使用Gson解析这样类型的数据和解析Json数据一样，只是把嵌套的Json封装成一个单独的Java对象

先创建Message对象


```java
public class Message {
    private String id;
    private String name;
    private String age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "id = "+id+
                "\nname = "+name+
                "\nage = "+age;
    }
}
```
然后创建Classroom对象，Classroom对象中是包含Message对象的


```java
public class Classroom {
    private String number;
    private String house;
    private Message message;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return "number = "+number+
                "\nhouse = "+house+
                "\nmessage = "+message.toString();
    }
}
```

完整代码如下：
```java
public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text_view);

        requestJson();//Volley请求Json
    }
    private void requestJson() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://wkxjc.github.io/test_json_fixed.json",
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJsonWithGson(response.toString());//Gson解析Json对象
                    }
                }, null);
                mQueue.add(jsonObjectRequest);
            }
        });
    }

    private void parseJsonWithGson(String jsonString) {
        Gson gson = new Gson();
        final Classroom classroom = gson.fromJson(jsonString,Classroom.class);
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(classroom.toString());
            }
        });
    }

}
```
同理，Json套JsonArray和解析Json过程一样，只是把嵌套的jsonArray单独封装一个对象即可。

JsonArray套Json的数据和解析JsonArray过程一样，只是把嵌套json单独封装一个对象即可。

JsonArray套JsonArray的数据和解析JsonArray过程一样，只是把嵌套jsonArray单独封装一个对象即可。

## Json格式分析工具
这里推荐两个分析json字符串格式的工具
### 1.在线代码格式化：
[http://tool.oschina.net/codeformat/json](http://tool.oschina.net/codeformat/json)

打开会看到以下界面：
![](https://img-blog.csdn.net/20180418095957697?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

将json字符串填入待格式化json文本框内，点击下方格式化就可以看到缩进后的json对象，使用此工具可以很好的帮助我们分析json格式。

### GsonFormat插件
使用Android Studio的GsonFormat插件也可以分析json格式，并且GsonFormat支持根据json自动生成Java对象，非常的好用，强烈推荐安装。在Preferences的Plugins中搜索GsonFormat并安装
![](https://img-blog.csdn.net/20180628222226369?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

装好插件后重启Android Studio，然后使用GsonFormat根据json字符串生成对象：

![](https://img-blog.csdn.net/2018062822255428?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

新建一个对象类，根据自己的需要命名。然后使用Alt+S，打开GsonFormat，（或者使用代码提示，Mac环境使用command+N，windows环境使用ctrl+N，找到GsonFormat并打开），将json格式字符串粘贴到GsonFormat中，GsonFormat右上角的Format可以将字符串格式化，点击OK可以自动根据json字符串格式生成java对象。然后就可以使用此对象解析json了。需要注意的是GsonFormat生成的对象会将嵌套的对象放在当前java类中，根据key+"Bean"的方式命名，如本例中的json字符串自动生成的对象格式为：

```java

public class Message {

    /**
     * house : 402
     * number : 13
     * message : {"age":"1 year","id":"1","name":"Name One"}
     */

    private String house;
    private String number;
    private MessageBean message;

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public MessageBean getMessage() {
        return message;
    }

    public void setMessage(MessageBean message) {
        this.message = message;
    }

    public static class MessageBean {
        /**
         * age : 1 year
         * id : 1
         * name : Name One
         */

        private String age;
        private String id;
        private String name;

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
```
解析json时，可以直接使用此对象去解析，需要使用MessageBean对象时通过getMessage获取。

# 四、将java对象转化为json格式

调用Gson的toJson()方法，就可以将任意Java对象转换成Json对象

例如，使用上文中的Message对象和Classroom对象生成Json对象：

```java
Gson gson = new Gson();
Message message = new Message();
message.setId("1");
message.setName("Name One");
message.setAge("1 year");
String jsonStringMessage = gson.toJson(message);
```
代码运行结果：
```xml
jsonStringMessage = 
{"id":"1","name":"name_one","age":"11"}
```
List<Message>生成json对象：
```java
Gson gson = new Gson();
Message message1 = new Message();
message1.setId("1");
message1.setName("Name One");
message1.setAge("1 year");
Message message2 = new Message();
message2.setId("2");
message2.setName("Name Two");
message2.setAge("2 years");
List<Message> messageList = new ArrayList<>();
messageList.add(message1);
messageList.add(message2);
String jsonArrayStringMessageList = gson.toJson(messageList);
```
代码运行结果，
```xml
jsonArrayStringMessageList = 
[
    { "id":"1","name":"Name One","age":"1 year" },
    { "id":"2","name":"Name Two","age":"2 years" }
]
```
嵌套对象生成Json对象
```java
Gson gson = new Gson();
Classroom classroom = new Classroom();
Message message = new Message();
message.setId("1");
message.setName("Name One");
message.setAge("1 year");
classroom.setNumber("13");
classroom.setHouse("402");
classroom.setMessage(message);
String jsonStringClassroom = gson.toJson(classroom);
```
代码运行结果：
```xml
jsonStringClassroom = 
{
  "house":"402",
  "number":"13",
  "message":
    {"age":"1 year","id":"1","name":"Name One"}
}
```
以上，就是Gson的基本使用

# 源码已上传：

[https://github.com/wkxjc/GsonStudy](https://github.com/wkxjc/GsonStudy)