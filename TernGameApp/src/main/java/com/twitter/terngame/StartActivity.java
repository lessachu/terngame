package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.terngame.util.EditTextWatcher;

public class StartActivity extends Activity
        implements View.OnClickListener, Session.LoginLoadedListener {

    private AppController mAppController;
    private Button mSignInButton;
    private EditText mTeamEditText;
    private EditText mPassEditText;
    private EditTextWatcher mTeamTextWatcher;
    private EditTextWatcher mPassTextWatcher;

    private boolean mDataLoaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataLoaded = false;
        mAppController = AppController.getInstance(this);

        Session s = Session.getInstance(this);
        if (s.isLoggedIn()) {
            goHome();
            return;
        }

        s.setLoginLoadedListener(this);

        setContentView(R.layout.start);

        mTeamEditText = (EditText) findViewById(R.id.team_name_edit);
        mPassEditText = (EditText) findViewById(R.id.password_edit);

        mSignInButton = (Button) findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(this);
        mSignInButton.setEnabled(false);

        mTeamTextWatcher = new EditTextWatcher(mTeamEditText, mSignInButton);
        mPassTextWatcher = new EditTextWatcher(mPassEditText, mSignInButton);

        mTeamTextWatcher.setSisterWatcher(mPassTextWatcher);
        mPassTextWatcher.setSisterWatcher(mTeamTextWatcher);

        mTeamEditText.addTextChangedListener(mTeamTextWatcher);
        mPassEditText.addTextChangedListener(mPassTextWatcher);
    }

    @Override
    public void onLoginLoaded() {
        mTeamTextWatcher.setReady(true);
        mPassTextWatcher.setReady(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Session.getInstance(this).isLoggedIn()) {
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

            if(Session.getInstance(this).login(mTeamEditText.getText().toString(),
                    mPassEditText.getText().toString())) {
                    startActivity(new Intent(this, MainActivity.class)
                                  .putExtra(Intent.EXTRA_INTENT,
                                  getIntent().getParcelableExtra(Intent.EXTRA_INTENT)));
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "I'm sorry, but that name and password combination is invalid.",
                        Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

}