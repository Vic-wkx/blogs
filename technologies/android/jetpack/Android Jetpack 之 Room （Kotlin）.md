@[TOC]

# 一、简介

Room 是 Google 官方推出的数据库 ORM 框架。ORM 是指 `Object Relational Mapping`，即对象关系映射，也就是将关系型数据库映射为面向对象的语言。使用 ORM 框架，我们就可以用面向对象的思想操作关系型数据库，不再需要编写 SQL 语句。

# 二、导入

```gradle
apply plugin: 'kotlin-kapt'

dependencies {
    ...
    implementation 'androidx.room:room-runtime:2.2.5'
    kapt 'androidx.room:room-compiler:2.2.5'
}
```

# 三、使用

Room 的使用可以分为三步：

* 创建 Entity 类：也就是实体类，每个实体类都会生成一个对应的表，每个字段都会生成对应的一列。
* 创建 Dao 类：Dao 是指 `Data Access Object`，即数据访问对象，通常我们会在这里封装对数据库的增删改查操作，这样的话，逻辑层就不需要和数据库打交道了，只需要使用 Dao 类即可。
* 创建 Database 类：定义数据库的版本，数据库中包含的表、包含的 Dao 类，以及数据库升级逻辑。

## 3.1 创建 Entity 类

新建一个 User 类，并添加 `@Entity` 注解，使 Room 为此类自动创建一个表。在主键上添加 `@PrimaryKey(autoGenerate = true)` 注解，使得 id 自增，不妨将这里的主键 id 记作固定写法。

```kotlin
@Entity
data class User(var firstName: String, var lastName: String, var age: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
```



## 3.2 创建 Dao 类

创建一个接口类 UserDao，并在此类上添加 `@Dao` 注解。增删改查方法分别添加 `@Insert`、`@Delete`、`@Update`、`@Query` 注解，其中，`@Query` 需要编写 SQL 语句才能实现查询。Room 会自动为我们生成这些数据库操作方法。

```kotlin
@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User): Long

    @Update
    fun updateUser(newUser: User)

    @Query("select * from user")
    fun loadAllUsers(): List<User>

    @Query("select * from User where age > :age")
    fun loadUsersOlderThan(age: Int): List<User>

    @Delete
    fun deleteUser(user: User)

    @Query("delete from User where lastName = :lastName")
    fun deleteUserByLastName(lastName: String): Int
}
```

`@Query` 方法不仅限于查找，还可以编写我们自定义的 SQL 语句，所以可以用它来执行特殊的 SQL 操作，如上例中的 `deleteUserByLastName` 方法所示。

## 3.3 创建 Database 抽象类

新建 AppDatabase  类，继承自 `RoomDatabase` 类，添加 `@Database` 注解，在其中声明版本号，包含的实体类。并在抽象类中声明获取 Dao 类的抽象方法。

```kotlin
@Database(version = 1, entities = [User::class])
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDatabase? = null
        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            return instance?.let { it }
                ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
                    .build()
                    .apply { instance = this }
        }
    }
}
```

在 getDatabase 方法中，第一个参数一定要使用 `applicationContext`，以防止内存泄漏，第三个参数表示数据库的名字。

## 3.4 测试

布局中只有四个 id 为 btnAdd，btnDelete，btnUpdate，btnQuery 的按钮，故不再给出布局代码。

MainActivity 代码如下：

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userDao = AppDatabase.getDatabase(this).userDao()
        val teacher = User("lin", "guo", 66)
        val student = User("alpinist", "wang", 3)
        btnAdd.setOnClickListener {
            thread {
                teacher.id = userDao.insertUser(teacher)
                student.id = userDao.insertUser(student)
            }
        }
        btnDelete.setOnClickListener {
            thread {
                userDao.deleteUser(student)
            }
        }
        btnUpdate.setOnClickListener {
            thread {
                teacher.age = 666
                userDao.updateUser(teacher)
            }
        }
        btnQuery.setOnClickListener {
            thread {
                Log.d("~~~", "${userDao.loadAllUsers()}")
            }
        }
    }
}
```

每一步操作我们都开启了一个新线程来操作，这是由于数据库操作涉及到 IO，所以不推荐在主线程执行。在开发环境中，我们也可以通过 `allowMainThreadQueries()` 方法允许主线程操作数据库，但一定不要在正式环境使用此方法。

```kotlin
Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
 .allowMainThreadQueries()
 .build()
```

点击 btnAdd，再点击 btnQuery，Log 如下：

```shell
~~~: [User(firstName=lin, lastName=guo, age=66), User(firstName=alpinist, lastName=wang, age=3)]
```

点击 btnDelete，再点击 btnQuery，Log 如下：

```shell
~~~: [User(firstName=lin, lastName=guo, age=66)]
```

点击 btnUpdate，再点击 btnQuery，Log 如下：

```shell
~~~: [User(firstName=lin, lastName=guo, age=666)]
```

由此可见，我们的增删改查操作都成功了。

# 四、数据库升级

## 4.1 简单升级

使用 `fallbackToDestructiveMigration()` 可以简单粗暴的升级，也就是直接丢弃旧版本数据库，然后创建最新的数据库

```kotlin
Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
 .fallbackToDestructiveMigration()
 .build()
```

注：此方法过于暴力，开发阶段可使用，不可在正式环境中使用，因为会导致旧版本数据库丢失。

## 4.2 规范升级

### 4.2.1 新增一张表

创建 Entity 类

```kotlin
@Entity
data class Book(var name: String, var pages: Int) {
    
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
```

 创建 Dao 类

```kotlin
@Dao
interface BookDao {
    @Insert
    fun insertBook(book: Book)

    @Query("select * from Book")
    fun loadAllBooks(): List<Book>
}
```

修改 Database 类：

```kotlin
@Database(version = 2, entities = [User::class, Book::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao

    companion object {
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    create table Book (
                    id integer primary key autoincrement not null,
                    name text not null,
                    pages integer not null)
                """.trimIndent()
                )
            }
        }

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            return instance?.let { it }
                ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .apply { instance = this }
        }
    }
}
```

注：这里的修改有：
* version 升级
* 将 Book 类添加到 entities 中
* 新增抽象方法 bookDao
* 创建 `Migration` 对象，并将其添加到 getDatabase 的 builder 中

现在如果再操作数据库，就会新增一张 Book 表了。

### 4.2.2 修改一张表

比如在 Book 中新增 author 字段

```kotlin
@Entity
data class Book(var name: String, var pages: Int, var author: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
```

修改 Database，增加版本 2 到 3 的迁移逻辑：

```kotlin
@Database(version = 3, entities = [User::class, Book::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao

    companion object {
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    create table Book (
                    id integer primary key autoincrement not null,
                    name text not null,
                    pages integer not null)
                """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    alter table Book add column author text not null default "unknown"
                """.trimIndent()
                )
            }
        }

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            return instance?.let { it }
                ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .apply { instance = this }
        }
    }
}
```

注：这里的修改有：
* version 升级
* 创建 `Migration` 对象，并将其添加到 getDatabase 的 builder 中

## 4.3 测试

修改 MainActivity：

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bookDao = AppDatabase.getDatabase(this).bookDao()
        btnAdd.setOnClickListener {
            thread {
                bookDao.insertBook(Book("第一行代码", 666, "guolin"))
            }
        }
        btnQuery.setOnClickListener {
            thread {
                Log.d("~~~", "${bookDao.loadAllBooks()}")
            }
        }
    }
}
```

点击 btnAdd，再点击 btnQuery，Log 如下：

```shell
~~~: [Book(name=第一行代码, pages=666, author=guolin)]
```

这就说明我们对数据库的两次升级都成功了。

# 参考文章

[《第一行代码》（第三版）- 第 13 章 13.5 Room](https://blog.csdn.net/guolin_blog/article/details/105233078)



