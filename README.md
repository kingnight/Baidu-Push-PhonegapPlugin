# 推送插件 #

# 1.Android客户端安装
</br>

plugin.xml中

    <!-- android -->
    <platform name="android">
		

		<!-- 在百度开发者中心查询应用的API Key -->
	        <meta-data android:name="api_key" android:value="PmdxEvYG5HatSGXzCYzqWahT" />


    </platform>


其中的android:value需要修改为从百度开发者网站获得的API Key

# 2.iOS客户端安装
</br>
## 1.Xcode中 Bundle Identifier 必须与在apple developer 官网申请的具有推送资格的appID完全一致
</br>
## 2.在主程序AppDelegate.m中作如下修改
</br>
###2.1添加头文件
	#import "BPush.h"
	
###2.2添加代码：在现有代码末尾，@end前面加入以下代码	

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
	
其中：

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
这部分代码用于应用运行于前台时提示，用户可根据自身应该需求修改或删除	
	
## 3.修改BPushConfig.plist中API_KEY为申请得到的百度推送APP对应的API_KEY

## 4.已知问题解决方案：

### 4.1 如果xcode升级到5.1，则系统默认允许64位编译器编译，但是目前插件不支持，所以需要调整编译器配置
选中Targets—>Build Settings—>Architectures。双击Architectures，选择other，删除$(ARCH_STANDARD)(点’-’)，然后增加armv7和armv7s(点‘+’)。clean一下再编译就行了。

### 4.2如果编译时提示

Undefined symbols for architecture armv7s:

  "_OBJC_CLASS_$_BPush", referenced from:
  
 请注意检查libBpush.a 这个依赖库文件是否正确添加成功
 
 
