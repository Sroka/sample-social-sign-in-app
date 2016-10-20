package com.sample.signin.app.ui.signin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.facebook.login.widget.LoginButton;
import com.sample.signin.app.R;
import com.sample.signin.app.data.login.ActivityResult;
import com.sample.signin.app.data.login.LoginType;
import com.sample.signin.app.data.model.user.User;
import com.sample.signin.app.ui.base.BaseActivity;
import com.sample.signin.app.ui.main.MainActivity;
import com.sample.signin.app.util.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SignInActivity extends BaseActivity implements SignInMvpView {

    private static final String EXTRA_POPUP_MESSAGE = "SignInActivity.EXTRA_POPUP_MESSAGE";
    private static final String EXTRA_POPUP_TITLE = "SignInActivity.EXTRA_POPUP_TITLE";

    @Inject
    SignInPresenter mSignInPresenter;

    @BindView(R.id.facebook_sign_in_button)
    Button facebookSignInButtonButton;
    @BindView(R.id.google_sign_in_button)
    Button googleSignInButton;

    public static Intent getStartIntent(Context context, boolean clearPreviousActivities) {
        Intent intent = new Intent(context, SignInActivity.class);
        if (clearPreviousActivities) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        return intent;
    }

    // popUpMessage will show on a Dialog as soon as the Activity opens
    public static Intent getStartIntent(Context context,
                                        boolean clearPreviousActivities,
                                        String popUpTitle, String popUpMessage) {
        Intent intent = getStartIntent(context, clearPreviousActivities);
        intent.putExtra(EXTRA_POPUP_TITLE, popUpTitle);
        intent.putExtra(EXTRA_POPUP_MESSAGE, popUpMessage);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Timber.d("OnCreate");
        activityComponent().inject(this);
        ButterKnife.bind(this);
        mSignInPresenter.attachView(this);
        String popUpTitle = getIntent().getStringExtra(EXTRA_POPUP_TITLE);
        String popUpMessage = getIntent().getStringExtra(EXTRA_POPUP_MESSAGE);
        if (popUpMessage != null) {
            DialogFactory.createSimpleOkDialog(this, popUpTitle, popUpMessage).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSignInPresenter.detachView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        ActivityResult activityResult = ActivityResult.createActivityResult(requestCode, resultCode, intent);
        mSignInPresenter.onActivityResult(this, activityResult);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /*****
     * MVP View methods implementation
     *****/

    @Override
    public void handleSignIn(User user) {
        Intent startIntent = MainActivity.getStartIntent(this, user);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startIntent);
    }

    @Override
    //TODO Move dialog creation to activity
    public void showDialog(Dialog dialog) {
        dialog.show();
    }

    @OnClick(R.id.google_sign_in_button)
    public void signInWithGoogle() {
        mSignInPresenter.signIn(this, LoginType.GOOGLE);
    }

    @OnClick(R.id.facebook_sign_in_button)
    public void signInWithFacebook() {
        mSignInPresenter.signIn(this, LoginType.FACEBOOK);
    }
}
