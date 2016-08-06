ORMLite-android 使用方法
====================

### 介绍
---

ORMLite-android 是 Android SQLite 的 ORM 框架，这篇文章将详细介绍 ORMLite-android 的使用方法。

### 下载ORMLit jar
---

要使用这个框架需要两个包，分别是 ormlite-core.jar 和 ormlite-android.jar 包。

下载地址为 [http://ormlite.com/releases/](http://ormlite.com/releases/)

网站可能会提供各种版本的包，下载较新的版本就好了。

### 创建 Bean 类
---

这个步骤的作用实际上就是告知 ORMLite-android 我们要创建什么样的表。
下面举一个例子：

```
package com.quseit.librarytest;
 
 import com.j256.ormlite.field.DatabaseField;
 import com.j256.ormlite.table.DatabaseTable;
 
 /**
  *
  * Created by zhimeng on 2016/5/17.
  */
 @DatabaseTable(tableName = "music")
 public class Music {
 
     @DatabaseField(generatedId = true)
     public int id;
 
     @DatabaseField(columnName = "name")
     public String name;
 
 }
```

这样类似的ORM框架都把数据表中的每一行数据当作一个对象来处理。
我们创建了这个Music类，代表我们希望创建一个相应的表用来存储多个Music类的信息。


>@DatabaseTable(tableName = "music")//这行代表这个表的表名为music
>@DatabaseField(generatedId = true)//表示id为主键且自动生成
>@DatabaseField(columnName = "name")//表示该表拥有一个叫 name 的字段


### 编写 DAO 类
---

DAO 类用于操作数据库，我们需要为不同的Bean类创建DAO类。

如果我们希望操作上面我们创建的 music 表，那我们可以为之创建一个 DAO 类，名字叫MusicDatabaseHelper，代码及说明如下：

```
package com.quseit.ormliteapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * 
 * Created by zhimeng on 2016/5/31.
 */
public class MusicModel extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "sqlite-test.db";

    private static MusicModel model;

    private Dao<Music, Integer> userDao;

    public static MusicModel newInstance(Context context) {
        if (model != null) return model;
        model = new MusicModel(context);
        return model;
    }

    private MusicModel(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Music.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Music.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Music, Integer> getDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(Music.class);
        }
        return userDao;
    }

    @Override
    public void close() {
        super.close();
        userDao = null;
    }
}
```

下面解释类成员和方法的意思：

  * private static final String TABLE_NAME 
    代表操作所在的数据库。
  * private Dao<Music, Integer> musicDao 
    这个就是所谓的DAO对象了。
  * public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) 
    需要重写的方法，写法可以固定像上面那样写，只要把Music.class替换成你创建的Bean类就好。
  * public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
    需要重写的方法，写法可以固定像上面那样写，只要把Music.class替换成你创建的Bean类就好。
  * public Dao<Music, Integer> getDao() 
    获取相应的 DAO 类。
  * public void close() 
    不再使用该 DatabaseHelper ，我们需要释放相关资源。
    

### 使用 DAO 类
---

当我们创建好后如何使用它操作数据库呢

##### 添加记录
>添加记录代码如下

```
        Music music = new Music();
        music.name = "sound.mp3";
        try {
            helper.getUserDao().create(music);
        } catch (SQLException e) {
            e.printStackTrace();
        }
```

##### 查询记录
>查询记录代码如下：

```
        try {
            return DatabaseHelper.getHelper(this).getDao().queryForAll();//返回表中所有记录，返回数据类型为 List<Music>
            return DatabaseHelper.getHelper(this).getDao().queryForEq("name", "sound.mp3");//返回表中name = sound.mp3的记录，返回数据类型为 List<Music>
        } catch (SQLException e) {
            e.printStackTrace();
        }
```

##### 删除记录
>删除记录代码如下：

```
    public void delete(List<Music> musics) {
        try {
            DatabaseHelper.getHelper(this).getDao().delete(musics); //我们可以将上面查询的结果作为此方法的参数来删除相关信记录
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
```