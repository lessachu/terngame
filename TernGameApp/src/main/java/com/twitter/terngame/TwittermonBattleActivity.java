package com.twitter.terngame;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.terngame.util.NdefMessageParser;

public class TwittermonBattleActivity extends Activity
        implements View.OnClickListener, TwittermonDialogGridFragment.TwittermonGridSelectionListener {

    public static final String s_creature = "creature";
    public static final String s_new_creature = "new_creature";
    public static final int NEW_CREATURE_REQUEST_CODE = 1;

    private Session mSession;
    private String mCreature;
    private String mOpponentCreature;
    private TextView mTextView;
    private ImageView mImageView;
    private TextView mOpponentTextView;
    private ImageView mOpponentImageView;
    private Button mSelectButton;
    private Button mFightButton;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private TwittermonDialogGridFragment mFragment;

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

        mTextView = (TextView) findViewById(R.id.name_title);
        mTextView.setText(mCreature);

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mCreature));

        mSelectButton = (Button) findViewById(R.id.select_button);
        mSelectButton.setOnClickListener(this);

        mFightButton = (Button) findViewById(R.id.fight_button);
        mFightButton.setOnClickListener(this);

        mOpponentTextView = (TextView) findViewById(R.id.opponent_title);
        mOpponentImageView = (ImageView) findViewById(R.id.opponent_image);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TwittermonBattleResultActivity.NEW_CREATURE_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null) {
            mEarnedNewCreature = data.getBooleanExtra(s_new_creature, false);
        }
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
            mFightButton.setVisibility(View.VISIBLE);
        } else {
            mOpponentTextView.setText("???");
            // TODO: have a ???? image
//            mOpponentImageView.setImageDrawable();
            mFightButton.setVisibility(View.GONE);
        }

    }

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.select_button) {
            mFragment = TwittermonDialogGridFragment.newInstance();
            mFragment.show(getFragmentManager(), "dialog");
            mFragment.setSelectionListener(this);
        } else if (id == R.id.fight_button) {
            if (mCreature != null && mOpponentCreature != null) {
                Intent i = new Intent(this, TwittermonBattleResultActivity.class);
                i.putExtra(TwittermonBattleResultActivity.s_creature, mCreature);
                i.putExtra(TwittermonBattleResultActivity.s_oppCreature, mOpponentCreature);
                startActivity(i);
            }
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
            mOpponentCreature = new String(payload); // TODO: actually parse this

            Log.d("terngame", "Battle with " + mCreature + " and " + mOpponentCreature);
            setIntent(new Intent());

            Intent i = new Intent(this, TwittermonBattleResultActivity.class);
            i.putExtra(TwittermonBattleResultActivity.s_creature, mCreature);
            i.putExtra(TwittermonBattleResultActivity.s_oppCreature, mOpponentCreature);
            startActivityForResult(i, TwittermonBattleResultActivity.NEW_CREATURE_REQUEST_CODE);
        } else {
            Log.d("terngame", "action: " + intent.getAction());
        }
    }

    public void onTwittermonGridSelection(String creature) {
        mOpponentCreature = creature;
        mOpponentTextView.setText(creature);
        mOpponentImageView.setImageDrawable(mSession.getTwittermonImage(creature));

        mFightButton.setVisibility(View.VISIBLE);

        if (mFragment != null) {
            mFragment.dismiss();
            mFragment = null;
        }
    }
}

