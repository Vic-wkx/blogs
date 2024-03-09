@[TOC](目录)
# 简介 
Volley、Okhttp、Retrofit都是用于发送和接收网络数据的框架。可以使用其进行GET请求和POST请求。本篇讲解通过GET方法请求String、Json、JsonArray，以及通过POST方法发送数据。 

# Volley篇
Volley的Github 地址：[https://github.com/google/volley](https://github.com/google/volley)

## 一、请求String

### 1.1.效果图
[外链图片转存失败(img-SM460GYs-1569317361196)(//img-blog.csdn.net/20180322022811400?watermark/2/text/Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)]

### 1.2.代码实现
#### 1.在app模块的build.gradle的dependencies中引入Volley
```xml
implementation 'com.android.volley:volley:1.1.0'
```
#### 2.在AndroidManifest中添加网络权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

#### 3.创建一个RequestQueue对象
```java
RequestQueue mQueue = Volley.newRequestQueue(this);
```

#### 4. 创建一个StringRequest对象

```java
StringRequest stringRequest = new StringRequest("https://wkxjc.github.io/test_string.txt",
        new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(response);
                    }
                });
            }
        }, null);
 ```
    StringRequest(String url, Listener<String> listener, ErrorListener errorListener)参数说明：

（1）String url 请求的URL地址

（2）Listener<String> listener 请求成功的回调，在onResponse(String response)中的response就是请求到的数据。

    这里把请求到的数据显示到了textView里面，由于在子线程中不能更新UI，所以使用了post方法更新UI，原理和使用Handler相同。

（3）ErrorListener errorListener 请求失败的回调



#### 5. 将StringRequest对象添加到RequestQueue里。

```java
mQueue.add(stringRequest);
```
#### MainActivity的完整代码
```java
public class MainActivity extends AppCompatActivity {
    private Button button;
    private TextView textView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);

        requestString();//Volley请求String
    }

    private void requestString() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest("https://wkxjc.github.io/test_string.txt",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(response);
                                    }
                                });
                            }
                        }, null);
                mQueue.add(stringRequest);
            }
        });
    }
}
```
这就是Volley的通过GET获取String的方法。



## 二、请求Json

### 2.1.效果图

![](https://img-blog.csdn.net/20180402155642317?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

### 2.2.代码实现

1，2，3，5步和 一、请求String 完全一样，只有第四步的StringRequest变成了JsonObjectRequest


```java
JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://wkxjc.github.io/test_json.json",
        null, new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(final JSONObject response) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(response.toString());
            }
        });
    }
}, null);

```
完整代码为：


```java
public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);

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
                    public void onResponse(final JSONObject response) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(response.toString());
                            }
                        });
                    }
                }, null);
                mQueue.add(jsonObjectRequest);
            }
        });
    }

}
```

## 三、请求JsonArray

### 3.1.效果图
![](https://img-blog.csdn.net/20180402155704670?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)


### 3.2.代码实现

1，2，3，5步和 一、请求String 完全一样，只有第四步的StringRequest变成了JsonArrayRequest


```java
JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://wkxjc.github.io/test_json_array.json",
        new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(response.toString());
                    }
                });
            }
        },null);

```
完整代码为：


```java
public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);

        requestJsonArray();//Volley请求JsonArray
    }
    private void requestJsonArray() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://wkxjc.github.io/test_json_array.json",
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(final JSONArray response) {
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(response.toString());
                                    }
                                });
                            }
                        },null);
                mQueue.add(jsonArrayRequest);
            }
        });
    }
}
```

## 四、POST数据

代码实现：

1，2，3，5步和 一、请求String 完全一样，只有第四步，先把Request的第一个参数设置为Request.Method.POST，再在StringRequest的匿名类中重写getParams()方法，在这里设置POST参数。


```java
StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://wkxjc.github.io/test_post.txt",
        new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(response);
                    }
                });
            }
        }, null) {
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> map = new HashMap<>();
        map.put("params1", "value1");
        map.put("params2", "value2");
        return map;
    }
};
```
完整代码：


```java
public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.text);

        requestStringPOST();//Volley POST数据
    }
    private void requestStringPOST() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://wkxjc.github.io/test_post.txt",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(response);
                                    }
                                });
                            }
                        }, null) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("params1", "value1");
                        map.put("params2", "value2");
                        return map;
                    }
                };
                mQueue.add(stringRequest);
            }
        });
    }

}
```
以上，就是Volley的基本用法：使用GET请求String、Json、JsonArray+使用POST传递数据



# 源码已上传

[https://github.com/wkxjc/VolleyStudy](https://github.com/wkxjc/VolleyStudy)



# Okhttp篇
Okhttp的Github地址为：https://github.com/square/okhttp

## 1.在app模块的build.gradle的dependencies中引入Okhttp


```xml
implementation 'com.squareup.okhttp3:okhttp:3.10.0'
```
## 2.在AndroidManifest中添加网络权限
```
<uses-permission android:name="android.permission.INTERNET" />
```
## 3.发送Get请求


```java
OkHttpClient client = new OkHttpClient();
Request request = new Request.Builder()
        .url("https://wkxjc.github.io/test_json_fixed.json")
        .build();
client.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String strResponse = response.body().string();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(strResponse);
            }
        });
    }
});
```
在enqueue中传入Callback，网络请求会异步执行，执行完后运行Callback中的回调方法onResponse或者onFailure

## 4.发送Post请求


```java
OkHttpClient client = new OkHttpClient();
FormBody formBody = new FormBody.Builder()
        .add("params1","value1")
        .add("params2","value2")
        .build();
Request request = new Request.Builder()
        .url("https://wkxjc.github.io/test_post.txt")
        .post(formBody)
        .build();
client.newCall(request).enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final String strResponse = response.body().string();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(strResponse);
            }
        });
    }
});

```
将需要post的参数封装在FormBody中，在新建request对象时，使用Builder().post方法传入formBody，之后的步骤和Get请求一样，在enqueue中传入Callback，网络请求会异步执行，执行完后运行Callback中的回调方法onResponse或者onFailure



# Retrofit篇
Retrofit的Github地址为：https://github.com/square/retrofit

## 1.在app模块的build.gradle的dependencies中引入Retrofit


```xml
implementation 'com.squareup.retrofit2:retrofit:2.4.0'
```
## 2.在AndroidManifest中添加网络权限
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
## 3.发送Get请求

Retrofit是使用注解的方式区分Get和Post的，首先新建一个接口文件IWebUtil：


```java
public interface IWebUtil {
    @GET("test_json_fixed.json")
    Call<ResponseBody> getInfo();
}
```
在程序中使用：


```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://wkxjc.github.io/")
        .build();
IWebUtil util = retrofit.create(IWebUtil.class);
Call<ResponseBody> call = util.getInfo();
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            text.setText(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
});
```
创建Retrofit对象的时候，传入了一个baseUrl，这个baseUrl规范是以'/'结尾，它和注解中括号内的字符串将组成网络请求的url。

在enqueue中传入Callback，网络请求会异步执行，执行完后运行Callback中的回调方法onResponse或者onFailure。这点和Okhttp一样，因为Retrofit内部就是通过Okhttp实现的。Retrofit只是将Okhttp封装了一下而已。不过Retrofit还帮我们做了一件事情，就是将onResponse和onFailure函数切换到了主线程执行。

## 4.发送Post 请求

前面已经说到，Retrofit是用注解区分GET和POST请求的，所以同样的，我们在接口中加入@POST注解


```java
public interface IWebUtil {
    @POST("test_post.text")
    @FormUrlEncoded
    Call<ResponseBody> postForm(@FieldMap Map<String,Object> map);
}
```
在程序中使用：


```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://wkxjc.github.io/")
        .build();
IWebUtil util = retrofit.create(IWebUtil.class);
Map<String,Object> map = new HashMap<>();
map.put("params1","value1");
map.put("params2","value2");
Call<ResponseBody> call = util.postForm(map);
call.enqueue(new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
               text.setText(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }
});
```
将需要post的值封装在HashMap中，使用postForm将这个HashMap传入即可。之后的步骤和Get请求一样，在enqueue中传入Callback，网络请求会异步执行，执行完后运行Callback中的回调方法onResponse或者onFailure。onResponse和onFailure同样是在主线程中执行的。

以上就是Retrofit的Get和Post方法的基本使用。以上方法的callback中，都是返回responseBody对象，拿到这个对象我们就可以解析了。Retrofit还可以与解析json的框架结合使用，下面介绍retrofit和gson结合使用解析json字符串。

## 5.Retrofit结合Gson，返回指定类型对象

Retrofit和Gson结合使用非常简单，只需要在构建Retrofit对象时增加一句 addConverterFactory(GsonConverterFactory.create()) ，我们就可以将Callback中返回的ResponseBody改成自己需要的格式了。不过要使用Gson，先要在app的build.gradle中导入Retrofit和Gson结合的包


```xml
implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
```
本例中，通过 [https://wkxjc.github.io/test_json.json](https://wkxjc.github.io/test_json.json) 这个网址获取一段json字符串，字符串结构为：


```json
{"id":"1","name":"name_one","age":"11"}
```
根据此结构，先创建Message对象：


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
}
```
IWebUtil中的GET方法：


```java
public interface IWebUtil {
    @GET("test_json.json")
    Call<Message> getInfo();
}
```
程序中调用：


```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://wkxjc.github.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
IWebUtil util = retrofit.create(IWebUtil.class);
Call<Message> call = util.getInfo();
call.enqueue(new Callback<Message>() {
    @Override
    public void onResponse(Call<Message> call, Response<Message> response) {
        Message message = response.body();
        if (message!= null) {
            text.setText(message.getName());
        }
    }
    @Override
    public void onFailure(Call<Message> call, Throwable t) {

    }
});
```
Retrofit就是这么简单。Retrofit和Rxjava现在非常的火，使用也很广泛。很多人都不知道怎么下手，或者是有畏难情绪。包括笔者之前也一直嫌Retrofit注解麻烦，学习之后发现Retrofit的注解帮我们使用网络请求省去了很多操作。其实这些流行的库并不难，毕竟它们都是为了程序员更简单的编程而诞生的。后面我会再写一篇Rxjava的文章。