@[TOC](目录)
# 一、组件化的基本操作

## 简介
Android组件化是指将项目划分为多个模块，并且每个模块可以单独的作为app运行。有利于不同业务的解耦，并且可以提高编译速度，提高协作开发的效率。

## 1.新建项目
取个简单的名字：Component：
![](https://img-blog.csdn.net/20180604173531843?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

建好之后长这样：

![](https://img-blog.csdn.net/20180604173700479?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

## 2.新建模块
既然是要分模块，那我们先建几个模块：
![](https://img-blog.csdn.net/20180604183412494?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
本例中，我们新建了Base模块，First模块，Second模块，Base模块用来放置每个模块都要用到的公共内容，First模块和Second模块是功能模块。新建项目时自动生成的app模块作为一个壳模块，用来整合其他模块。

## 3.gradle的统一管理

![](https://img-blog.csdn.net/20180605011639366?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

首先在项目根目录的build.gradle中，添加Sdk版本配置和依赖库的版本信息：


```xml
ext {
    cfg = [
            compileSdk       : 27,
            minSdk           : 15,
            targetSdk        : 27,
            versionCode      : 1,
            versionName      : "1.0"
    ]

    libs = [
            support             : "com.android.support:appcompat-v7:27.1.1",
            constraintLayout    : "com.android.support.constraint:constraint-layout:1.1.0"
    ]
}
```

添加了之后，就可以在模块的build.gradle中，使用cfg.compileSdk、libs.support等变量了。这里的cfg、libs、compileSdk、support都是变量名，名字可以任取。

### 3.1.把各个模块需要依赖的公共库添加到base模块的build.gradle中。

此时，base的build.gradle完整代码如下：


```xml
apply plugin: 'com.android.library'

android {
    compileSdkVersion cfg.compileSdk
    defaultConfig {
        minSdkVersion cfg.minSdk
        targetSdkVersion cfg.targetSdk
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

}

dependencies {
    api fileTree(dir: 'libs', include: ['*.jar'])
    api libs.support
    api libs.constraintLayout
}
```


### 3.2.在first模块和second模块的build.gradle中，添加base模块依赖。

此时，first模块和second模块的build.gradle相同，如下：


```xml
apply plugin: 'com.android.library'

android {
    compileSdkVersion cfg.compileSdk
    defaultConfig {
        minSdkVersion cfg.minSdk
        targetSdkVersion cfg.targetSdk
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation project(':base')
}
```
### 3.3.在app模块的build.gradle中，添加base模块，first模块，second模块的依赖。

此时，app模块下的build.gradle代码如下：


```xml
apply plugin: 'com.android.application'

android {
    compileSdkVersion cfg.compileSdk
    defaultConfig {
        applicationId "com.simple.component"
        minSdkVersion cfg.minSdk
        targetSdkVersion cfg.targetSdk
        versionCode cfg.versionCode
        versionName cfg.versionName
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation project(':base')
    implementation project(':first')
    implementation project(':second')
}
```
注意base中添加依赖时需要使用api关键字，不能使用implementation关键字。因为implementation关键字引入的库是对外不开放的。（对外不开放的意思是指：在其他模块中引入base模块后，无法使用base模块中implementation引入的依赖库；api关键字引入的库是对外开放的。）

## 4.设置模块的启动Activity
要让first模块能够单独运行，需要first模块有一个启动Activity，并且需要将first模块下的build.gradle中的
```xml
apply plugin: 'com.android.library'
```
改为
```xml
apply plugin: 'com.android.application'
```

### 4.1.让first模块单独运行起来
![](https://img-blog.csdn.net/20180605101805552?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

1.先新建了一个Activity，注意这里命名不能和其他模块重复，所以我们带上模块名做区分，命名为FirstMainActivity。然后将FirstMainActivity作为启动Activity，然后将first模块下的build.gradle中的
```xml
apply plugin: 'com.android.library'
```
改为
```xml
apply plugin: 'com.android.application'
```
由于first模块变成了一个单独的可运行模块，所以app模块中不能将他作为库依赖进来了，所以我们先将app模块下的
```xml
implementation project(':first')
```
注释掉。

2.切换到first模块运行：
![](https://img-blog.csdn.net/20180605102938455?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)


### 4.2.指定Activity主题
这时候会遇到崩溃，打开Log日志看到如下信息：
```xml
java.lang.RuntimeException: Unable to start activity ComponentInfo{com.simple.first/com.simple.first.FirstMainActivity}: java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.
```
指的是Activity必须有一个主题，那么我们就来给FirstMainActivity设置一个主题：

![](https://img-blog.csdn.net/20180605104450446?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

我们注意到，app模块下是有一个AppTheme主题的，我们把app模块下的styles文件和AppTheme中使用到的colors文件都移动到base模块下的values文件夹中，然后在first模块的AndroidManifest中，给application标签添加主题：
```xml
android:theme="@style/AppTheme"
```
（也可以只给FirstMainActivity添加主题，在application标签中添加主题会给first模块下所有的Activity都添加这个主题）。这时候再运行first模块就没有问题了。

app会使用默认的图标，app名字为包名：

![](https://img-blog.csdn.net/20180605105257139?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =200x)

### 4.3.自定义模块中application标签的属性
如果要自己设置app的名字和图标等属性，照着app模块下AndroidManifest中的application标签中依样画葫芦即可：


```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:label="@string/app_name"
    android:supportsRtl="true"
    android:theme="@style/AppTheme">
```
注：
1.allowBackup是指此app是否允许备份用户数据，如果不需要的话最好关闭，网上说有开启它有安全风险
2.icon是指应用图标
3.roundIcon是指应用圆形图标
4.label是指应用名称
5.supportsRtl是指是否支持从右到左布局（Rtl是right-to-left的缩写），举个例子：如果在布局中使用了marginLeft属性，AndroidStudio就会提示我们：
![](https://img-blog.csdn.net/20180605110517541?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)
可以看到，AndroidStudio建议我们增加android:layout_marginStart="16dp"，这是由于有的国家是从右到左写字的（比如我们的阿拉伯兄弟），如果supportsRtl设置为true，布局中设置android:layout_marginStart="16dp"，那么对于从右到左写字的国家，布局就会变成从右往左，方便他们使用。

## 5.新增标志位
通过第4点我们已经学会了怎么让模块单独运行，但是我们想要达到的效果是：让一个模块随时可以单独运行，也可以随时切换成总app的一个模块方便打包。接下来，我们通过一个标志位来处理这两种情况。

![](https://img-blog.csdn.net/20180605113736662?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

### 5.1.新建debug文件夹
先在first模块的java文件夹下，新建debug文件夹，将第4步中first模块下的AndroidManifest复制到debug文件夹中，再将first模块的AndroidManifest改回成作为总app的一个模块时的样子。

### 5.2.添加isUniqueApp变量
在项目的gradle.properties中，新增isUniqueApp变量，设置为true。用isUniqueApp来标记功能模块是否作为独立的app。isUniqueApp = true时，表示功能模块作为独立的app运行，isUniqueApp = false时，表示功能模块作为总app的一个模块，不能单独运行。

### 5.3.使用isUniqueApp变量
在first模块的build.gradle中，将：


```xml
apply plugin: 'com.android.application'
```
改为


```xml
if(isUniqueApp.toBoolean()){
    apply plugin: 'com.android.application'
}else{
    apply plugin: 'com.android.library'
}
```
意思是：如果这个模块是作为单独app运行的，那么
```xml
apply plugin: 'com.android.application'
```

如果这个模块是作为总app的一个模块，那么
```xml
apply plugin: 'com.android.library'
```

### 5.4.使用debug资源
在first模块的build.gradle中，添加：


```xml
sourceSets {
    main{
        if(isUniqueApp.toBoolean()){
            manifest.srcFile 'src/main/java/debug/AndroidManifest.xml'
        }else{
            manifest.srcFile 'src/main/AndroidManifest.xml'
            java{
                exclude 'debug/**'
            }
        }
    }
}
```
意思是：如果这个模块是作为单独app运行的，那么manifest资源使用
```xml
src/main/java/debug/AndroidManifest.xml
```

如果这个模块是作为总app的一个模块，那么manifest资源使用
```xml
src/main/AndroidManifest.xml
```
并且排除debug文件夹中的内容

对second模块的操作和first模块一样。

### 5.5.修改app模块对子模块的依赖
将app模块下引入first、second模块依赖的代码改为：


```xml
if(!isUniqueApp.toBoolean()){
    implementation project(':first')
    implementation project(':second')
}
```
意思是：如果first模块和second模块是作为单独app运行的，那么不引入first、second模块；

如果first模块和second模块是作为总app的模块，那么引入first、second模块

## 6.设置统一图标和名称
为了标题和图标的统一，我们为first模块和second模块设置一个应用图标和应用名称：
![](https://img-blog.csdn.net/20180605145700614?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)


先把app模块中和图标有关的图片移动到base模块，以保证每个模块都可以访问这些资源文件。然后给application标签设置label属性和icon属性

这样就完成了项目的组件化。



# 二、ARouter的基本使用
## 简介
ARouter是阿里巴巴开源的路由框架，可以使用ARouter方便的进行Activity隐式跳转。
ARouter的Github地址为：[https://github.com/alibaba/ARouter](https://github.com/alibaba/ARouter)

## 1.引入ARouter

![](https://img-blog.csdn.net/20180605152117861?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

在项目的ext.libs中，添加：

```xml
arouterCompiler     : "com.alibaba:arouter-compiler:1.1.4",
arouterApi          : "com.alibaba:arouter-api:1.3.1"
```
在base模块，添加：

```xml
api libs.arouterApi
```
在first模块和second模块，添加：（需要跳转的每个模块都要添加）


```xml
android {
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ moduleName : project.getName() ]
            }
        }
    }
}

dependencies {
    ...
    annotationProcessor libs.arouterCompiler
}
```
## 2.初始化ARouter

![](https://img-blog.csdn.net/20180605152619989?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)

## 3.实现界面跳转
![](https://img-blog.csdn.net/20180605153213429?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =800x)


### 3.1.添加路径
在FirstMainActivity中添加了路径：
```java
@Route(path = "/first/main")
```
路径需要至少两级，第一级是指分组。

### 3.2.界面跳转
在MainActivity中添加跳转代码：

```java
// 1. 应用内简单的跳转(通过URL跳转在'进阶用法'中)
ARouter.getInstance().build("/first/main").navigation();
```
### 3.3.设置isUniqueApp=false
在gradle.properties中将isUniqueApp设置为false，因为在组件化开发时，各个模块是独立的，只有非组件化模式才能实现跳转到不同的模块。

### 4.携带参数的跳转
如果要跳转时需要带参数，代码为：


```java
// 2. 跳转并携带参数
ARouter.getInstance().build("/first/main")
        .withLong("key1", 666L)
        .withString("key2", "888")
        .withObject("key3", Your_Data)
        .navigation();
```
其中，Your_Data是一个实现了序列化的对象。

运行程序，Log控制台可以看到：

```xml
com.simple.component D/FirstMainActivity: onCreate: 666
com.simple.component D/FirstMainActivity: onCreate: 888
```
以上，就是ARouter的基本使用。

参考文章：组件化在项目中的使用姿势：[https://www.jianshu.com/p/ed845d796710](https://www.jianshu.com/p/ed845d796710)

# 源码已上传

[https://github.com/wkxjc/StudyComponent](https://github.com/wkxjc/StudyComponent)