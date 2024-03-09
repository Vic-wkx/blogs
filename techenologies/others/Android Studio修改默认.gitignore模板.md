Android Studio 创建新项目时，默认有.gitignore文件，但是默认的模板缺少一些应该忽略的内容，如.idea*、.DS_Store等等。每次创建新项目后都去修改它又费时费力，那么我们就来修改这个默认的模板，仅需一步：

对于mac电脑，到达文件夹：
```
/Applications/Android\ Studio.app/Contents/plugins/android/lib/templates/gradle-projects/NewAndroidProject/root

```
对于windows电脑，到达文件夹：
```
你的Android Studio安装目录/plugins/android/lib/templates/gradle-projects/NewAndroidProject/root

```

修改project_ignore文件为如下内容：
```kotlin
*.iml
.gradle
.idea*
/.idea
gradlew
gradlew.bat

/local.properties
.DS_Store
/build
/captures
.externalNativeBuild

*apk
*build
.local.properties
```
然后保存，重启Android Studio即可。以后创建新项目时，.gitignore文件都会是这个新模板了。

注：
建议在修改前将project_ignore文件做个备份，在升级Android Studio的时候，需要将这个文件还原，否则会无法升级，并报如下错误：
```
Android Studio Update: Some conflicts found in installation area

/Contents/plugins/android/lib/templates/gradle-projects/NewAndroidProject/root/project_ignore

```
如果没有备份也不用担心，拷贝我这一份就可以了：
```xml
*.iml
.gradle
/local.properties
/.idea/caches/build_file_checksums.ser
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
.DS_Store
/build
/captures
.externalNativeBuild
```