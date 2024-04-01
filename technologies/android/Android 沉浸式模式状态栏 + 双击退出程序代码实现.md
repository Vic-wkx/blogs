@[TOC](目录)
# 一、Android沉浸式模式状态栏
## 效果图
![](https://img-blog.csdn.net/20180328175326658?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)
## 代码实现
将以下代码加入相应的Activity中：
```java
//沉浸式模式状态栏
@Override
public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus && Build.VERSION.SDK_INT >= 19) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
```



# 二、Android双击退出程序

## 效果图
![](https://img-blog.csdn.net/20180329102622536?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FscGluaXN0V2FuZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70 =350x)

## 代码实现

将以下代码加入相应的Activity中：
```java
//双击退出程序
private long firstTime=0;
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
        if (System.currentTimeMillis() - firstTime > 2000){
            Toast.makeText(MainActivity.this,"Press again to exit the program.",Toast.LENGTH_SHORT).show();
            firstTime=System.currentTimeMillis();
        }else{
            finish();
            System.exit(0);
        }
        return true;
    }
    return super.onKeyDown(keyCode, event);
}
```

参考文章：[Android状态栏微技巧，带你真正理解沉浸式模式：https://blog.csdn.net/guolin_blog/article/details/51763825](https://blog.csdn.net/guolin_blog/article/details/51763825)