# NewIM

基于BmobNewIMSDK开发的一个完整的陌生人聊天及好友管理的官方Demo

## NewIM的特点

新版IM具备以下特点：

- **与用户系统解耦**
  任何终端用户只需要提供一个唯一标识自己的`objectId`就可以加入聊天，不再局限于Bmob的用户系统

- **支持多账号登录、跨平台**
  支持`单个设备多个账号`登录，支持与iOS互通聊天

- **支持多种格式的富媒体消息**
  支持`文本`、`图片`、`音频`和`地理位置`等多种格式的富媒体消息，功能更加丰富

- **允许开发者自定义消息**
  支持开发者`自定义消息类型`，方便开发者扩展本业务逻辑相关的消息

- **API设计更加合理规范**
  全新的架构设计，API更加简单易用，较旧版进一步降低开发者使用成本

## NewIM 快速入门

请按照以下步骤完成NewIM的集成工作：

#### 下载BmobNewIMSDK

下载[BmobNewIMSDK](http://www.bmob.cn/site/sdk#android_im_sdk_tab)：其包含NewIMSDK（相关libs包和values文件夹）以及官方Android studio工程。

注：

1、新版IMDemo只提供Android Studio版本。

2、解压之后的文件夹内容如下：

`libs(包含三个jar文件)`-这三个jar需要全部复制进工程中才可以使用新版IM

`values(bmob_im_notification_strings.xml)`-用于通知栏显示

`NewIM_V2.0.x_Demo`-官方发布的示例Demo,一个完整即时通讯Demo，包含陌生人聊天及基于好友之间的聊天（v2.0.4以后版本demo提供）。

***注：libs和values文件夹下面的都需要复制到应用中，否则将无法正常使用IM服务。***

#### 配置AndroidManifest.xml

1. 添加Bmob_APP_KEY：
```xml
   <meta-data
	    android:name="Bmob_APP_KEY"
	    android:value="Bmob平台的Application ID" />
```
***注：Bmob_APP_KEY 必须填写，否则无法正常使用IM服务。**
2.  添加权限

```xml
	<!--网络权限 -->
	<uses-permission android:name="android.permission.INTERNET" />
    <!-- 监听网络的变化 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
    <!-- 设备休眠 -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <!-- sd卡存储-->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!--摄像头-->
    <uses-permission android:name="android.permission.CAMERA" />
    <!--录音-->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <!--通知震动-->
    <uses-permission android:name="android.permission.VIBRATE" />

```
 3. 添加Service、receiver标签：

```xml
  <receiver android:name="cn.bmob.newim.core.ConnectChangeReceiver" >
	    <intent-filter>
	        <action android:name="cn.bmob.action.RECONNECT" />
	        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
	        <action android:name="android.intent.action.BOOT_COMPLETED" />
	        <action android:name="android.intent.action.USER_PRESENT" />
	    </intent-filter>
  </receiver>
  <service
    android:name="cn.bmob.newim.core.service.BmobImService"
    android:process=":bmobcore" />
   //v2.0.4版本增加service-用于进程保活
  <service
    android:name="cn.bmob.newim.core.service.NotifyService"
    android:process=":bmobcore" />
  <service android:name="cn.bmob.newim.core.service.ReConnectService" />
  <service android:name="cn.bmob.newim.core.service.HeartBeatService" />

```

#### 注册消息接收器

- 如果你使用的是`NewIM_V2.0.2以后(包含v2.0.2)`的SDK版本,那么你需要自定义消息接收器继承自`BmobIMMessageHandler`来处理服务器发来的消息和离线消息。

```xml

public class DemoMessageHandler extends BmobIMMessageHandler{

    @Override
    public void onMessageReceive(final MessageEvent event) {
        //当接收到服务器发来的消息时，此方法被调用
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
    }
}

```

别忘记在Application的onCreate方法中注册这个`DemoMessageHandler`：

```xml

public class BmobIMApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
		//NewIM初始化
		BmobIM.init(this);
		//注册消息接收器
        BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
    }
}
```

- 如果你使用的SDK版本是`NewIM_V2.0.1`,那么你需要在应用中创建一个广播消息接收器，用于接收服务器发来的消息。

```xml

public class MessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(intent!=null){
            final MessageEvent event =(MessageEvent)intent.getSerializableExtra("event");
            //开发者可以在这里发应用通知
    }
}

```

同样，别忘记在`AndroidManifest.xml`中注册这个receiver

```xml
<receiver
    android:name="程序包名.MessageReceiver"
    android:enabled="true">
    <intent-filter>
        <action android:name="cn.bmob.im.action.MESSAGE"/>
    </intent-filter>
</receiver>

```

#### 初始化

在Application的onCreate方法中调用`BmobIM.init(context)`

```xml
public class BmobIMApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
    }

	/**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}


```

注：

1. 初始化方法包含了BmobSDK的初始化步骤，故无需再初始化BmobSDK
2. 在初始化的时候，最好做下判断：只有主进程运行的时候才开始初始化，避免资源浪费。

#### 服务器连接

- 连接服务器：

```xml
User user = BmobUser.getCurrentUser(context,User.class);
BmobIM.connect(user.getObjectId(), new ConnectListener() {
    @Override
    public void done(String uid, BmobException e) {
        if (e == null) {
            Logger.i("connect success");
        } else {
            Logger.e(e.getErrorCode() + "/" + e.getMessage());
        }
    }
});

```

注：

调用`connect`方法，需要传入一个唯一的用户标示`clientId`，Demo使用的是Bmob的用户登录系统。

- 断开连接：

```xml
BmobIM.getInstance().disConnect();

```

注：调用`disConnect`方法，客户端会断开与服务器之间的连接，再次聊天需要重新调用`connect`方法完成与服务器之间的连接。

- 监听服务器连接状态

调用`setOnConnectStatusChangeListener`方法即可监听到当前长链接的连接状态

```java
 BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                Logger.i("" + status.getMsg());
            }
        });

```