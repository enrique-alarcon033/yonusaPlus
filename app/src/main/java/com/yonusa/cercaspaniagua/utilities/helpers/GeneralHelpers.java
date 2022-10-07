package com.yonusa.cercaspaniagua.utilities.helpers;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.yonusa.cercaspaniagua.R;

public class GeneralHelpers {

    public static void singleMakeAlert(Context context, String title, String msg){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(context.getString(R.string.ok_alert_text), null)
                .show();
    }



}
