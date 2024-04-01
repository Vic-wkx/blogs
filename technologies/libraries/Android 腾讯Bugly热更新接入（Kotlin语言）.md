@[TOC]

# 简介
Bugly热更新是腾讯推出的热更新框架，热更新是指无需到应用市场重新下载安装app，只需要在app内下载补丁包即可实现app的更新，主要用于app的bug修复或者少量改动。

大家在使用app（特别是游戏app比较常见）的时候应该都有过类似经历：打开app时，一个弹框显示：有新的更新包，点击下载，只需要下载几百KB或几兆的补丁包，app就实现了更新，这就是使用的热更新技术。

Bugly热更新官方接入文档：[https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix/?v=20180709165613](https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix/?v=20180709165613)
# 一、添加插件依赖
工程根目录下“build.gradle”文件中添加：
```kotlin
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.tencent.bugly:tinker-support:1.1.5"
    }
}
```
# 二、gradle配置


先生成一个jks密钥，然后在app module的“build.gradle”文件中添加（示例配置）：
```kotlin
 android {
       defaultConfig {
        ndk {
            abiFilters 'armeabi'
        }
        signingConfigs {
            release {
                storeFile file(jks密钥库路径)
                storePassword 密钥库密码
                keyAlias 密钥名
                keyPassword 密钥密码
            }
        }
        buildTypes {
            release {
            	//应用签名信息
                signingConfig signingConfigs.release
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }
       }
     }
dependencies {
 	implementation 'com.android.support:design:27.1.1'
	implementation 'com.android.support:multidex:1.0.3'
	implementation 'com.tencent.bugly:crashreport_upgrade:1.3.5'
	implementation 'com.tencent.tinker:tinker-android-lib:1.9.6'
	implementation 'com.tencent.bugly:nativecrashreport:2.2.0'
}
 ```
# 三、新建tinker-support.gradle
在app module下新建tinker-support.gradle，内容如下：
```kotlin
apply plugin: 'com.tencent.bugly.tinker-support'

def bakPath = file("${buildDir}/bakApk/")

/**
 * 此处填写每次构建生成的基准包目录
 */
def baseApkDir = "app-1115-15-33-36"

tinkerSupport {
    /**构建基准包和补丁包都要指定不同的tinkerId，并且必须保证唯一性 base/patch*/
    tinkerId = "base-1.0.0"
    autoBackupApkDir = "${bakPath}"
    overrideTinkerPatchConfiguration = true
    baseApk = "${bakPath}/${baseApkDir}/app-release.apk"
    baseApkProguardMapping = "${bakPath}/${baseApkDir}/app-release-mapping.txt"
    baseApkResourceMapping = "${bakPath}/${baseApkDir}/app-release-R.txt"
    enableProxyApplication = true
    supportHotplugComponent = true
}

/**
 * 一般来说,我们无需对下面的参数做任何的修改
 * 对于各参数的详细介绍请参考:
 * https://github.com/Tencent/tinker/wiki/Tinker-%E6%8E%A5%E5%85%A5%E6%8C%87%E5%8D%97
 */
tinkerPatch {
    ignoreWarning = false
    useSign = true
    dex {
        dexMode = "jar"
        pattern = ["classes*.dex"]
        loader = []
    }
    lib {
        pattern = ["lib/*/*.so"]
    }

    res {
        pattern = ["res/*", "r/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]
        ignoreChange = []
        largeModSize = 100
    }

    packageConfig {
    }
    sevenZip {
        zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
    }
    buildConfig {
        keepDexApply = false
    }
}

```
建立完成后，在app module中的build.gradle文件中添加：
```kotlin
apply from: 'tinker-support.gradle'
```
# 四、初始化SDK
先在[Bugly管理平台：https://bugly.qq.com/v2/](https://bugly.qq.com/v2/)上申请appId，注册账户后登录，点击新建产品：
![](https://img-blog.csdnimg.cn/20181119105024991.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
然后在我的产品-->设置中可以看到appId
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119105324912.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119105426918.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)

新建BaseApplication：
```kotlin
@Suppress("unused")
class BaseApplication : Application(), BetaPatchListener {

    override fun onCreate() {
        super.onCreate()
        /**Bugly管理平台：https://bugly.qq.com/v2/ */
        Bugly.init(this, Bugly管理平台申请的appId, false)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
        /**安装tinker*/
        Beta.installTinker()
        /**设置监听器，补丁包应用成功后杀进程，重启app*/
        Beta.betaPatchListener = this
    }

    /**Bugly BetaPatchListener*/
    override fun onApplySuccess(p0: String?) {
        /**补丁包应用成功回调，在这里杀进程，重启app，完成热更新。
        否则需要等待用户下次自己主动杀进程重启后才能完成更新*/
        restartApp()
    }

    override fun onPatchReceived(p0: String?) {
    }

    override fun onApplyFailure(p0: String?) {
    }

    override fun onDownloadReceived(p0: Long, p1: Long) {
    }

    override fun onDownloadSuccess(p0: String?) {
    }

    override fun onDownloadFailure(p0: String?) {
    }

    override fun onPatchRollback() {
    }

    /**
     * 杀进程，重启app
     */
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
```
这里笔者设置了热更新完成后自动杀进程，重启app，这样的目的是让用户立即用上热更新后的版本。

这里只是为了演示，突然地重启给用户的体验不是很好，如果不必要的话可以去掉这段代码，那么程序就会在用户第二次打开时应用热更新的内容。

或者可以在热更新完成后显示弹窗，提示用户重启，实际开发时应该视情况而定。
# 五、AndroidManifest.xml配置
在AndroidMainfest.xml中进行以下配置：

1. 权限配置
```kotlin
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```


2. Activity配置
```kotlin
<activity
    android:name="com.tencent.bugly.beta.ui.BetaActivity"
    android:configChanges="keyboardHidden|orientation|screenSize|locale"
    android:theme="@android:style/Theme.Translucent" />
```
3. 配置FileProvider
先在res目录新建xml文件夹，创建provider_paths.xml文件如下：

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="beta_external_path"
        path="Download/" />
    <external-path
        name="beta_external_files_path"
        path="Android/data/" />
</paths>
```
在AndroidManifest中配置FileProvider：
```kotlin
 <provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
</provider>
```

# 六、混淆配置
如果开启了混淆，在proguard-rules.pro文件中添加以下混淆规则：
```kotlin
# bugly混淆规则
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }
# v4包混淆规则
-keep class android.support.**{*;}
```

# 七、生成基准包
我们对基准包和补丁包做一个简单的测试，在基准包的MainActivity中显示一个TextView，文字为“基准包”，补丁包中将其显示文字修改为“补丁包”。
编辑activity_main：
```kotlin
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <TextView
        android:id="@+id/tv_test"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</android.support.constraint.ConstraintLayout>
```
编辑MainActivity：
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_test.text = "基准包"
    }
}
```
运行app，显示如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119110335125.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
运行成功后，在[Bugly管理后台](https://bugly.qq.com/v2/)的我的产品-->版本管理界面可以看到如下显示：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119110625764.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
生成基准包：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119112039529.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
点击图示位置的assembleRelease，生成基准包，完成后在下图位置可以看到app_release.apk
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119113044976.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
在手机联网状态下，安装此基准包，否则上传补丁包时会报错："<font color=#FF0000>未匹配到可应用补丁包的App版本，请确认补丁包的基线版本是否配置正确</font>"
# 八、生成补丁包
修改MainActivity中的代码：
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_test.text = "补丁包"
    }
}
```
生成补丁包：
1.先将tinker-support.gradle中的baseApkDir修改为第七步生成的apk文件夹名
2.再将tinkerId修改为patch-1.0.0
3.然后点击下图位置的buildTinkerPatchRelease生成补丁包
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119113337227.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
生成的补丁包在这里：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119140719613.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
这里注意路径不要找错了，是patch/release文件夹下的patch_signed_7zip.apk，而不是apk/tinkerPatch/release文件夹下的patch_signed_7zip.apk，如果上传apk文件夹下的补丁包，Bugly管理平台会报错：“<font color =#FF0000 >上传失败！补丁文件缺失必需字段：Created-Time、Created-By、YaPatchType、VersionName、VersionCode、From、To，请检查补丁文件后重试！</font>”
# 九、上传补丁包，见证热更新
在[Bugly管理后台](https://bugly.qq.com/v2/)的 应用升级 --> 热更新 中，点击发布新补丁：
![在这里插入图片描述](https://img-blog.csdnimg.cn/201811191139173.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
上传patch/release文件夹下的patch_signed_7zip.apk：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119142242330.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
上传成功后，显示如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119142336414.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70)
然后我们静静等待热更新生效，生效时间大概十分钟，生效之后显示如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181119150254454.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==,size_16,color_FFFFFF,t_70 =300x)
以上，就是Bugly热更新的接入。

源码已上传：
[https://github.com/wkxjc/TestBugly](https://github.com/wkxjc/TestBugly)