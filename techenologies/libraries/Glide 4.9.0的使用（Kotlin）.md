
@[TOC]
# 一、简介
Glide是Google推出的加载图片的框架，支持静态图、动态图、网络图片、本地图片、资源文件等等图片的加载，非常强大。

Github地址：[https://github.com/bumptech/glide](https://github.com/bumptech/glide)
官方文档：[http://bumptech.github.io/glide/](http://bumptech.github.io/glide/)
官方文档中文版：[https://muyangmin.github.io/glide-docs-cn/](https://muyangmin.github.io/glide-docs-cn/)
# 二、下载和设置
## 1.导入Glide库和Glide编译库
build.gradle中，导入库：
```xml
apply plugin: 'kotlin-kapt'

android{
	...
}

dependencies {
	...
	implementation 'com.github.bumptech.glide:glide:4.9.0'
	kapt 'com.github.bumptech.glide:compiler:4.9.0'
}
```
## 2.添加权限
AndroidManifest中，添加权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 ```
## 3.Proguard混淆
proguard-rules.pro中添加以下代码：
```xml
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
```
# 三、Generated API

## 1.创建MyAppGlideModule
### 1.1.作用
创建AppGlideModule的目的是：
(1)全局配置Glide选项
(2)注册自定义组件

注：在Glide4的4.9.0版本以前，创建了MyAppGlideModule才能实现链式调用所有API，Glide4.9.0取消了这一限制。

### 1.2.步骤
新建MyAppGlideModule继承自AppGlideModule，并添加@GlideModule注解：
```kotlin
@GlideModule
class MyAppGlideModule : AppGlideModule()
```
AppGlideModule有两个常用的重载方法：
```kotlin
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    /**
     * 全局配置Glide选项
     */
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        // 例如：全局设置图片格式为RGB_565
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
    }

    /**
     * 注册自定义组件
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
    }
}
```
## 2.GlideExtension
官方提供了@GlideExtension的注解，使用的是静态方法实现拓展Glide的API。但是Kotlin本身提供了扩展方法，所以我们使用Kotlin的扩展即可，例如：
```kotlin
fun RequestOptions.miniThumb(size: Int): RequestOptions {
    return this.fitCenter()
            .override(100)
}
```
设置好之后，就可以直接链式调用此扩展方法：
```kotlin
Glide.with(context)
   .load(url)
   .miniThumb()
   .into(imageView);
```
# 四、占位符
## 1.占位符(Placeholder)
表示加载过程中显示的图片
```kotlin
Glide.with(context)
  .load(url)
  .placeholder(R.drawable.placeholder)
  .into(imageView)
// 或者:
Glide.with(context)
  .load(url)
  .placeholder(new ColorDrawable(Color.BLACK))
  .into(imageView)
```
## 2.错误符(Error)
表示加载错误显示的图片，使用方式与Placeholder类似
```kotlin
Glide.with(context)
  .load(url)
  .error(R.drawable.error)
  .into(imageView)
// 或者:
Glide.with(context)
  .load(url)
  .error(new ColorDrawable(Color.RED))
  .into(imageView)
```
## 3.后备回调符(Fallback)
当需要加载的url或者文件为空时展示的图，如果不设置Fallback，内容为空时默认展示错误符。
```kotlin
Glide.with(context)
  .load(url)
  .fallback(R.drawable.fallback)
  .into(imageView)
//或者:
Glide.with(context)
  .load(url)
  .fallback(new ColorDrawable(Color.GREY))
  .into(imageView)
```
# 五、选项
## 1.RequestOptions
Glide中的大部分设置项都可以通过 RequestOptions 类和 apply() 方法来应用到程序中。
例如：
```kotlin
val options = RequestOptions().centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
Glide.with(context)
        .load(url)
        .apply(options)
        .into(imageView)
```
apply() 方法可以被调用多次，因此 RequestOption 可以被组合使用。如果 RequestOptions 对象之间存在相互冲突的设置，那么只有最后一个被应用的 RequestOptions 会生效。

RequestOptions是为了复用Glide配置而存在的，比如两张图片需要应用同样的变换，没有RequestOptions的话就需要写两次链式调用，有了RequestOptions则只需使用同一个RequestOptions即可。

并且将配置提取成RequestOptions变量之后，在不同的类中可以将其作为参数传递，非常方便。
## 2.Generated API
所有的RequestOptions都方法可以直接链式调用，如上述的RequestOptions代码也可以写成：
```kotlin
Glide.with(context)
        .load(url)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .into(imageView)
```

## 3.过渡选项
TransitionOptions 用于决定你的加载完成时会发生什么，例如加载完成时图片淡入效果：
```kotlin
Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(imageView)
```
## 4.缩略图 (Thumbnail) 请求
Glide 的 thumbnail() API 允许你指定一个 RequestBuilder 以与你的主请求并行启动。thumbnail() 会在主请求加载过程中展示。如果主请求在缩略图请求之前完成，则缩略图请求中的图像将不会被展示。thumbnail() API 对本地和远程图片都适用，例如：
```kotlin
Glide.with(context)
        .load(url)
        .thumbnail(Glide.with(context
                .load(thumbnailUrl))
        .into(imageView)
```
## 5.在失败时开始新的请求
```kotlin
Glide.with(context)
        .load(url)
        .error(Glide.with(context
                .load(errorUrl))
        .into(imageView)
```
# 六.变换
github上有一个比较好用的Glide Transformations库：[https://github.com/wasabeef/glide-transformations](https://github.com/wasabeef/glide-transformations)

具体效果可以在这个库里看到，我们学习一下怎么使用：
## 1.导入Glide Transformations库
```xml
// Glide图片变换库
implementation 'jp.wasabeef:glide-transformations:4.0.1'
```
## 2.使用
```kotlin
Glide.with(context)
        .load(url)
        // 模糊效果
        .transform(BlurTransformation(15))
        .into(imageView)
```
## 3.多重变换
```kotlin
Glide.with(context)
        .load(url)
        // 圆角效果+模糊效果
        .transform(MultiTransformation(RoundedCornersTransformation(15, 5), BlurTransformation(15)))
        .into(imageView)
```
# 七、尺寸
```kotlin
Glide.with(context)
        .load(url)
        // 固定宽高
        .override(100,100)
        .into(imageView)
```
# 八、缓存
默认的策略叫做 AUTOMATIC ，当你加载远程数据时，AUTOMATIC 策略仅会存储原始数据。对于本地数据，AUTOMATIC 策略则会仅存储变换过的缩略图。

这是因为远程图片下载的过程比变换的过程耗时更长，本地的图片变换的过程比加载原图的过程耗时更长。默认的策略是为了在节省内存的同时缩短图片加载的时间。

## 1.一共有五种缓存策略
```xml
DiskCacheStrategy.NONE： 不缓存任何内容
DiskCacheStrategy.RESOURCE： 缓存解码后的图片
DiskCacheStrategy.DATA： 缓存未经解码的数据
DiskCacheStrategy.AUTOMATIC：默认选项
DiskCacheStrategy.ALL ： 对于远程图片，缓存RESOURCE+DATA；对于本地图片，缓存RESOURCE
```
## 2.指定缓存策略
```kotlin
Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
```
# 九、监听器
```kotlin
Glide.with(context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            /**
             * 加载失败
             * @return false 未消费，继续走into(ImageView)
             *         true 已消费，不再继续走into(ImageView)
             */
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }
            /**
             * 加载成功
             * @return false 未消费，继续走into(ImageView)
             *         true 已消费，不再继续走into(ImageView)
             */
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }
        })
        .into(imageView)
```
