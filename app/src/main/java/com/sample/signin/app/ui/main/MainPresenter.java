package com.sample.signin.app.ui.main;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.sample.signin.app.data.login.LoginType;
import com.sample.signin.app.data.login.SignInException;
import com.sample.signin.app.data.login.facebook.FacebookLoginService;
import com.sample.signin.app.data.login.google.GoogleApiClientService;
import com.sample.signin.app.data.login.google.GoogleLoginService;
import com.sample.signin.app.injection.ApplicationContext;
import com.sample.signin.app.ui.base.Presenter;
import com.sample.signin.app.util.RxUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class MainPresenter implements Presenter<MainMvpView> {

    private final GoogleApiClientService googleApiClientService;
    private final GoogleLoginService googleLoginService;
    private final FacebookLoginService facebookLoginService;
    private final Context applicationContext;
    private MainMvpView mvpView;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MainPresenter(GoogleApiClientService googleApiClientService,
                         GoogleLoginService googleLoginService,
                         FacebookLoginService facebookLoginService,
                         @ApplicationContext Context applicationContext) {
        this.googleApiClientService = googleApiClientService;
        this.googleLoginService = googleLoginService;
        this.facebookLoginService = facebookLoginService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        mvpView = null;
        subscriptions.unsubscribe();
    }

    public void signOut(LoginType loginType) {
        switch (loginType) {
            case GOOGLE:
                Observable<GoogleApiClient> googleApiClientObservable = googleApiClientService.googleApiClient().cache().single();
                subscriptions.add(googleApiClientObservable
                        .map(GoogleApiClient::blockingConnect)
                        .flatMap(connectionResult -> {
                            if (connectionResult.isSuccess()) {
                                return googleApiClientObservable;
                            } else {
                                return SignInException.asErrorObservable("Google play services unavailable");
                            }
                        }).flatMap(new Func1<GoogleApiClient, Observable<Status>>() {
                            @Override
                            public Observable<Status> call(GoogleApiClient googleApiClient) {
                                return googleLoginService.signOut(googleApiClient).timeout(5, TimeUnit.SECONDS)
                                        .onErrorResumeNext(SignInException.asErrorObservable("Sign out timed out"));
                            }
                        })
                        .compose(RxUtils.applyIoMainSchedulers())
                        .subscribe(status -> {
                            mvpView.onSignedOut();
                        }, throwable -> {
                            Toast.makeText(applicationContext, "Not signed out safely due to: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            mvpView.onSignedOut();
                        }));

                break;
            case FACEBOOK:
                facebookLoginService.signOut();
                mvpView.onSignedOut();
                break;
        }
    }
}
