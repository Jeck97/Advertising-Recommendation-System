package com.utem.ftmk.ws2.arsconsumer.utils;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class DialogUtil {

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(@NonNull Context context, @StringRes int message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(message));
        progressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
