package com.ocetnik.timer;

import android.os.Handler;
import android.os.PowerManager;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@ReactModule(name = BackgroundTimerModule.NAME)
public class BackgroundTimerModule extends NativeRNBackgroundTimerSpec {

    public static final String NAME = "RNBackgroundTimer";

    private Handler handler;
    private PowerManager.WakeLock wakeLock;
    private Runnable runnable;

    private final LifecycleEventListener listener = new LifecycleEventListener() {
        @Override
        public void onHostResume() {}

        @Override
        public void onHostPause() {}

        @Override
        public void onHostDestroy() {
            if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        }
    };

    public BackgroundTimerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        PowerManager powerManager = (PowerManager) reactContext.getSystemService(reactContext.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RNBackgroundTimer:wakelock");
        reactContext.addLifecycleEventListener(listener);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start(double delay, Promise promise) {
        if (!wakeLock.isHeld()) wakeLock.acquire();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendEvent("backgroundTimer", null);
            }
        };
        handler.post(runnable);
        promise.resolve(true);
    }

    @Override
    public void stop(Promise promise) {
        if (wakeLock.isHeld()) wakeLock.release();
        if (handler != null) handler.removeCallbacks(runnable);
        promise.resolve(true);
    }

    @Override
    public void setTimeout(double timeoutId, double timeout, Promise promise) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getReactApplicationContext().hasActiveReactInstance()) {
                    sendEvent("backgroundTimer.timeout", timeoutId);
                }
            }
        }, (long) timeout);
        promise.resolve(true);
    }

    @Override
    public void addListener(String eventName) {}

    @Override
    public void removeListeners(double count) {}

    private void sendEvent(String eventName, Object params) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }
}
