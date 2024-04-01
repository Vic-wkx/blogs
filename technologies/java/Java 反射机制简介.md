@[TOC]
# 反射简介
Java反射是用来获取Java中任意一个类中的所有方法和属性。

# 一、Java反射的典型例子

## 1.新建一个Student类
```java
public class Student {
    private static final String TAG = "~~~~Student~~~~";
    private String studentName;
    private int studentAge;

    private Student(String studentName){
        this.studentName = studentName;
    }

    private String show(String message){
        Log.d(TAG, "show: " + studentName + "," + studentAge + ","+ message);
        return "abc";
    }
}
```
里面有两个字段，一个带参数的构造方法，一个带参数和返回值的函数，且都是私有的。

## 2.MainActivity中反射获取Student属性及其方法
```java
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "~~~~MainActivity~~~~";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            //1.通过字符串获取Class对象，这个字符串必须带上完整路径名
            Class studentClass = Class.forName("com.sample.testreflex.Student");

            //2.获取声明的构造方法，传入所需参数的类名，如果有多个参数，用','连接即可
            Constructor studentConstructor = studentClass.getDeclaredConstructor(String.class);
            //如果是私有的构造方法，需要调用下面这一行代码使其可使用，公有的构造方法则不需要下面这一行代码
            studentConstructor.setAccessible(true);
            //使用构造方法的newInstance方法创建对象，传入构造方法所需参数，如果有多个参数，用','连接即可
            Object student = studentConstructor.newInstance("NameA");

            //3.获取声明的字段，传入字段名
            Field studentAgeField = studentClass.getDeclaredField("studentAge");
            //如果是私有的字段，需要调用下面这一行代码使其可使用，公有的字段则不需要下面这一行代码
            studentAgeField.setAccessible(true);
            //使用字段的set方法设置字段值，传入此对象以及参数值
            studentAgeField.set(student,10);

            //4.获取声明的函数，传入所需参数的类名，如果有多个参数，用','连接即可
            Method studentShowMethod = studentClass.getDeclaredMethod("show",String.class);
            //如果是私有的函数，需要调用下面这一行代码使其可使用，公有的函数则不需要下面这一行代码
            studentShowMethod.setAccessible(true);
            //使用函数的invoke方法调用此函数，传入此对象以及函数所需参数，如果有多个参数，用','连接即可。函数会返回一个Object对象，使用强制类型转换转成实际类型即可
            Object result = studentShowMethod.invoke(student,"message");
            Log.d(TAG, "result: " + (String) result);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
```
3.运行程序，Log 控制台输出如下：
```xml
/com.sample.testreflex D/~~~~Student~~~~: show: NameA,10,message
/com.sample.testreflex D/~~~~MainActivity~~~~: result: abc
```
以上就是Java反射机制的典型例子。
# 二、拓展
## 1.获取Class对象
为了方便演示，在Student中新增公有的无参构造方法
```java
public Student(){
}
```
获取Class对象的三种方式：
```java
//1.1通过字符串获取Class对象，这个字符串必须带上完整路径名
Class studentClass = Class.forName("com.sample.testreflex.Student");
//1.2通过类的class属性
Class studentClass2 = Student.class;
//1.3通过对象的getClass()函数
Student studentObject = new Student();
Class studentClass3 = studentObject.getClass();
```
第一种就是上面典型例子中的方法，通过字符串获取Class对象，这也是最常用的反射获取Class对象的方法；

第二种有限制条件：需要导入类的包；

第三种已经有了对象，不再需要反射。

通过这三种方式获取到的Class对象是同一个，也就是说Java运行时，每一个类只会生成一个Class对象。

## 2.获取类中所有的构造方法
```java
//2.1获取所有声明的构造方法
Constructor[] declaredConstructorList = studentClass.getDeclaredConstructors();
for(Constructor declaredConstructor:declaredConstructorList){
    Log.d(TAG, "declared Constructor: " + declaredConstructor);
}
//2.2获取所有公有的构造方法
Constructor[] constructorList = studentClass.getConstructors();
for(Constructor constructor:constructorList){
    Log.d(TAG, "constructor: " + constructor);
}
```
运行程序，Log控制台输出如下：
```xml
/com.sample.testreflex D/~~~~MainActivity~~~~: 
declared Constructor: public com.sample.testreflex.Student()
declared Constructor: private com.sample.testreflex.Student(java.lang.String)
declared Constructor: com.sample.testreflex.Student(java.lang.Object[],com.android.tools.ir.runtime.InstantReloadException)
constructor: public com.sample.testreflex.Student()
```
## 3.获取类中所有的字段
```java
//3.1获取所有声明的字段
Field[] declaredFieldList = studentClass.getDeclaredFields();
for(Field declaredField:declaredFieldList){
    Log.d(TAG, "declared Field: " + declaredField);
}
//3.2获取所有公有的字段
Field[] fieldList = studentClass.getFields();
for(Field field:fieldList){
    Log.d(TAG, "field: " + field);
}
```
运行程序，Log控制台输出如下：
```xml
D/~~~~MainActivity~~~~: 
declared Field: private int com.sample.testreflex.Student.studentAge
declared Field: private java.lang.String com.sample.testreflex.Student.studentName
declared Field: public static transient volatile com.android.tools.ir.runtime.IncrementalChange com.sample.testreflex.Student.$change
declared Field: private static final java.lang.String com.sample.testreflex.Student.TAG
declared Field: public static final long com.sample.testreflex.Student.serialVersionUID
field: public static transient volatile com.android.tools.ir.runtime.IncrementalChange com.sample.testreflex.Student.$change
field: public static final long com.sample.testreflex.Student.serialVersionUID
```
## 4.获取类中所有的函数
```java
//4.1获取所有声明的函数
Method[] declaredMethodList = studentClass.getDeclaredMethods();
for(Method declaredMethod:declaredMethodList){
    Log.d(TAG, "declared Method: " + declaredMethod);
}
//4.2获取所有公有的函数
Method[] methodList = studentClass.getMethods();
for(Method method:methodList){
    Log.d(TAG, "method: " + method);
}
```
运行程序，Log控制台输出如下：
```xml
/com.sample.testreflex D/~~~~MainActivity~~~~:
declared Method: public static java.lang.Object com.sample.testreflex.Student.access$super(com.sample.testreflex.Student,java.lang.String,java.lang.Object[])
declared Method: private java.lang.String com.sample.testreflex.Student.show(java.lang.String)
method: public static java.lang.Object com.sample.testreflex.Student.access$super(com.sample.testreflex.Student,java.lang.String,java.lang.Object[])
method: public boolean java.lang.Object.equals(java.lang.Object)
method: public final java.lang.Class java.lang.Object.getClass()
method: public int java.lang.Object.hashCode()
method: public final native void java.lang.Object.notify()
method: public final native void java.lang.Object.notifyAll()
method: public java.lang.String java.lang.Object.toString()
method: public final native void java.lang.Object.wait() throws java.lang.InterruptedException
method: public final void java.lang.Object.wait(long) throws java.lang.InterruptedException
method: public final native void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
```

参考文章：[Java基础之--反射：https://blog.csdn.net/sinat_38259539/article/details/71799078](https://blog.csdn.net/sinat_38259539/article/details/71799078)