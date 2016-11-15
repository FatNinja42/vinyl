package com.example.gabriel.vinyl.util;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Gabriel on 11/15/2016.
 */

public class DialogUtils {
    public static void showError(Context context, Exception e) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(e.getMessage())
                .setCancelable(true)
                .create()
                .show();
    }
}
