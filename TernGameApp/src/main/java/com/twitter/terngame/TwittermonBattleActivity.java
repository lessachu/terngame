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
        implements View.OnClickListener {

    public static final String s_creature = "creature";

    private Session mSession;
    private String mCreature;
    private String mOpponentCreature;
    private TextView mTextView;
    private ImageView mImageView;
    private Button mSelectButton;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;

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

        mSelectButton = (Button) findViewById(R.id.battle_button);
        mSelectButton.setOnClickListener(this);

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
    }

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.battle_button) {
            // bring up the selector grid
        }
    }

    public void onNewIntent(Intent intent) {

        Log.d("terngame", "in onNewIntent");
//        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //do something with tagFromIntent
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Log.d("terngame", "ACTION_NDEF_DISCOVERED");
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
            startActivity(i);
        } else {
            Log.d("terngame", "action: " + intent.getAction());
        }


    }

}

