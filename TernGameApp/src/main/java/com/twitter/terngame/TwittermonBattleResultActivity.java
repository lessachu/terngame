package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.NdefMessageParser;

public class TwittermonBattleResultActivity extends Activity {

    public static String s_creature = "creature";

    private Session mSession;
    private String mCreature;
    private String mOpponentCreature;
    private TextView mTextView;
    private ImageView mImageView;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_battle);

        mSession = Session.getInstance(this);
        PuzzleExtraInfo pei = mSession.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

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

        Log.d("terngame", "TwittermonBattleResult onCreate");

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Your device doesn't support NFC.",
                    Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Session s = Session.getInstance(this);

        String curPuzzle = s.getCurrentPuzzleID();

        Log.d("terngame", "TwittermonBattleResultActivity onResume");
/*
        if (curPuzzle != null && curPuzzle.equals(PuzzleExtraInfo.s_twittermon)) {

            mCollectLayout.setVisibility(View.VISIBLE);
            mNotStartedLayout.setVisibility(View.GONE);
        } else {
            mCollectLayout.setVisibility(View.GONE);
            mNotStartedLayout.setVisibility(View.VISIBLE);
        } */

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = NdefMessageParser.getNdefMessages(getIntent());

            if (messages == null) {
                Log.d("terngame", "TwittermonBattleResult: Unknown intent.");
                finish();
            }

            byte[] payload = messages[0].getRecords()[3].getPayload();
            mOpponentCreature = new String(payload); // TODO: actually parse this
            setIntent(new Intent());
        }
    }

}
