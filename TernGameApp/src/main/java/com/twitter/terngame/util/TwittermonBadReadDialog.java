package com.twitter.terngame.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by jchong on 6/26/14.
 */
public class TwittermonBadReadDialog {

    static public void ShowNFCBadReadDialog(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Failed to read Twittermon Name");
        alertDialogBuilder
                .setMessage("Try holding the tag against the phone for a little longer.")
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
