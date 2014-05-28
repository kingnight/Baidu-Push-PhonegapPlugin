//
//  PushManager.m
//
//  Created by aisino on 13-11-6.
//
//

#import "PushManager.h"
#import "BPush.h"

@interface PushManager()
@property (nonatomic,strong) NSString *userName;
@property (nonatomic,strong) NSString *callid_init;
@property (nonatomic,strong) NSString *callid_setTags;
@property (nonatomic,strong) NSString *callid_delTags;
@property (nonatomic,strong) NSString *serverUrl;
@end

@implementation PushManager

- (void) initWork:(CDVInvokedUrlCommand*)command{
    //NSLog(@"On initWork:");
    self.userName=[[NSString alloc]initWithFormat:@"%@",[command argumentAtIndex:0]];
    self.serverUrl=[[NSString alloc]initWithFormat:@"%@",[command argumentAtIndex:1]];
    
    self.callid_init=[[NSString alloc] initWithString:[NSString stringWithFormat:@"%@",command.callbackId]];
    [BPush setupChannel:nil];
    [BPush setDelegate:self];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
     UIRemoteNotificationTypeAlert
     | UIRemoteNotificationTypeBadge
     | UIRemoteNotificationTypeSound];
    //NSLog(@"On bindChannel:");
    [BPush bindChannel];
    
}

- (void) setTags:(CDVInvokedUrlCommand*)command{
    //NSLog(@"setTags server ");
    self.callid_setTags=[[NSString alloc] initWithString:[NSString stringWithFormat:@"%@",command.callbackId]];
    NSString *parameters = [command.arguments objectAtIndex:0];
    //NSLog(@"setTags server %@",parameters);
    NSArray *tags = [parameters componentsSeparatedByString:@","];
    [BPush setTags:tags];
}

- (void) delTags:(CDVInvokedUrlCommand*)command{
    //NSLog(@"setTags server ");
    self.callid_delTags=[[NSString alloc] initWithString:[NSString stringWithFormat:@"%@",command.callbackId]];
    NSString *parameters = [command.arguments objectAtIndex:0];
    //NSLog(@"setTags server %@",parameters);
    NSArray *tags = [parameters componentsSeparatedByString:@","];
    [BPush delTags:tags];
}

- (void) stopWork:(CDVInvokedUrlCommand*)command{
    
    [BPush unbindChannel];
}

- (void) onMethod:(NSString*)method response:(NSDictionary*)data {
    //NSLog(@"On method:%@", method);
    //NSLog(@"data:%@", [data description]);
    NSDictionary* res = [[NSDictionary alloc] initWithDictionary:data];
    NSString * appid = [res valueForKey:BPushRequestAppIdKey];
    NSString * userid = [res valueForKey:BPushRequestUserIdKey];
    NSString * channelid = [res valueForKey:BPushRequestChannelIdKey];
    
    
    if ([BPushRequestMethod_Bind isEqualToString:method]){
        
        //NSString *requestid = [res valueForKey:BPushRequestRequestIdKey];
        int returnCode = [[res valueForKey:BPushRequestErrorCodeKey] intValue];
        
        if (returnCode == BPushErrorCode_Success){
            
            if ([self serverUrl]) {
                NSString *userNameforload=[self userName];
                //NSLog(@"userNameforload=%@",userNameforload);
                NSString *post = [NSString stringWithFormat:@"user_name=%@&action=initUser&appid=%@&user_id=%@&channel_id=%@&platform=ios",userNameforload,appid,userid,channelid ];
                NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
                
                NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
                
                NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
                [request setURL:[NSURL URLWithString:[self serverUrl]]];
                [request setHTTPMethod:@"POST"];
                [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
                [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
                [request setHTTPBody:postData];
                //BUG 未来要改为异步
                NSData *received = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
                
                NSString *str1 = [[NSString alloc]initWithData:received encoding:NSUTF8StringEncoding];
                
                NSLog(@"middletools server %@",str1);
            }
            
            //add js callback for push userID and channelID
            NSDictionary *pushCB=[[NSDictionary alloc] initWithObjectsAndKeys:userid,@"userID",channelid,@"channelID",appid,@"appID", nil];
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:pushCB];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callid_init];
            
        }
        else{
            NSString *code=[self translateBPushErrorCode:returnCode];
            NSLog(@"PUSH ERROR:%@",code);
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:returnCode];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callid_init];
        }
    }
    else if ([BPushRequestMethod_Unbind isEqualToString:method]) {
        int returnCode = [[res valueForKey:BPushRequestErrorCodeKey] intValue];
        if (returnCode == BPushErrorCode_Success) {
            
        }
        else
        {
            NSString *code=[self translateBPushErrorCode:returnCode];
            NSLog(@"PUSH ERROR:%@",code);
        }
    }
    else if ([BPushRequestMethod_SetTag isEqualToString:method]){
        int returnCode = [[res valueForKey:BPushRequestErrorCodeKey] intValue];
        if (returnCode == BPushErrorCode_Success) {
            if ([self serverUrl]) {
                //接收tag 格式信息
                NSDictionary *response_param=[res valueForKey:BPushRequestResponseParamsKey];
                NSArray *details=[response_param valueForKey:@"details"];
                NSString *tags=[NSString stringWithFormat:@"%@", nil];
                int i;
                for (i=0; i<[details count]; i++) {
                    if (i==0) {
                        tags=[NSString stringWithFormat:@"%@", [[details objectAtIndex:i] valueForKey:@"tag"]];
                    }else{
                        tags=[NSString stringWithFormat:@"%@,%@", tags,[[details objectAtIndex:i] valueForKey:@"tag"]];
                    }
                }
                NSLog(@"tags %@",tags);
                //NSLog(@"userid %@",userid);
                NSString *post = [NSString stringWithFormat:@"action=setTag&user_id=%@&tag=%@",[BPush getUserId],tags];
                NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
                
                NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
                
                NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
                [request setURL:[NSURL URLWithString:[self serverUrl]]];
                [request setHTTPMethod:@"POST"];
                [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
                [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
                [request setHTTPBody:postData];
                //BUG 未来要改为异步
                NSData *received = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
                
                NSString *str1 = [[NSString alloc]initWithData:received encoding:NSUTF8StringEncoding];
                
                NSLog(@"middletools server %@",str1);
            }
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:returnCode];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callid_setTags];
            
        }else{
            NSString *code=[self translateBPushErrorCode:returnCode];
            NSLog(@"PUSH ERROR:%@",code);
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:returnCode];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callid_setTags];
        }
        
    }
    else if ([BPushRequestMethod_DelTag isEqualToString:method]){
        int returnCode = [[res valueForKey:BPushRequestErrorCodeKey] intValue];
        if (returnCode == BPushErrorCode_Success) {
            
            if ([self serverUrl]) {
                //接收tag 格式信息
                NSDictionary *response_param=[res valueForKey:BPushRequestResponseParamsKey];
                NSArray *details=[response_param valueForKey:@"details"];
                NSString *tags=[NSString stringWithFormat:@"%@", nil];
                int i;
                for (i=0; i<[details count]; i++) {
                    if (i==0) {
                        tags=[NSString stringWithFormat:@"%@", [[details objectAtIndex:i] valueForKey:@"tag"]];
                    }else{
                        tags=[NSString stringWithFormat:@"%@,%@", tags,[[details objectAtIndex:i] valueForKey:@"tag"]];
                    }
                }
                NSLog(@"tags %@",tags);
                //NSLog(@"userid %@",userid);
                NSString *post = [NSString stringWithFormat:@"action=delTag&user_id=%@&tag=%@",[BPush getUserId],tags ];
                NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
                
                NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
                
                NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
                [request setURL:[NSURL URLWithString:[self serverUrl]]];
                [request setHTTPMethod:@"POST"];
                [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
                [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
                [request setHTTPBody:postData];
                //BUG 未来要改为异步
                NSData *received = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
                
                NSString *str1 = [[NSString alloc]initWithData:received encoding:NSUTF8StringEncoding];
                
                NSLog(@"middletools server %@",str1);
            }
            
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:returnCode];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callid_delTags];
            
        }
        else{
            NSString *code=[self translateBPushErrorCode:returnCode];
            NSLog(@"PUSH ERROR:%@",code);
            
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsInt:returnCode];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:self.callid_delTags];
        }
    }
    
}
/*
 *BpushErrorCode_Success = 0,
 BpushErrorCode_MethodTooOften = 22, // 方法调用太频繁，如循环调用 bind
 BpushErrorCode_NetworkInvalible = 10002, // 网络连接错误
 BpushErrorCode_InternalError = 30600, // 服务器内部错误
 BpushErrorCode_MethodNodAllowed = 30601, // 请求方法不允许
 BpushErrorCode_ParamsNotValid = 30602, // 请求参数错误
 BpushErrorCode_AuthenFailed = 30603, // 权限验证失败
 BpushErrorCode_DataNotFound = 30605, // 请求数据不存在
 BpushErrorCode_RequestExpired = 30606, // 请求时间戳验证超时
 BpushErrorCode_BindNotExists = 30608, // 绑定关系不存在
 */
-(NSString*)translateBPushErrorCode:(int)returnCode{
    switch (returnCode) {
        case BPushErrorCode_MethodTooOften:
        return @"方法调用太频繁，如循环调用 bind";
        break;
        case BPushErrorCode_NetworkInvalible:
        return @"网络连接错误";
        break;
        case BPushErrorCode_InternalError:
        return @"服务器内部错误";
        break;
        case BPushErrorCode_MethodNodAllowed:
        return @"请求方法不允许";
        break;
        case BPushErrorCode_ParamsNotValid:
        return @"请求参数错误";
        break;
        case BPushErrorCode_AuthenFailed:
        return @"权限验证失败";
        break;
        case BPushErrorCode_DataNotFound:
        return @"请求数据不存在";
        break;
        case BPushErrorCode_RequestExpired:
        return @"请求时间戳验证超时";
        break;
        case BPushErrorCode_BindNotExists:
        return @"绑定关系不存在";
        break;
        default:
        return nil;
        break;
    }
}




@end
