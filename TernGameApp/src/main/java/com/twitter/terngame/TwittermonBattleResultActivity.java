package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

public class TwittermonBattleResultActivity extends Activity
        implements View.OnClickListener {

    public static final String s_creature = "creature";
    public static final String s_oppCreature = "oppCreature";
    public static final int NEW_CREATURE_REQUEST_CODE = 1;

    private Session mSession;
    private String mCreature;
    private String mOpponentCreature;
    private TextView mTextView;
    private ImageView mImageView;
    private TextView mNameView;
    private ImageView mOppImageView;
    private TextView mOppNameView;
    private Button mHistory;
    private Boolean mEarnedNewCreature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_battle_result);

        Log.d("terngame", "In BattleResultActivity");

        mSession = Session.getInstance(this);
        PuzzleExtraInfo pei = mSession.getPuzzleExtraInfo();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCreature = extras.getString(s_creature);
            mOpponentCreature = extras.getString(s_oppCreature);
        } else {
            mCreature = "error";
            mOpponentCreature = "error";
            Log.e("terngame", "BattleResults called with no extras");
            finish();
        }

        Log.d("terngame", "BattleResultActivity between: " + mCreature + " and " + mOpponentCreature);

        mTextView = (TextView) findViewById(R.id.battle_result);

        int result = mSession.battleTwittermon(mCreature, mOpponentCreature);
        switch (result) {
            case TwittermonInfo.s_win:
                mTextView.setText("You win!");
                break;
            case TwittermonInfo.s_tie:
                mTextView.setText("It's a tie!");
                break;
            case TwittermonInfo.s_lose:
                mTextView.setText("You lose!");
                break;
        }

        mSession.logTwittermonBattle(mCreature, mOpponentCreature, result);

        mNameView = (TextView) findViewById(R.id.name_title);
        mNameView.setText(mCreature);

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mCreature));


        mOppNameView = (TextView) findViewById(R.id.opponent_title);
        mOppNameView.setText(mOpponentCreature);

        mOppImageView = (ImageView) findViewById(R.id.opponent_image);
        mOppImageView.setImageDrawable(mSession.getTwittermonImage(mOpponentCreature));

        mHistory = (Button) findViewById(R.id.battle_history);
        mHistory.setOnClickListener(this);

        TextView collectMsgView = (TextView) findViewById(R.id.collect_message_text);

        if (!mSession.hasTwittermon(mOpponentCreature)) {
            mSession.collectTwittermon(mOpponentCreature);
            mEarnedNewCreature = true;
            collectMsgView.setText(mOpponentCreature + " has been added to your collection!");
        } else {
            collectMsgView.setVisibility(View.GONE);
            mEarnedNewCreature = false;
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.battle_history) {
            Intent i = new Intent(this, TwittermonBattleHistoryActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra(TwittermonBattleActivity.s_new_creature, mEarnedNewCreature);
        setResult(RESULT_OK, output);
        super.onBackPressed();
    }
}
