//
//  RNBackgroundTimer.h
//  react-native-background-timer
//

#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <RNBackgroundTimerSpec/RNBackgroundTimerSpec.h>
#endif

NS_ASSUME_NONNULL_BEGIN

#ifdef RCT_NEW_ARCH_ENABLED
@interface RNBackgroundTimer : RCTEventEmitter <NativeRNBackgroundTimerSpec>
#else
@interface RNBackgroundTimer : RCTEventEmitter
#endif

@end

NS_ASSUME_NONNULL_END
