package com.twitter.terngame;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ChangeTeamNameDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private Button mButton;
    private EditText mTeamNameEdit;

    public interface TeamNameChangeListener {
        public void onNewTeamName(String teamName);
    }

    TeamNameChangeListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TeamNameChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TeamNameChangeListener");
        }
    }

    static ChangeTeamNameDialogFragment newInstance() {
        return new ChangeTeamNameDialogFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_team_name, container, false);

        final Activity activity = getActivity();
        if (activity != null) {
            mButton = (Button) view.findViewById(R.id.bad_read_ok);
            mButton.setOnClickListener(this);
            mButton.setEnabled(false);

            mTeamNameEdit = (EditText) view.findViewById(R.id.team_name_edit);
            mTeamNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable text) {
                    mButton.setEnabled(text.length() > 0);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });


        } else {
            Log.d("terngame", "Activity is null");
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Change your team name");
        return dialog;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.bad_read_ok) {
            mListener.onNewTeamName(mTeamNameEdit.getText().toString());
            dismiss();
        }
    }

}
