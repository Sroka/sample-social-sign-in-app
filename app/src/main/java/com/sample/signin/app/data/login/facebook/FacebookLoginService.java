package com.sample.signin.app.data.login.facebook;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.params.Face;
import android.util.Pair;

import com.sample.signin.app.data.login.ActivityResult;
import com.sample.signin.app.injection.ApplicationContext;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Class handling Facebook sign in
 * Created by srokowski.maciej@gmail.com on 15.10.16.
 */

public class FacebookLoginService {

    private static final String PUBLIC_PROFILE_PERMISSION = "public_profile";
    private static final String EMAIL_PERMISSION = "email";
    private final Context applicationContext;

    @Inject
    public FacebookLoginService(@ApplicationContext Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Initializes Facebook login procedure
     *
     * @param activity Current Activity
     */
    public void signIn(Activity activity) {
        FacebookSdk.sdkInitialize(applicationContext);
        //TODO Decide if you need logs in debug version. They were just annoying for me
        FacebookSdk.setIsDebugEnabled(false);
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(PUBLIC_PROFILE_PERMISSION, EMAIL_PERMISSION));
    }

    /**
     * Initializes Facebook sign in procedure
     */
    public void signOut() {
        FacebookSdk.sdkInitialize(applicationContext);
        LoginManager.getInstance().logOut();
    }

    /**
     * Handles the result of Facebook sign in.
     *
     * @param activityResult Result of Facebook sign in activity
     * @return Observable containing {@link Profile} and {@link AccessToken}, error or empty one in case of cancellation
     */
    public Observable<Pair<Profile, AccessToken>> signInResult(ActivityResult activityResult) {
        if (FacebookSdk.isFacebookRequestCode(activityResult.requestCode())) {
            CallbackManager callbackManager = CallbackManager.Factory.create();
            Observable<Pair<Profile, AccessToken>> loginResultObservable = TrackerServices.getLoginResult(callbackManager)
                    .map(loginResult -> Pair.create(Profile.getCurrentProfile(), loginResult.getAccessToken()));
            callbackManager.onActivityResult(activityResult.requestCode(), activityResult.resultCode(), activityResult.data());
            return loginResultObservable;
        } else {
            return Observable.empty();
        }
    }
}
