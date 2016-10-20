package com.sample.signin.app.data.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * Container class bundling result of Activity stareted with {@link Activity#startActivityForResult(Intent, int)}
 * Created by maciek on 12.10.16.
 */

@AutoValue
public abstract class ActivityResult {

    public static ActivityResult createActivityResult(int requestCode, int resultCode, Intent data) {
        return new AutoValue_ActivityResult(requestCode, resultCode, data);
    }

    public abstract int requestCode();

    public abstract int resultCode();

    @Nullable
    public abstract Intent data();

}
