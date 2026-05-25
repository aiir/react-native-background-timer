package com.ocetnik.timer;

import android.os.Handler;
import android.os.PowerManager;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class BackgroundTimerModule extends ReactContextBaseJavaModule {

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

    @ReactMethod
    public void start(final int delay) {
        if (!wakeLock.isHeld()) wakeLock.acquire();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                sendEvent("backgroundTimer");
            }
        };
        handler.post(runnable);
    }

    @ReactMethod
    public void stop() {
        if (wakeLock.isHeld()) wakeLock.release();
        if (handler != null) handler.removeCallbacks(runnable);
    }

    @ReactMethod
    public void setTimeout(final int id, final double timeout) {
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getReactApplicationContext().hasActiveReactInstance()) {
                    sendEvent("backgroundTimer.timeout", id);
                }
            }
        }, (long) timeout);
    }

    private void sendEvent(String eventName) {
        sendEvent(eventName, null);
    }

    private void sendEvent(String eventName, Object params) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }
}
