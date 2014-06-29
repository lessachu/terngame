package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.NdefMessageParser;

public class TwittermonCollectActivity extends Activity
        implements View.OnClickListener {

    private String mTwittermonName;

    // this is pretty hacky
    private LinearLayout mCollectLayout;
    private LinearLayout mNotStartedLayout;
    private LinearLayout mBadNFCLayout;

    private ImageView mCreatureImage;
    private TextView mCreatureName;
    private EditText mTrapCodeEdit;
    private Button mEnterButton;
    private Button mNYSOkButton;
    private Button mBadNFCButton;

    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_collect);

        mSession = Session.getInstance(this);

        mCollectLayout = (LinearLayout) findViewById(R.id.collect_layout);
        mNotStartedLayout = (LinearLayout) findViewById(R.id.not_started_layout);
        mBadNFCLayout = (LinearLayout) findViewById(R.id.bad_nfc_layout);

        mCreatureImage = (ImageView) findViewById(R.id.twittermon_image);
        mCreatureName = (TextView) findViewById(R.id.twittermon_text);

        mTrapCodeEdit = (EditText) findViewById(R.id.trap_code_edit);
        mEnterButton = (Button) findViewById(R.id.collect_button);
        mEnterButton.setOnClickListener(this);
        mEnterButton.setEnabled(false);

        mNYSOkButton = (Button) findViewById(R.id.not_started_ok);
        mNYSOkButton.setOnClickListener(this);

        mBadNFCButton = (Button) findViewById(R.id.bad_read_ok);
        mBadNFCButton.setOnClickListener(this);

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

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = NdefMessageParser.getNdefMessages(getIntent());

            if (messages == null) {
                Log.d("terngame", "TwittermonCollectActivity: Unknown Intent");
                finish();
            }

            byte[] payload = messages[0].getRecords()[0].getPayload();
            mTwittermonName = new String(payload);
            Drawable image = mSession.getTwittermonImage(mTwittermonName);

            if (image != TwittermonInfo.mDefaultPict) {
                setIntent(new Intent());
                mCreatureName.setText(mTwittermonName);
                mCreatureImage.setImageDrawable(image);

                if (curPuzzle != null && curPuzzle.equals(PuzzleExtraInfo.s_twittermon)) {
                    setTitle(R.string.collect_title);
                    mCollectLayout.setVisibility(View.VISIBLE);
                    mNotStartedLayout.setVisibility(View.GONE);
                    mBadNFCLayout.setVisibility(View.GONE);
                } else {
                    setTitle(R.string.too_soon_title);
                    mCollectLayout.setVisibility(View.GONE);
                    mNotStartedLayout.setVisibility(View.VISIBLE);
                    mBadNFCLayout.setVisibility(View.GONE);
                }
            } else {
                setTitle(R.string.bad_nfc_title);
                mCollectLayout.setVisibility(View.GONE);
                mNotStartedLayout.setVisibility(View.GONE);
                mBadNFCLayout.setVisibility(View.VISIBLE);
                // TODO set the return result, finish the activity and let the previous one show the dialog
            }
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
                    mSession.collectTwittermon(mTwittermonName);
                    Intent i = new Intent(this, TwittermonCollectSucceedActivity.class);
                    i.putExtra(TwittermonCollectSucceedActivity.s_creature, mTwittermonName);
                    startActivity(i);
                }
            } else {
                Intent i = new Intent(this, TwittermonCollectFailActivity.class);
                startActivity(i);
            }
        } else if (id == R.id.not_started_ok) {
            // TODO - why is this causing a flash on dismiss?
            this.finish();
        } else if (id == R.id.bad_read_ok) {
            this.finish();
        }
    }

}
