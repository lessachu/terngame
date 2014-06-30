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
import android.widget.TextView;

public class CollectionResultDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private String mCreatureName;
    private Button mButton;

    static CollectionResultDialogFragment newInstance(String creature) {
        return new CollectionResultDialogFragment(creature);
    }

    private CollectionResultDialogFragment(String creature) {
        mCreatureName = creature;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.twittermon_collect_result, container, false);

        final Activity activity = getActivity();
        if (activity != null) {
            final Context context = getActivity().getApplicationContext();

            TextView prompt = (TextView) view.findViewById(R.id.collect_result_prompt);
            prompt.setText(mCreatureName + " has been added to your collection!");

            mButton = (Button) view.findViewById(R.id.collect_result_ok);
            mButton.setOnClickListener(this);
        } else {
            Log.d("terngame", "Activity is null");
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.collect_succeed_title);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.collect_result_ok) {
            dismiss();
        }
    }

}
