package com.sample.signin.app.ui.main;

import android.app.Dialog;

import com.sample.signin.app.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void onSignedOut();

    void showDialog(Dialog dialog);

}
