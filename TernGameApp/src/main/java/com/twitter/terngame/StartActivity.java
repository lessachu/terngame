package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class StartActivity extends Activity
        implements View.OnClickListener {

    private Button mSignInButton;
    private EditText mTeamEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        Session s = Session.getInstance(this);
        if (s.getTeamName().length() != 0) {
            goHome();
            return;
        }

        setContentView(R.layout.start);

        mTeamEditText = (EditText) findViewById(R.id.team_name_edit);

        mSignInButton = (Button) findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(this);
        mSignInButton.setEnabled(false);

        mTeamEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                if (text.length() > 0) {
                    mSignInButton.setEnabled(true);
                } else {
                    mSignInButton.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Session.getInstance(this).getTeamName().length() != 0) {
            goHome();
        }
    }

    protected void goHome() {
        Activity activity = getParent();
        if (activity == null) {
            activity = this;
        }

        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        activity.finish();
    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }

    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.sign_in) {
            Session.getInstance(this).login(mTeamEditText.getText().toString());
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra(Intent.EXTRA_INTENT,
                            getIntent().getParcelableExtra(Intent.EXTRA_INTENT)));
        }
    }

}