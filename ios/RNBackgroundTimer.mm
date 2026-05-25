//
//  RNBackgroundTimer.mm
//  react-native-background-timer
//

#import <UIKit/UIKit.h>
#import "RNBackgroundTimer.h"

#ifdef RCT_NEW_ARCH_ENABLED
#import <React/RCTTurboModule.h>
#endif

@implementation RNBackgroundTimer {
    UIBackgroundTaskIdentifier bgTask;
    int delay;
    BOOL _hasListeners;
}

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents {
    return @[@"backgroundTimer", @"backgroundTimer.timeout"];
}

- (void)startObserving {
    _hasListeners = YES;
}

- (void)stopObserving {
    _hasListeners = NO;
}

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

- (void)_start
{
    [self _stop];
    bgTask = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"RNBackgroundTimer" expirationHandler:^{
        [[UIApplication sharedApplication] endBackgroundTask:self->bgTask];
        self->bgTask = UIBackgroundTaskInvalid;
    }];

    UIBackgroundTaskIdentifier thisBgTask = bgTask;
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self->_hasListeners && thisBgTask == self->bgTask) {
            [self sendEventWithName:@"backgroundTimer" body:[NSNumber numberWithInt:(int)thisBgTask]];
        }
    });
}

- (void)_stop
{
    if (bgTask != UIBackgroundTaskInvalid) {
        [[UIApplication sharedApplication] endBackgroundTask:bgTask];
        bgTask = UIBackgroundTaskInvalid;
    }
}

RCT_EXPORT_METHOD(start:(double)_delay
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    delay = (int)_delay;
    [self _start];
    resolve(@YES);
}

RCT_EXPORT_METHOD(stop:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [self _stop];
    resolve(@YES);
}

RCT_EXPORT_METHOD(setTimeout:(double)timeoutId
                     timeout:(double)timeout
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    __block UIBackgroundTaskIdentifier task = [[UIApplication sharedApplication] beginBackgroundTaskWithName:@"RNBackgroundTimer" expirationHandler:^{
        [[UIApplication sharedApplication] endBackgroundTask:task];
    }];

    __weak __typeof__(self) weakSelf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(timeout * NSEC_PER_MSEC)), dispatch_get_main_queue(), ^{
        __typeof__(self) strongSelf = weakSelf;
        if (strongSelf && strongSelf->_hasListeners) {
            [strongSelf sendEventWithName:@"backgroundTimer.timeout" body:@(timeoutId)];
        }
        [[UIApplication sharedApplication] endBackgroundTask:task];
    });
    resolve(@YES);
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRNBackgroundTimerSpecJSI>(params);
}
#endif

@end
