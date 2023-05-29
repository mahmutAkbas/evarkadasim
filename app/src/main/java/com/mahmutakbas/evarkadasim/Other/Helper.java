package com.mahmutakbas.evarkadasim.Other;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class Helper {


    public static String IsNull(String value) {
        return value.isEmpty() ? "" : value;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        return progressDialog;
    }
}
