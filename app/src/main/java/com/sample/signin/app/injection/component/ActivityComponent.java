package com.sample.signin.app.injection.component;

import com.sample.signin.app.injection.PerActivity;
import com.sample.signin.app.injection.module.ActivityModule;
import com.sample.signin.app.ui.LauncherActivity;
import com.sample.signin.app.ui.main.MainActivity;
import com.sample.signin.app.ui.signin.SignInActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(SignInActivity signInActivity);
    void inject(LauncherActivity launcherActivity);
    void inject(MainActivity mainActivity);
}

