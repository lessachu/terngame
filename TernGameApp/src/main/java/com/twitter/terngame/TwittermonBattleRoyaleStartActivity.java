package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.terngame.util.TwittermonBattleRoyalHelper;

public class TwittermonBattleRoyaleStartActivity extends Activity
        implements View.OnClickListener {

    public static final String s_correct = "correct";

    private Button mStart;
    private TextView mResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale_start);

        mResultView = (TextView) findViewById(R.id.battle_royale_result);
        mResultView.setVisibility(View.GONE);

        mStart = (Button) findViewById(R.id.start_finale);
        mStart.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TwittermonBattleRoyaleActivity.RESULT_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null) {
            int numCorrect = data.getIntExtra(s_correct, 0);
            if (numCorrect < TwittermonBattleRoyalHelper.s_total) {
                String text = "INCORRECT. TRY AGAIN.";
                mResultView.setText(text);
                mResultView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.start_finale) {
            Intent i = new Intent(this, TwittermonBattleRoyaleActivity.class);
            startActivityForResult(i, TwittermonBattleRoyaleActivity.RESULT_REQUEST_CODE);
        }
    }

}
