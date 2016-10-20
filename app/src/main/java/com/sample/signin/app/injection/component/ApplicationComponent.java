package com.sample.signin.app.injection.component;

import android.app.Application;
import android.content.Context;

import com.sample.signin.app.CustomApplication;
import com.sample.signin.app.injection.ApplicationContext;
import com.sample.signin.app.injection.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(CustomApplication customApplication);

    @ApplicationContext Context context();
    Application application();
}
