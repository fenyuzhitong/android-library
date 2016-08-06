ActiveAndroid 使用教程
===============

### 简介
---

使用 ActiveAndroid 可以帮助我们更方便地操作 SQLite ，由于 ActiveAndroid 本身已经非常简单，在 Library 中我们暂时就不再做封装了。
在这篇文章里有 ActiveAndroid 使用的详细介绍和可能出现的问题的注意事项。

### 下载ActiveAndroid工程文件
---
下载地址为 [https://github.com/pardom/ActiveAndroid](https://github.com/pardom/ActiveAndroid)

### 设置环境
---

##### 步骤 1
将下载下来的整过工程文件作为Module导入进来

##### 步骤 2

在 AndroidMenifest.xml 文件中为 Application 添加属性
```
        <meta-data
            android:name="AA_DB_NAME"
            android:value="database.db" />
        <meta-data
            android:name="AA_DB_VERSION"
            android:value="5" />
```

##### 步骤 3

调整工程文件的build.gradle文件的下面这句话：
>classpath 'com.android.tools.build:gradle:1.5.0'
应该是这个ActiveAndroid是比较老的框架了，如果gradle版本是2.0.0是不行的，1.5.0还可以

##### 步骤 4

在 AndroidMenifest.xml 为 Application 添加 android:name="com.activeandroid.app.Application" 属性

##### 注意事项

关于第二步，如果要使用自己的 Application 类，那么这个类继承 com.activeandroid.app.Application 就好了
如果实在要继承其他的 Application 类，可以这样写以达到初始化目的，如：

```
    public class MyApplication extends ***Application {
        @Override
        public void onCreate() {
            super.onCreate();
            ActiveAndroid.initialize(this);
        }
        @Override
        public void onTerminate() {
            super.onTerminate();
            ActiveAndroid.dispose();
        }
    }
```

##### 完成
环境建立完成，我们就可以开始操作数据库了。

### 建表
----

##### 代码及说明
按照如下方法创建相应的类，就相当于告诉 ActiveAndroid 创建相应的表，例子如下：
```
        import com.activeandroid.Model;
        import com.activeandroid.annotation.Column;
        import com.activeandroid.annotation.Table;

        @Table(name = "music")
        public class Music extends Model {

            @Column(name = "path")
            private String path;

            public void setPath(String path) {
                this.path = path;
            }

            public String getPath() {
                return path;
            }
        }
```

>@Table(name = "music") 这行中的 music 代表表名

>@Column(name = "path") 这行中的 path 代表这个 music 表中有一个字段 path

##### 注意事项
ActiveAndroid 为所有的表都会创建一些系统需要的字段，特别是id，如果需要类似字段，我们建议字段名不要取名太简单。
如果新版本应用比旧版本多了新的表，安装新版本之后可能会出问题，请今后的开发者注意，可能需要清除一些记录

### 操作
------

##### 添加记录

以上面的 music 表为例，增加记录方法如下：
```
Music music = new Music();
music.setPath("storage/quseit.mp3");
music.save();
```

##### 删除记录

```
new Delete().from(Music.class).where("path = ?", "storage/quseit.mp3").execute();//删除 path = storage/quseit.mp3 的记录
new Delete().from(Music.class).execute();//删除所有记录
```

##### 查询记录

```
    public static List<Music> getAll() {
        return new Select()
                .from(Music.class)
                .where("path = ?", "music5.mp3")
                .execute();
    }
```

### 更多操作待今后开发者补充