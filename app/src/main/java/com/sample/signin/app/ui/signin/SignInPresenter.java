package com.sample.signin.app.ui.signin;

import android.app.Activity;
import android.app.Dialog;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.sample.signin.app.R;
import com.sample.signin.app.data.login.ActivityResult;
import com.sample.signin.app.data.login.LoginType;
import com.sample.signin.app.data.login.SignInException;
import com.sample.signin.app.data.login.facebook.FacebookLoginService;
import com.sample.signin.app.data.login.google.GoogleApiClientService;
import com.sample.signin.app.data.login.google.GoogleLoginService;
import com.sample.signin.app.data.model.user.User;
import com.sample.signin.app.ui.base.Presenter;
import com.sample.signin.app.util.DialogFactory;
import com.sample.signin.app.util.NetworkUtil;
import com.sample.signin.app.util.RxUtils;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SignInPresenter implements Presenter<SignInMvpView> {

    private final GoogleApiClientService googleApiClientService;
    private final GoogleLoginService googleLoginService;
    private final FacebookLoginService facebookLoginService;
    private SignInMvpView mvpView;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @DebugLog
    @Inject
    public SignInPresenter(GoogleApiClientService googleApiClientService, GoogleLoginService googleLoginService, FacebookLoginService
            facebookLoginService) {
        this.googleApiClientService = googleApiClientService;
        this.googleLoginService = googleLoginService;
        this.facebookLoginService = facebookLoginService;
    }

    @DebugLog
    @Override
    public void attachView(SignInMvpView mvpView) {
        this.mvpView = mvpView;
    }

    @DebugLog
    @Override
    public void detachView() {
        subscriptions.unsubscribe();
        mvpView = null;
    }

    void signIn(Activity activity, LoginType loginType) {
        if (!NetworkUtil.isNetworkConnected(activity)) {
            mvpView.showDialog(DialogFactory.createSimpleOkDialog(activity, R.string.no_internet_dialog_title, R.string.no_internet_dialog_message));
            return;
        }
        switch (loginType) {
            case GOOGLE:
                Observable<GoogleApiClient> googleApiClientObservable = googleApiClientService.googleApiClient().cache().single();
                subscriptions.add(googleApiClientObservable
                        .map(GoogleApiClient::blockingConnect)
                        .compose(RxUtils.applyIoMainSchedulers())
                        .subscribe(connectionResult -> {
                            if (connectionResult.isSuccess()) {
                                subscriptions.add(googleApiClientObservable.subscribe(googleApiClient -> {
                                    googleLoginService.signIn(activity, googleApiClient);
                                }));
                            } else {
                                mvpView.showDialog(googleApiClientService.getConnectionErrorDialog(activity, connectionResult));
                            }
                        }));
                break;
            case FACEBOOK:
                facebookLoginService.signIn(activity);
                break;
        }
    }

    public void onActivityResult(Activity activity, ActivityResult activityResult) {
        Timber.e("OnActivityResult: %s", activityResult);

        Observable<GoogleApiClient> googleApiClientObservable = googleApiClientService.userConnectionResult(activityResult)
                .compose(RxUtils.applyIoMainSchedulers());


        Observable<GoogleSignInAccount> googleSignInAccountObservable = googleLoginService.signInResult(activityResult)
                .timeout(10, TimeUnit.SECONDS, SignInException.asErrorObservable("Timeout - no answer from Google login services"))
                .compose(RxUtils.applyIoMainSchedulers());

        Observable<Pair<Profile, AccessToken>> facebookSignInObservable = facebookLoginService.signInResult(activityResult)
                .timeout(10, TimeUnit.SECONDS, SignInException.asErrorObservable("Timeout - no answer from Facebook login services"))
                .compose(RxUtils.applyIoMainSchedulers());

        subscriptions.add(googleApiClientObservable
                .subscribe(googleApiClient -> {
                    googleLoginService.signIn(activity, googleApiClient);
                }, throwable -> {
                    Toast.makeText(activity, R.string.google_play_not_enabled_toast_text, Toast.LENGTH_LONG).show();
                }));

        subscriptions.add(googleSignInAccountObservable
                .subscribe(googleSignInAccount -> {
                    Timber.i("Successfully signed in with Google: %s", googleSignInAccount.toString());
                    User user = User.create(-1, LoginType.GOOGLE, googleSignInAccount.getDisplayName(),
                            ""/*No access token support yet, googleSignInAccount.getIdToken()*/);
                    mvpView.handleSignIn(user);
                }, throwable -> {
                    Dialog errorDialog = DialogFactory.createSimpleOkDialog(activity, R.string.sign_in_error_dialog_title,
                            throwable.getMessage());
                    mvpView.showDialog(errorDialog);
                }));


        subscriptions.add(facebookSignInObservable
                .subscribe(profileAccessTokenPair -> {
                    Profile profile = profileAccessTokenPair.first;
                    AccessToken accessToken = profileAccessTokenPair.second;
                    Timber.i("Successfully signed in with Facebook, Profile: %s , Access Token: %s", profile.toString(), accessToken.getToken());
                    User user = User.create(-1, LoginType.GOOGLE, profile.getName(), ""/*No access token support yet, accessToken.getToken()*/);
                    mvpView.handleSignIn(user);
                }, throwable -> {
                    Dialog errorDialog = DialogFactory.createSimpleOkDialog(activity, R.string.sign_in_error_dialog_title,
                            throwable.getMessage());
                    mvpView.showDialog(errorDialog);
                }));
    }

}
