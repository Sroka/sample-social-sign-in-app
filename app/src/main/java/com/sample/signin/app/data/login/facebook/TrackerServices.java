package com.sample.signin.app.data.login.facebook;

import com.sample.signin.app.data.login.SignInException;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import timber.log.Timber;

/**
 * Created by srokowski.maciej@gmail.com on 15.10.16.
 */

public class TrackerServices {

    /**
     * Provides stream for listening for Facebook login result.
     *
     * @param callbackManager Callback manager on which onActivityResult will be called.
     * @return Observable providing {@link LoginResult} in case of success, empty one in case of cancelation, throwing {@link SignInException} in case
     * of failure or empty one in case of cancellation
     */
    public static Observable<LoginResult> getLoginResult(CallbackManager callbackManager) {
        AsyncSubject<LoginResult> loginResultAsyncSubject = AsyncSubject.create();

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Timber.i("Facebook login successful %s", loginResult.toString());
                loginResultAsyncSubject.onNext(loginResult);
                loginResultAsyncSubject.onCompleted();
            }

            @Override
            public void onCancel() {
                Timber.i("Facebook login onCancel");
                loginResultAsyncSubject.onCompleted();
            }

            @Override
            public void onError(FacebookException error) {
                Timber.i("Facebook login errored: %s", error.toString());
                loginResultAsyncSubject.onError(new SignInException(error.getLocalizedMessage()));
                loginResultAsyncSubject.onCompleted();
            }
        });

        return loginResultAsyncSubject
                .subscribeOn(Schedulers.io())
                .asObservable();
    }

    /**
     * Provides stream for listening for Facebook login result.
     *
     * @return Observable providing {@link Profile} in ces of success. As this is passively listening user has te decide himself when he has waited long
     * enough.
     */
    public static Observable<Profile> getProfile() {
        AsyncSubject<Profile> profileAsyncSubject = AsyncSubject.create();

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Timber.i("New Facebook delivered %s", currentProfile.toString());
                profileAsyncSubject.onNext(currentProfile);
                profileAsyncSubject.onCompleted();
            }
        };
        return profileAsyncSubject.doOnCompleted(profileTracker::stopTracking)
                .subscribeOn(Schedulers.io())
                .asObservable();
    }
}
