package com.sample.signin.app.injection.module;

import android.app.Application;
import android.content.Context;

import com.sample.signin.app.data.model.user.UsersProvider;
import com.sample.signin.app.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies. Mainly singleton object that can be injected from
 * anywhere in the app.
 */
@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    UsersProvider provideUsersProvider() {
        return new UsersProvider();
    }
}
