package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.twitter.terngame.data.PuzzleExtraInfo;

public class TwittermonCollectActivity extends Activity
        implements View.OnClickListener {

    private String mTwittermonName;

    private LinearLayout mCollectLayout;
    private LinearLayout mNotStartedLayout;

    private EditText mTrapCodeEdit;
    private Button mEnterButton;

    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: if the puzzle hasn't been started yet, use another layout

        setContentView(R.layout.twittermon_collect);

        mSession = Session.getInstance(this);

        mCollectLayout = (LinearLayout) findViewById(R.id.collect_layout);
        mNotStartedLayout = (LinearLayout) findViewById(R.id.not_started_layout);

        mTrapCodeEdit = (EditText) findViewById(R.id.trap_code_edit);
        mEnterButton = (Button) findViewById(R.id.collect_button);
        mEnterButton.setOnClickListener(this);
        mEnterButton.setEnabled(false);

        mTrapCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                mEnterButton.setEnabled(text.length() > 0);
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
    protected void onResume() {
        super.onResume();
        Session s = Session.getInstance(this);

        String curPuzzle = s.getCurrentPuzzleID();

        if (curPuzzle != null && curPuzzle.equals(PuzzleExtraInfo.s_twittermon)) {

            mCollectLayout.setVisibility(View.VISIBLE);
            mNotStartedLayout.setVisibility(View.GONE);
        } else {
            mCollectLayout.setVisibility(View.GONE);
            mNotStartedLayout.setVisibility(View.VISIBLE);
        }

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            mTwittermonName = new String(payload); // TODO: actually parse this
            setIntent(new Intent());
        }
    }

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.collect_button) {
            if (mSession.verifyTwittermonTrapCode(mTwittermonName,
                    mTrapCodeEdit.getText().toString())) {

                if (mSession.hasTwittermon(mTwittermonName)) {
                    Intent i = new Intent(this, TwittermonCollectDupeActivity.class);
                    i.putExtra(TwittermonCollectDupeActivity.s_creature, mTwittermonName);
                    startActivity(i);
                } else {
                    // TODO: enable this after we have display fixed
                    //                  mSession.collectTwittermon(mTwittermonName);

                    Intent i = new Intent(this, TwittermonCollectSucceedActivity.class);
                    i.putExtra(TwittermonCollectSucceedActivity.s_creature, mTwittermonName);
                    startActivity(i);
                }
            } else {
                Intent i = new Intent(this, TwittermonCollectFailActivity.class);
                startActivity(i);
            }
        }
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
            }
        } else {
            Log.d("terngame", "Unknown intent.");
            finish();
        }
        return msgs;
    }

}
