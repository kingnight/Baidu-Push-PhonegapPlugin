<!---
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

/*Title: 推送Description: 百度推送客户端API接口*/


# aisino-plugin-push



## 安装

    cordova plugin add aisino-plugin-push/

## initwork


推送客户端初始化函数，包含以下三个参数：

- __SuccessCallBack__: 初始化成功回调函数。该回调函数以参数形式返回一个对象，包含三个参数：userID，channelID，appID。 _(Function)_


- __FailCallBack__: 初始化失败回调函数。 该回调函数以参数形式返回一个字符串说明失败原因。_(Function)_

- __UserName__: 用户名，用于配合服务器端账号系统。 _(String)_

- __ServerUrl__: 服务器地址，可选。 _(String)_

如果指定服务器地址，则init初始化成功后，客户端自动向指定服务器发送以下格式字符串信息："user_name=%@&action=initUser&appid=%@&user_id=%@&channel_id=%@&platform=%@" ，其中％@参数将被实际参数替换， 

* user_name 传递用户名
* action 说明行为方式
* appid  传递当前应用appid
* user_id  当前用户userID
* channel_id 当前用户channelID
* platform   当前系统平台，如iOS或Android

### 支持平台

- iOS
- Android


### 示例

    window.plugin.pushManager.initWork(initSucc, initFail, "Tommy","http://192.168.44.7/Mserver");

    function initSucc(args) {
        console.log("userID: " + args.userID + " channelID: " + args.channelID + "appID:"+args.appID);
    }
    
    function initFail(args){
        console.log("initWork Fail: " + args);    
    }

	
## setTags

设置标签

- __SuccessCallBack__: 设置成功回调函数，返回值参见错误码。 _(Function)_

- __FailCallBack__: 设置失败回调函数，返回值参见错误码。 _(Function)_

- __Tags__: 设置标签内容，可以是多个标签，之间使用“,”分割。 _(String)_

如果初始化时指定服务器地址，则setTags操作成功后，客户端自动向指定服务器发送以下格式字符串信息：“action=setTag&user_id=%@&tag=%@” ，其中％@参数将被实际参数替换， 

* user_id  当前用户userID
* tag 设置tag信息

### 支持平台

- iOS
- Android

### 示例

	function setTagsFail(args){
        alert("setTagsFail:"+args);
    }
                  
    function setTagSucc(args){
        alert("setTagSSucc:"+args);
    }
    window.plugin.pushManager.setTags(setTagSucc,setTagsFail,"tagA,tagB,tagC");


## delTags

删除标签

- __SuccessCallBack__: 删除成功回调函数，返回值参见错误码。 _(Function)_

- __FailCallBack__: 删除失败回调函数，返回值参见错误码。 _(Function)_

- __Tags__: 删除标签内容，可以是多个标签，之间使用“,”分割。 _(String)_

如果初始化时指定服务器地址，则delTags操作成功后，客户端自动向指定服务器发送以下格式字符串信息："action=delTag&user_id=%@&tag=%@" ，其中％@参数将被实际参数替换， 

* user_id  当前用户userID
* tag 设置tag信息


### 支持平台

- iOS
- Android

### 示例

    function delTagsFail(args){
        alert("delTagsFail:"+args);
    }
                  
    function delTagsSucc(args){
        alert("delTagsSucc:"+args);
    }
	window.plugin.pushManager.delTags(delTagsSucc,delTagsFail,"tagC");    

## listTags

列出所有标签

- __CallBack__: 回调函数，返回字符串对象，包含所有tags，以","分割 _(Function)_


### 支持平台

- iOS
- Android

### 示例

	function listTagsSucc(taglist){
       alert("listTagsSucc:"+taglist);
    }
    window.plugin.pushManager.listTags(listTagsSucc);


<!--## getTagInfo

获取tag信息

- __CallBack__: 回调函数，返回获取到的信息 _(Function)_



### 支持平台

- Android

### 示例

    //only for android
    function getTagsSucc(args){
        alert(args);
    }
    window.plugin.pushManager.getTagInfo(getTagsSucc);-->
    
    
## 错误码

</br>

 BpushErrorCode_Success = 0,//成功
 BpushErrorCode_MethodTooOften = 22, // 方法调用太频繁，如循环调用 bind
 BpushErrorCode_NetworkInvalible = 10002, // 网络连接错误
 BpushErrorCode_InternalError = 30600, // 服务器内部错误
 BpushErrorCode_MethodNodAllowed = 30601, // 请求方法不允许
 BpushErrorCode_ParamsNotValid = 30602, // 请求参数错误
 BpushErrorCode_AuthenFailed = 30603, // 权限验证失败
 BpushErrorCode_DataNotFound = 30605, // 请求数据不存在
 BpushErrorCode_RequestExpired = 30606, // 请求时间戳验证超时
 BpushErrorCode_BindNotExists = 30608, // 绑定关系不存在
