package com.sample.signin.app.data.login.google;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.sample.signin.app.data.login.ActivityResult;
import com.sample.signin.app.data.login.SignInException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Class handling Google Login
 * Created by maciek on 07.10.16.
 */

public class GoogleLoginService {

    private static final int RC_SIGN_IN = 1;

    @Inject
    public GoogleLoginService() {
    }

    /**
     * Starts sing in procedure
     *
     * @param activity        Current Activity
     * @param googleApiClient Connected Google Api Client
     */
    public void signIn(final Activity activity, GoogleApiClient googleApiClient) {
        Timber.i("Request GoogleApiClient");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Starts Sign Out procedure
     *
     * @param googleApiClient Connected Google Api Client
     * @return Observable containing Sign Out Status. Observable will block, call {@link Observable#timeout(Func0, Func1)} to interrupt
     */
    public Observable<Status> signOut(GoogleApiClient googleApiClient) {
        return Observable.fromCallable(() -> signOutBlocking(googleApiClient));
    }

    /**
     * Signs out
     *
     * @param googleApiClient Connected Google Api Client
     * @return Sign out status. Blocking method
     */
    public Status signOutBlocking(GoogleApiClient googleApiClient) {
        return Auth.GoogleSignInApi.signOut(googleApiClient).await();
    }

    /**
     * Result of Google Sign in procedure
     *
     * @param activityResult Activity result from Google Sign in
     * @return Observable containing {@link GoogleSignInAccount}, error or empty one in case of cancellation.
     */
    public Observable<GoogleSignInAccount> signInResult(ActivityResult activityResult) {
        if (activityResult.requestCode() == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(activityResult.data());
            int statusCode = result.getStatus().getStatusCode();
            Timber.d("Sign in result: %s", GoogleSignInStatusCodes.getStatusCodeString(statusCode));
            switch (statusCode) {
                case CommonStatusCodes.SUCCESS:
                case CommonStatusCodes.SUCCESS_CACHE:
                    GoogleSignInAccount account = result.getSignInAccount();
                    return Observable.just(account);
                case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                    return Observable.empty();
                default:
                    String errorMessage = GoogleSignInStatusCodes.getStatusCodeString(statusCode);
                    return Observable.error(new SignInException(errorMessage));
            }
        } else {
            return Observable.empty();
        }
    }
}
