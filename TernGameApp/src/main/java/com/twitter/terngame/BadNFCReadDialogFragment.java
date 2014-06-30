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
import android.widget.Button;

public class BadNFCReadDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private Button mButton;

    static BadNFCReadDialogFragment newInstance() {
        return new BadNFCReadDialogFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bad_nfc_read, container, false);

        final Activity activity = getActivity();
        if (activity != null) {
            final Context context = getActivity().getApplicationContext();

            mButton = (Button) view.findViewById(R.id.bad_read_ok);
            mButton.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.bad_read_ok) {
            dismiss();
        }
    }

}
