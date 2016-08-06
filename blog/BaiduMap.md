百度地图SDK
==========
### 介绍
---
在这片文章中，作者将介绍百度地图Android SDK的简单使用，作者是重头开始制作一个显示百度地图的app的同时来写这篇文章，应该还是比较详细的。
### 下载开发包
---
下载地址为 [http://lbsyun.baidu.com/sdk/download](http://lbsyun.baidu.com/sdk/download)

网站会让你勾选一些功能，根据你的需求给你下载开发包，建议一定要勾选完你所需要的所有功能，否则后期再添加功能可能会出问题。
### 环境设置
---
##### 导入开发包
* jar文件放入 工程文件夹/app/libs/ 文件夹下
* 其他的各种包含 so 文件的文件夹复制到 工程文件夹/app/src/main/jniLibs/ 文件夹下
* assets文件夹放在 工程文件夹/app/src/main/ 文件夹下

##### 获取密钥
获取密钥教程很简单，就是注册一个账号。但是在注册过程中它需要我们应用的SHA1值，所以在这一步我们主要介绍如何获取应用SHA1.

在 AndroidStudio 的自带命令行中执行下面这行命令即可。

>F:/jdk/bin/keytool -list -keystore G:\Project\Android\QUSEIT\BaiduApplication\baidu.jks

根据自己的情况改变命令最左边的keytool应用路径和最右边的签名包路径就好。

值得注意的是，在debug的时候，应用的sha1值并不是我们提交的sha1值，所以在测试时是不会显示地图的，Log会提示验证码错误。一个解决的方法是在百度账号中设置开发版的sha1，不过作者所知道的获取方法较复杂，所以我使用的是另一种方法，即每一次测试都用签名包签名。要做到这个，只需要在app的 build.gradle 文件中进行设置。下面给出一个完整的 build.gradle 内容供参考:
```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.2"

    defaultConfig {
        applicationId "com.quseit.baiduapplication"
        minSdkVersion 16
        targetSdkVersion 23
        versionCode 1
        versionName "1.0"
    }
    signingConfigs {
        debug {
            storeFile file("G:/Project/Android/QUSEIT/BaiduApplication/baiduTest.jks")
            storePassword "******"
            keyAlias "******"
            keyPassword "******"
        }
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.2.0'
    compile files('libs/BaiduLBS_Android.jar')
}
```

其实就是要添加 signingConfigs 的内容

##### 在代码中添加密钥信息
在 manifest 文件中为 Application 添加密钥信息
```
        <meta-data
            android:name="com.baidu.lbsapi.API_KEY"
            android:value="RhcohAatyqHMSVcnf2aq4DWX1AIUULqX" />
```

##### 设置权限
在 manifest 添加权限，权限可以根据情况添加，代码及说明如下：
```
    <!-- 下面的权限是基本的权限 -->
    <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.GET_TASKS" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <!-- 这个权限用于进行网络定位-->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <!-- 这个权限用于访问GPS定位-->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <!-- 用于访问wifi网络信息，wifi信息会用于进行网络定位-->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <!-- 获取运营商信息，用于支持提供运营商信息相关的接口-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <!-- 这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
    <!-- 用于读取手机当前的状态-->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <!-- 访问网络，网络定位需要上网-->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- SD卡读取权限，用户写入离线定位数据-->
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
```
### 创建一个显示地图的Activity
---
##### Activity 布局文件
Activity 的布局文件中添加百度地图部件 MapView，完整xml文件内容如下：
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.baidu.mapapi.map.MapView
        android:id="@+id/bmapView"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:clickable="true" />

</RelativeLayout>
```
##### Activity java文件
下面给出最基础的代码，下面的代码即可显示出地图，其原理就是在Activity的生命周期中做初始化和释放资源的工作。
```
public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MapView) findViewById(R.id.bmapView)).onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MapView) findViewById(R.id.bmapView)).onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((MapView) findViewById(R.id.bmapView)).onPause();
    }

}
```
上述Activity的运行结果就是显示出地图，并默认定位在伟大的北京天安门。

### 各种功能使用
---
能显示地图之后，我们希望根据需要完成一些功能，下面会一一介绍一些基础的功能及实现方法。

##### 设置屏幕中心坐标
刚刚创建的地图Activity是定位在天安门的，如果我们想让屏幕中心定位到某一点，可以调用下面这个作者写的函数
```
    private void local(double v, double v1) {
        BaiduMap mBaidumap = ((MapView) findViewById(R.id.bmapView)).getMap();
        LatLng cenpt = new LatLng(v,v1);
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaidumap.setMapStatus(mMapStatusUpdate);
    }
```
>参数v，v1相当与坐标，当然我们需要知道地点的位置信息。其实我们很少会设置屏幕中心，一般只会在刚开始将屏幕中心定位到当前位置。

##### 获取当前位置
由于当前的位置是可能变化的，所以我们会每隔一段时间就获取一次当前位置，步骤如下：
>为MapActivity添加两个成员变量。其中LocationClient将为我们获取当前的位置信息，当获得当前位置之后，它会通知BDLocationListener

```
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(MapActivity.this, "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(MapActivity.this, "网络不同导致定位失败，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(MapActivity.this, "" + location.getLocType() + "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机", Toast.LENGTH_SHORT).show();
            }
            //参数location是当前的位置信息，如果，我们想获得坐标，可以这样写：
            double v = location.getLatitude(), v1 = location.getLongitude();
        }
    };
```

>初始化LocationClient并开始定位，代码如下：

```
    private void initLocation(){
        BaiduMap mBaiduMap = ((MapView) findViewById(R.id.bmapView)).getMap();
        mBaiduMap.setMyLocationEnabled(true);// 开启定位图层
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
```
##### 添加标注
如果我们使用过百度地图都应该在地图上见过一些小箭头用于标注某些特定地点如目的地等。当我们知道了了坐标信息如何将它标注出来呢，下面作者写了两个函数，分别都可以用于在地图上添加标注,并返回这个标注对象。
```
    private Marker createBaiduMarker(double v, double v1) {
        LatLng point = new LatLng(v, v1);
        //构建Marker图标，这里是为每个标注都创建一个位图，建议修改一下，将位图变为Activity的成员或静态变量，比较节约资源
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        return (Marker)((MapView) findViewById(R.id.bmapView)).getMap().addOverlay(option);
    }
    
    private Marker createBaiduMarker(LatLng point) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        return (Marker)((MapView) findViewById(R.id.bmapView)).getMap().addOverlay(option);
    }
```
我们还可以用下面这个方法为地图上的标记设置点击事件：
```
        BaiduMap mBaidumap = ((MapView) findViewById(R.id.bmapView)).getMap();
        mBaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(MapActivity.this, "click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
```
##### 查询兴趣点POI
如果我们想查找附近或一个城市的饭店或加油站，我们要如何做，下面作者简单编写了一个搜索南宁所有的加油站的位置。我们只用在需要查找时调用这个函数就好。
```
    public void searchPOI() {
        PoiSearch mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
            public void onGetPoiResult(PoiResult result){
                //获取POI检索结果
                if (result == null || result.getAllPoi() == null) return;
                int i = 0;
                int size = result.getAllPoi().size();
                while (i < size) {
                    //这里为每个加油站标注
                    createBaiduMarker(result.getAllPoi().get(i).location);
                    i++;
                }
            }
            public void onGetPoiDetailResult(PoiDetailResult result){
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchInCity(new PoiCitySearchOption().city("南宁").keyword("加油站"));
    }
```
>注意：搜索函数不能写在 Activity 的 onCreate 函数里，这样是搜索不出来的。可能是百度地图没有初始化的原因吧。

### 更多功能，有待今后的开发人员添加
---