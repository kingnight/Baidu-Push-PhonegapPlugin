# 推送插件 #

# 1.Android客户端安装

## 1.1 修改原始插件的plugin.xml
plugin.xml中

    <!-- android -->
    <platform name="android">		
		<!-- 在百度开发者中心查询应用的API Key -->
	        <meta-data android:name="api_key" android:value="" />
    </platform>

其中的android:value需要修改为从百度开发者网站获得的API Key

##  1.2 修改插件安装完成后生成的Android项目AndroidManifest.xml文件

找到

    <application android:debuggable="true" android:hardwareAccelerated="true" android:icon="@drawable/icon" android:label="@string/app_name" >

添加 android:name="com.baidu.frontia.FrontiaApplication"到 <application>标签末尾

    <application android:debuggable="true" android:hardwareAccelerated="true" android:icon="@drawable/icon" android:label="@string/app_name" android:name="com.baidu.frontia.FrontiaApplication">
    

# 2. iOS客户端安装

## 2.1 Xcode中 Bundle Identifier 必须与在apple developer 官网申请的具有推送资格的appID完全一致
</br>
## 2.2 BPushConfig.plist中 API_KEY 需要添加从百度开发者网站获得的API Key ##
</br>

## 2.3 在开发的程序的主程序AppDelegate.m中，添加以下三个回调函数
</br>
**添加头文佳引入#import "BPush.h"**

	#import "BPush.h"

    - (void)application:(UIApplication *)application      didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
       NSLog(@"test:%@",deviceToken);
       [BPush registerDeviceToken: deviceToken];
    }
    
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

	- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
	{
   		 NSLog(@"error=%@",error);
	}


其中以下部分代码用于自定义接受消息后处理模式

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
	


## 2.4 生产版本

BPushConfg.plist中的PRODUCT_MODE，如果是测试开发版请设置为NO，生产版设置为YES；
修改BPushConfg.plis的配置后，请卸载应用再安装；


选中Targets—>Build Settings—>Architectures。双击Architectures，选择other，删除$(ARCH_STANDARD)(点’-’)，然后增加armv7和armv7s(点‘+’)。clean一下再编译就行了。

## 2.5 如果编译时提示

	Undefined symbols for architecture armv7s:

	  "_OBJC_CLASS_$_BPush", referenced from:
  
请注意检查libBpush.a 这个依赖库文件是否正确添加成功
