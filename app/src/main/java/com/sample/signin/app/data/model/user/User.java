package com.sample.signin.app.data.model.user;

import android.os.Parcelable;

import com.sample.signin.app.data.login.LoginType;
import com.google.auto.value.AutoValue;

/**
 * Class describing signed in User
 * Created by maciek on 19.09.16.
 */
@AutoValue
public abstract class User implements Parcelable {

    public static User create(long id, LoginType loginType, String name, String accessToken) {
        return new AutoValue_User(id, loginType, name, accessToken);
    }

    public abstract long id();

    public abstract LoginType loginType();

    public abstract String name();

    public abstract String accessToken();
}
