# 推送插件 #

# 1.Android客户端安装

如果首次安装请顺序阅读，升级插件请首先阅读__1.3 升级提示__

## 1.1 修改原始插件的plugin.xml
plugin.xml中

    <!-- android -->
    <platform name="android">		
		<!-- 在百度开发者中心查询应用的API Key -->
	        <meta-data android:name="api_key" android:value="PmdxEvYG5HatSGXzCYzqWahT" />
    </platform>

其中的android:value需要修改为从百度开发者网站获得的API Key

##  1.2 修改插件安装完成后生成的Android项目AndroidManifest.xml文件

找到

    <application android:debuggable="true" android:hardwareAccelerated="true" android:icon="@drawable/icon" android:label="@string/app_name" >

添加 android:name="com.baidu.frontia.FrontiaApplication"到 <application>标签末尾

    <application android:debuggable="true" android:hardwareAccelerated="true" android:icon="@drawable/icon" android:label="@string/app_name" android:name="com.baidu.frontia.FrontiaApplication">
    
    
## 1.3 升级提示
升级插件时，请首先卸载插件，然后重新安装！应用层代码无需改动

__主要改动：__

将 libs 目录下 armeabi/mips 目录下原有的 libbdpush_V*_*.so,替换为最新的libbdpush_V2_2.so, 最新版本不再单独提供 x86下so,目前 x86 机型均支持 arm 指令集兼容,push 功能运行正常,请删除原有x86目录下的so文件。
    

# 2. iOS客户端安装

## 2.1 Xcode中 Bundle Identifier 必须与在apple developer 官网申请的具有推送资格的appID完全一致
</br>
## 2.2 BPushConfig.plist中 API_KEY 需要添加从百度开发者网站获得的API Key ##
</br>

## 2.3 在开发的程序的主程序AppDelegate.m中，添加以下代码

###2.3.1 添加头文件

	#import "BPush.h"


###2.3.2 设置channel

	- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions

在函数尾部return YES;前添加

	[BPush setupChannel:launchOptions];	

###2.3.3 注册token


    - (void)application:(UIApplication *)application      didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {

    }
在此函数末尾添加   

       [BPush registerDeviceToken: deviceToken];

###2.3.4 接收推送

AppDelegate.m尾部添加
    
	- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
      	
      	NSString *alert = [[userInfo objectForKey:@"aps"] objectForKey:@"alert"];
      	if (application.applicationState == UIApplicationStateActive) 
      	{
        // Nothing to do if applicationState is Inactive, the iOS already displayed an alert view.
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Did receive a Remote Notification" 
        	message:[NSString stringWithFormat:@"The application received this remote  notification while it was running:\n%@", alert]
            delegate:self
            cancelButtonTitle:@"OK"
            otherButtonTitles:nil];
        [alertView show];
    }
    	[application setApplicationIconBadgeNumber:0];
    
    	[BPush handleNotification:userInfo];
    }
    
    
其中以下部分代码用于自定义接受消息后处理模式，可以根据需求定制

    if (application.applicationState == UIApplicationStateActive)
    {
        // Nothing to do if applicationState is Inactive, the iOS already displayed an alert view.
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Did receive a Remote Notification"
                    message:[NSString stringWithFormat:@"The application received this remote  notification while it was running:\n%@", alert]
                    delegate:self
                    cancelButtonTitle:@"OK"
                    otherButtonTitles:nil];
        [alertView show];
    }	

###2.3.5	 添加iOS8支持

	#if SUPPORT_IOS8
	- (void)application:(UIApplication *)application 		didRegisterUserNotificationSettings:(UIUserNotificationSettings 		*)notificationSettings
	{
    	//register to receive notifications
    	[application registerForRemoteNotifications];
	}
	#endif

###2.3.6 添加前缀

在当前项目的xxx－Prefix.pch文件中添加

	#define SUPPORT_IOS8 1

如果设置为1，标志支持iOS8，否则设置为0
	


## 2.4 生产版本

BPushConfg.plist中的PRODUCT_MODE，如果是测试开发版请设置为NO，生产版设置为YES；
修改BPushConfg.plis的配置后，请卸载应用再安装；


选中Targets—>Build Settings—>Architectures。双击Architectures，选择other，删除$(ARCH_STANDARD)(点’-’)，然后增加armv7和armv7s(点‘+’)。clean一下再编译就行了。

## 2.5 错误信息处理


###2.5.1

	Undefined symbols for architecture armv7s:

	  "_OBJC_CLASS_$_BPush", referenced from:
  
请注意检查libBpush.a 这个依赖库文件是否正确添加成功


###2.5.2

	register Remote Notification error=Error Domain=NSCocoaErrorDomain Code=3000 "未找到应用程序的“aps-environment”的授权字符串" UserInfo=0x155a7270 {NSLocalizedDescription=未找到应用程序的“aps-environment”的授权字符串}

当前Mac中不包含推送证书，请去developer.apple.com下载推送证书
