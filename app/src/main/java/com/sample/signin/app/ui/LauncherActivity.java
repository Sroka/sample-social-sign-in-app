package com.sample.signin.app.ui;

import android.content.Intent;
import android.os.Bundle;

import com.sample.signin.app.ui.base.BaseActivity;
import com.sample.signin.app.ui.signin.SignInActivity;

import timber.log.Timber;

/**
 * Starting point activity, from here we should decide if we want to force user to authenticate, find out if he was already authenticatedor what
 */
public class LauncherActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("OnCreate");
        activityComponent().inject(this);
        Intent intent = SignInActivity.getStartIntent(this, false);
        startActivity(intent);
    }
}
