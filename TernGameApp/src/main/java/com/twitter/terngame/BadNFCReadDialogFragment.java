package com.twitter.terngame;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BadNFCReadDialogFragment extends DialogFragment {

    static BadNFCReadDialogFragment newInstance() {
        return new BadNFCReadDialogFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bad_nfc_read, container, false);

        final Activity activity = getActivity();
        if (activity != null) {
            final Context context = getActivity().getApplicationContext();

            // hook up the OK button
        } else {
            Log.d("terngame", "Activity is null");
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.bad_nfc_title);
        return dialog;
    }


}
