package com.sample.signin.app.util;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.sample.signin.app.R;

public class DialogFactory {

    public static Dialog createSimpleOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkDialog(Context context,
                                              @StringRes int titleResource,
                                              @StringRes int messageResource) {
        return createSimpleOkDialog(context, context.getString(titleResource), context.getString(messageResource));
    }

    public static Dialog createSimpleOkDialog(Context context,
                                              String title,
                                              @StringRes int messageResource) {
        return createSimpleOkDialog(context, title, context.getString(messageResource));
    }

    public static Dialog createSimpleOkDialog(Context context,
                                              @StringRes int titleResource,
                                              String messageResource) {
        return createSimpleOkDialog(context, context.getString(titleResource), messageResource);
    }

    public static Dialog createSimpleOkDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkDialog(Context context,
                                              @StringRes int messageResource) {

        return createSimpleOkDialog(context, context.getString(messageResource));
    }

}
