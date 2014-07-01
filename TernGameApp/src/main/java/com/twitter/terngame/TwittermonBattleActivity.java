package com.twitter.terngame;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.NdefMessageParser;

public class TwittermonBattleActivity extends Activity
        implements View.OnClickListener, TwittermonDialogGridFragment.TwittermonGridSelectionListener {

    public static final String s_creature = "creature";
    public static final int NEW_CREATURE_REQUEST_CODE = 1;

    private Session mSession;
    private String mCreature;
    private String mOpponentCreature;
    private TextView mTitleText;
    private TextView mPromptText;
    private TextView mInstructionText;
    private TextView mCreatureTextView;
    private ImageView mCreatureImageView;
    private TextView mOpponentTextView;
    private ImageView mOpponentImageView;
    private Button mSelectButton;
    private Button mBattleHistoryButton;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private TwittermonDialogGridFragment mFragment;
    private BadNFCReadDialogFragment mBadNFCFragment;

    private Boolean mEarnedNewCreature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_battle);

        mSession = Session.getInstance(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCreature = extras.getString(s_creature);
        } else {
            mCreature = "error";
        }

        mTitleText = (TextView) findViewById(R.id.battle_title);
        mPromptText = (TextView) findViewById(R.id.battle_prompt);
        mInstructionText = (TextView) findViewById(R.id.battle_instruction);

        final RelativeLayout creatureLayout = (RelativeLayout) findViewById(R.id.creature_layout);
        mCreatureTextView = (TextView) creatureLayout.findViewById(R.id.twittermon_text);
        mCreatureTextView.setText(mCreature);

        mCreatureImageView = (ImageView) creatureLayout.findViewById(R.id.twittermon_image);
        mCreatureImageView.setImageDrawable(mSession.getTwittermonImage(mCreature));

        mSelectButton = (Button) findViewById(R.id.select_button);
        mSelectButton.setOnClickListener(this);

        mBattleHistoryButton = (Button) findViewById(R.id.battle_history_button);
        mBattleHistoryButton.setOnClickListener(this);

        final RelativeLayout opponentLayout = (RelativeLayout) findViewById(R.id.opponent_layout);
        mOpponentTextView = (TextView) opponentLayout.findViewById(R.id.twittermon_text);
        mOpponentImageView = (ImageView) opponentLayout.findViewById(R.id.twittermon_image);

        mOpponentTextView.setText("???");
        mOpponentImageView.setImageDrawable(getResources().getDrawable(R.drawable.unknown));

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mIntentFilters = new IntentFilter[]{ndef};

        mEarnedNewCreature = false;
    }

    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra(TwittermonActivity.s_new_creature, mEarnedNewCreature);
        setResult(RESULT_OK, output);
        super.onBackPressed();
    }

    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            Log.d("terngame", "Disable Foreground Dispatch");
            mAdapter.disableForegroundDispatch(this);
        }
    }

    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            Log.d("terngame", "Enable Foreground Dispatch");
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, null);
        }

        if (mOpponentCreature != null) {
            mOpponentTextView.setText(mOpponentCreature);
            mOpponentImageView.setImageDrawable(mSession.getTwittermonImage(mOpponentCreature));
        } else {
            mOpponentTextView.setText("???");
//            mOpponentImageView.setImageDrawable();
        }

    }

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.select_button) {
            mFragment = TwittermonDialogGridFragment.newInstance();
            mFragment.show(getFragmentManager(), "dialog");
            mFragment.setSelectionListener(this);
        } else if (id == R.id.battle_history_button) {
            Intent i = new Intent(this, TwittermonBattleHistoryActivity.class);
            startActivity(i);
        }
    }

    public void onNewIntent(Intent intent) {

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] messages = NdefMessageParser.getNdefMessages(intent);

            if (messages == null) {
                Log.d("terngame", "TwittermonBattleActivity: Unknown Intent");
                finish();
            }

            byte[] payload = messages[0].getRecords()[0].getPayload();
            String creature = new String(payload);

            Drawable image = mSession.getTwittermonImage(creature);
            if (image != TwittermonInfo.mDefaultPict) {
                mOpponentCreature = creature;

                Log.d("terngame", "Battle with " + mCreature + " and " + mOpponentCreature);
                setIntent(new Intent());

                doBattle();
            } else {
                mBadNFCFragment = BadNFCReadDialogFragment.newInstance();
                mBadNFCFragment.show(getFragmentManager(), "dialog");
            }
        } else {
            Log.d("terngame", "action: " + intent.getAction());
        }
    }

    public void onTwittermonGridSelection(String creature) {
        mOpponentCreature = creature;
        mOpponentTextView.setText(creature);
        mOpponentImageView.setImageDrawable(mSession.getTwittermonImage(creature));

        if (mFragment != null) {
            mFragment.dismiss();
            mFragment = null;
        }

        doBattle();
    }

    public void doBattle() {

        int result = mSession.battleTwittermon(mCreature, mOpponentCreature);
        switch (result) {
            case TwittermonInfo.s_win:
                mPromptText.setText("You win!");
                break;
            case TwittermonInfo.s_tie:
                mPromptText.setText("It's a tie!");
                break;
            case TwittermonInfo.s_lose:
                mPromptText.setText("You lose!");
                break;
        }

        mSession.logTwittermonBattle(mCreature, mOpponentCreature, result);

        if (!mSession.hasTwittermon(mOpponentCreature)) {
            mSession.collectTwittermon(mOpponentCreature);
            mEarnedNewCreature = true;

            CollectionResultDialogFragment dialogFragment =
                    CollectionResultDialogFragment.newInstance(mOpponentCreature);
            dialogFragment.show(getFragmentManager(), "dialog");
        } else {
            mEarnedNewCreature = false;
        }
        hideBattleUX();
    }

    public void hideBattleUX() {
        mTitleText.setText(R.string.battle_result_title);
        mSelectButton.setVisibility(View.GONE);
        if (mEarnedNewCreature) {
            mInstructionText.setText(mCreature + " has been added to your collection!");
        } else {
            mInstructionText.setVisibility(View.GONE);
        }
    }
}

