package com.sample.signin.app.ui.base;

/**
 * Every presenter in the app must either implement this interface
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();
}
