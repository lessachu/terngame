package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.Session;
import com.twitter.terngame.AppController;
import com.twitter.terngame.MainActivity;
import com.twitter.terngame.util.EditTextWatcher;

public class StartActivity extends Activity
    implements View.OnClickListener {

    private AppController mAppController;
    private Button mSignInButton;
    private EditText mTeamEditText;
    private EditText mPassEditText;
    // these probably don't have to be member variables
    private EditTextWatcher mTeamTextWatcher;
    private EditTextWatcher mPassTextWatcher;

    /**
     * Go to the signed-out screen
     *
     * @param activity The current activity
     */
    public static void toStart(Activity activity) {
        activity.startActivity(new Intent(activity, StartActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        activity.finish();
    }

    /**
     * Go to the signed-out screen
     *
     * @param activity The current activity
     * @param activityIntent The intent which started the activity
     */
    public static void toStart(Activity activity, Intent activityIntent) {
        activity.startActivity(new Intent(activity, StartActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(Intent.EXTRA_INTENT, activityIntent));
        activity.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AppController appController = AppController.getInstance(this);
        mAppController = appController;

        if (Session.getInstance(this).isLoggedIn()) {
            goHome();
            return;
        }

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
    protected void onStart() {
        super.onStart();

        if (Session.getInstance(this).isLoggedIn()) {
            goHome();
            return;
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
        // I actually don't think this check is needed
        if (id == R.id.sign_in) {

            if(Session.getInstance(this).login(mTeamEditText.getText().toString(),
                    mPassEditText.getText().toString())) {
                    startActivity(new Intent(this, MainActivity.class)
                                  .putExtra(Intent.EXTRA_INTENT,
                                  getIntent().getParcelableExtra(Intent.EXTRA_INTENT)));
            } else {
                // show an error message toast
                Toast toast = Toast.makeText(getApplicationContext(),
                        "I'm sorry, but that name and password combination is invalid.",
                        Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

}