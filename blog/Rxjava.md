RxJava 教程
=========

### RxJava 简单介绍
-----------
RxJava 就是一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库。
简单来说，我们可以使用它精简我们的异步操作的代码，不用再写Handler 和 Message，还可以减少很多迷之缩进，你懂的。

RxJava最核心的两个东西是Observables（被观察者）和Subscribers（观察者）。
具体的举个例子：
假设我们写了一个用来网络登陆的类 
> ***LoginHelper

调用它的登陆方法后可以从callback中获得登陆情况
> loginInBackground(String username, String password, LoginCallback callback); 

其中，这个LoginHelper就是所谓的被观察者，callback就相当于观察者。


### 引入依赖
---
在build.gradle中加入依赖
> compile 'io.reactivex:rxjava:1.0.14'
> compile 'io.reactivex:rxandroid:1.0.1'

同步后即可，记得翻墙，1.0.14 是版本号，你可以使用最新版的，
rxjava代码的github地址为 [https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava) 
rxandroid代码的github地址为 [https://github.com/ReactiveX/RxAndroid ](https://github.com/ReactiveX/RxAndroid )

### 最简单的RxJava使用
---
下面作者通过演示一个非常简单的场景来展示RxJava是如何完成异步操作的。
假设我们需要这样一个功能，读取一张大图片，读取完成后让ImageView显示它，读取图片是耗时操作。

##### 创建观察者
```
        Subscriber<Bitmap> imageView = new Subscriber<Bitmap>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(Bitmap s) {
                view.setImageBitmap(s);
            }
        };
```
不难看出，代码中的 Bitmap 表示了这个观察者很任性，它只在意被观察者发出的Bitmap信息
下面解释一下这个Subscriber接口的几个函数

 * onCompleted()，有的被观察者发出了所有信息后便会执行这条函数，告诉观察者我不会再发信息给你了，不要再看我了。
 * onError(Throwable e) 有的被观察者在内部出现错误时此函数会被执行
 * onNext(Bitmap s) 接收到位图信息，当被观察者读取完图片后调用
 
上述代码所描述的观察者会在获得位图信息后将图片显示出来。

##### 创建被观察者
```
Observable bigImage = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {

                Bitmap bitmap;
                bitmap = readBitmap();// 耗时的图片读取操作，readBitmap()自己写
                subscriber.onNext(bitmap);//传递图片给监听者

                subscriber.onCompleted();//告诉监听者，我读取完成了，忘了我吧
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
```
解释一下上述代码，上述代码是创建了一个Observable（被观察者）对象。
我们需要为这个被观察者实现OnSubscribe接口，这个接口就一个方法call，当被观察者被观察时会执行此函数。
subscribeOn函数指示了被观察者的动作（这里是读取图片的动作）是在新线程执行的
observeOn函数指示了观察者的动作是在主线程执行的
参数subscriber就是观察者，观察者要看你就是为了看到一张图片，所以每当有一个观察者看你的时候都要执行subscriber.onNext(bitmap);传递图片给观察者
上述代码差不多就是这个意思。

##### 在需要的时候，让 Subscriber 观察 Observable，实现我们的业务
比如我们希望用户点击了某个按钮，ImageView开始显示图片
```
        findViewById(R.id.img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigImage.subscribe(imageView);//派观察者imageView去看被观察者bigImage
            }
        });
```
根据我们前两步的定义，当 bigImage 被观察后会给出一个位图信息，imageView得到位图信息后会将其显示出来，就是这么简单。

##### 额外的
你们可能会发现，观察者并不能传递什么参数给被观察者（或者是作者不知道这个功能）。不过不能传递参数也有它的道理。
毕竟从常识来说，被观察者一般情况下是不会变的，它应该是一个静态的东西，就如不同的人去看一个苹果，都只看能到苹果，结果不会因人而异。
虽然被观察者不能接受参数，但是我们在外部却是可以直接创建不同的被观察者。到时候我们就根据需要创建不同的 Observable 让 Subscriber去观察就行了。

### 变换
---
接下来介绍一下RxJava的一个重要功能，这个功能的最大的作用是简化代码。
在异步操作中我们有时需要将异步操作得到的数据n1做为搜索条件再次进行异步操作得到n2，又再把n2当作条件异步地去获得n3。。。这样，我们会有太多的嵌套的大括号。
而RxJava也为我们设计了一些方法来优化代码的结构，变换就是有这个重要的功能。
下面我们介绍一些变换的使用方法
假设我们有这样一种需求：我们将所有手机中的图片的缩略图在列表中显示，我们可以按下面的步骤做

##### 创建观察者
```
        Subscriber<Bitmap> imageList = new Subscriber<Bitmap>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onNext(Bitmap s) {
                //TODO:将缩略图加入列表显示
            }
        };
```

##### 创建手机所有图片路径的 Observable， 通过观察它可以获得所有的手机中的图片路径
```
    Observable imagePath = Observable.create(new Observable.OnSubscribe<String>() {
        @Override
        public void call(final Subscriber<? super String> subscriber) {
            int i = 10;//模拟数据,一共会搜到10张图片
            while (i > 0) {
                try {
                    Thread.sleep(1000);//模拟延时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext("" + i + ".jpg");//会调用10次onNext
                i--;
            }
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.newThread());//设置这个 Observable 的动作在新线程执行
```

##### 创建手机所有图片的 Observable，通过观察它，可以获得所有手机中的图片
```
    Observable image = imagePath.map(new Func1<String, Bitmap>() {
        @Override
        public Bitmap call(String path) {
            return loadThumbnail(path);//这个函数自己写的
        }
    }).subscribeOn(Schedulers.newThread());//设置这个 Observable 的动作在新线程执行
```
这段代码非常简洁是因为复用了获取手机所有图片路径的 Observable，把获取手机所有图片路径的 Observable 做了一个小小的转化。
在这里，func1就是我们要自己实现的转换的步骤,泛型<String, Bitmap> 代表从String 转 Bitmap

##### 最后一步
```
image.observeOn(AndroidSchedulers.mainThread()).subscribe(imageList);//observeOn指示观察者的动作在主线程执行
```

不知道你现在对RxJava的变换有没有一些理解，其实RxJava还提供了一些变换方法，作者只打算在文章最后的进阶小知识部分再做介绍。
目前你最重要是要知道变换最大的作用就是优化代码的结构和可读性。

### 线程控制：Scheduler
---
相信你们在上面的代码中已经发现很多 Scheduler 的出现。我们即便不详细说，我相信大家也能使用。
不过我这里还是简单介绍一下。Scheduler是用来控制观察者和被观察者的代码要在什么样的线程运行。

 * subscribeOn(Scheduler) 方法是 Observable 这个类的方法，它会改变它的 Observable 动作的执行所在的线程并创建一个新的 Observable 返回
    如：Observable a = ...; Observable b = a.subscribeOn(Schedulers.newThread()); 
    其中b的call程序段是在新线程执行的，但a != b
    
 *  observeOn(Scheduler)方法也是 Observable 这个类的方法，它会改变它的 Observable 的观察者的动作的执行所在的线程并创建一个新的 Observable 返回
     如之前的这句：image.observeOn(AndroidSchedulers.mainThread()).subscribe(imageList);
     我们预先设置了imageList 的处理方法是在主线程执行的
     同样的Observable a = ...; Observable b = a.observeOn(AndroidSchedulers.mainThread()); 
     其中b的onNext, onCompleted 方法是在主线程执行的，但a != b

归结起来就是 subscribeOn 管当前， observeOn管后面。

### RxJava 进阶小知识
-----------
需要说明一点，这里说的进阶知识并不是指这里的知识会难一些，只是这里的知识不是必要的，但是如果我们了解，我们的代码会更简洁。

##### Observable 更多的构造方法

 1 Observable.just(T t1, T t2, T t3, ...);//T 为泛型
       将会依次调用：
       onNext(t1);
       onNext(t2);
       onNext(t3);
 2 Observable.from(多种参数形式，数组什么应有尽有，自己在android studio研究吧);
 
##### Action0、Action1、Action2、。。。
Actionx 功能跟 Subscriber 差不多，在之前的所有的代码中的 Subscriber 都可以用 Action1代替。
Actionx 与 Subscriber 不同就是在于 Actionx 忽略了 Subscriber 中的 onCompleted 和 onError 接口，的确这个不常用。
Actionx 中的x代表泛型参数的个数。之前的 Subscriber 我们观察者只观察了一个位图参数，但如果你关注位图的同时还想关注位图的名字，你可以用Action2
这里给个简单展示：
```
        Action2<String, Bitmap> action2 = new Action2<String, Bitmap>() {
            @Override
            public void call(String name, Bitmap bitmap) {
            }
        };
```

##### flatMap()
flatMap()是一种特殊的转换

下面是例子，我们已经可以获取所有的人 Person ，现在我们想获取 所有人的所有朋友，我们需要平铺转换 flatMap。
其原理是将每个Person中的friends转化为 Observable 后由RxJava统一帮我们将所有的 Observable 合并，有没有点平铺的意思呢
```
    class Person {
        public String[] friends;
    }

    public void a() {
        Person[] people = ...;
        Observable.from(people)
                .flatMap(new Func1<Person, Observable<String>>() {
                    @Override
                    public Observable<String> call(Person person) {
                        return Observable.from(people.friends);
                    }
                })
                .subscribe(some);
    }
```

### 总结
----
在理解RxJava的时候，我们应该将观察者和被观察者想象成生活中的事物。我们应该相信所有框架的开发人员都是为了简化代码，提高代码的可读性去编写这个框架的。
这样明确了这一点，这个框架会变得非常有趣。
如果你打算使用RxJava的话，你的网络框架不妨试用Retrofit，Retrofit也是使用这种工作方式的。