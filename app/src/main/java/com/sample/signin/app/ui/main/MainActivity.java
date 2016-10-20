package com.sample.signin.app.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sample.signin.app.R;
import com.sample.signin.app.data.model.user.User;
import com.sample.signin.app.ui.base.BaseActivity;
import com.sample.signin.app.ui.signin.SignInActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    public static final String USER_EXTRA = "USER_EXTRA";
    private User user;
    @Inject
    MainPresenter mMainPresenter;

    public static Intent getStartIntent(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USER_EXTRA, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter.attachView(this);
        user = getIntent().getParcelableExtra(USER_EXTRA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                mMainPresenter.signOut(user.loginType());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*****
     * MVP View methods implementation
     *****/

    @Override
    public void onSignedOut() {
        startActivity(SignInActivity.getStartIntent(this, true));
    }

    @Override
    public void showDialog(Dialog dialog) {
        dialog.show();
    }
}
