//
//  PushManager.h
//
//  Created by aisino on 13-11-6.
//
//

#import <Foundation/Foundation.h>
#import <MessageUI/MFMessageComposeViewController.h>
#import <Cordova/CDVPlugin.h>

@interface PushManager : CDVPlugin

- (void) initWork:(CDVInvokedUrlCommand*)command;
- (void) setTags:(CDVInvokedUrlCommand*)command;
- (void) delTags:(CDVInvokedUrlCommand*)command;
- (void) stopWork:(CDVInvokedUrlCommand*)command;

@end
