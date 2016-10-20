package com.sample.signin.app;

import android.app.Application;
import android.content.Context;

import com.sample.signin.app.injection.component.ApplicationComponent;

import com.sample.signin.app.injection.component.DaggerApplicationComponent;
import com.sample.signin.app.injection.module.ApplicationModule;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import timber.log.Timber;

public class CustomApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);
    }

    public static CustomApplication get(Context context) {
        return (CustomApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }
}

