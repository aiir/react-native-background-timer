package com.ocetnik.timer;

import androidx.annotation.Nullable;

import com.facebook.react.BaseReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;

import java.util.HashMap;
import java.util.Map;

public class BackgroundTimerPackage extends BaseReactPackage {

    @Nullable
    @Override
    public NativeModule getModule(String name, ReactApplicationContext reactContext) {
        if (BackgroundTimerModule.NAME.equals(name)) {
            return new BackgroundTimerModule(reactContext);
        }
        return null;
    }

    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return () -> {
            final Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
            moduleInfos.put(
                BackgroundTimerModule.NAME,
                new ReactModuleInfo(
                    BackgroundTimerModule.NAME,
                    BackgroundTimerModule.class.getName(),
                    false,
                    false,
                    false,
                    BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
                )
            );
            return moduleInfos;
        };
    }
}
