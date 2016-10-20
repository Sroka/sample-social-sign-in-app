package com.sample.signin.app.data.login.google;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.sample.signin.app.R;
import com.sample.signin.app.data.login.ActivityResult;
import com.sample.signin.app.injection.ApplicationContext;
import com.sample.signin.app.util.DialogFactory;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import rx.Observable;
import timber.log.Timber;

/**
 * Class managing Google Api Client connection
 * Created by srokowski.maciej@gmail.com on 17.10.16.
 */

public class GoogleApiClientService {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private Context applicationContext;

    @DebugLog
    @Inject
    public GoogleApiClientService(@ApplicationContext Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Returns Google api client wrapped in observable. Will return new api client with every subscribtion
     *
     * @return Observable with new api client
     */
    public Observable<GoogleApiClient> googleApiClient() {
        return Observable.fromCallable(() -> buildGoogleApiClient(applicationContext));
    }

    /**
     * Handles result of User enabling Google Play Services
     *
     * @param activityResult activity result for user enabling Google Play Services
     * @return Observable containing connected Google Api Client or error
     */
    //TODO Move dialog creation to activity
    public Observable<GoogleApiClient> userConnectionResult(ActivityResult activityResult) {
        if (activityResult.requestCode() == PLAY_SERVICES_RESOLUTION_REQUEST) {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext) == ConnectionResult.SUCCESS) {
                Timber.d("User enabled Google play services, retry sign in");
                return googleApiClient();
            } else {
                Timber.d("User didn't enable Google play services, abandon sign in request");
                return Observable.error(new IllegalStateException("User didn't enable google play services"));
            }
        } else {
            return Observable.empty();
        }
    }

    /**
     * Returns appropriate error dialog in case of Google api client connection failure
     *
     * @param activity         Current activity
     * @param connectionResult Google Api Client connection result
     * @return Dialog handling conneciton failure situation
     */
    public Dialog getConnectionErrorDialog(Activity activity, ConnectionResult connectionResult) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        boolean userResolvableError = googleApiAvailability.isUserResolvableError(connectionResult.getErrorCode());
        Timber.e("Play Services missing - user recoverable error: %b", userResolvableError);
        if (userResolvableError) {
            return googleApiAvailability.getErrorDialog(activity, connectionResult.getErrorCode(),
                    PLAY_SERVICES_RESOLUTION_REQUEST);
        } else {
            return DialogFactory.createSimpleOkDialog(activity, R.string.dialog_error_title,
                    R.string.error_message_play_services + connectionResult.getErrorMessage());
        }
    }

    private static GoogleApiClient buildGoogleApiClient(Context context) {
        Timber.i("Preparing GoogleApiClient");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
}
