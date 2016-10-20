package com.sample.signin.app.ui.signin;

import android.app.Dialog;

import com.sample.signin.app.data.model.user.User;
import com.sample.signin.app.ui.base.MvpView;

public interface SignInMvpView extends MvpView {

    void handleSignIn(User user);

    //TODO Move dialog creation to activity
    void showDialog(Dialog dialog);

}
