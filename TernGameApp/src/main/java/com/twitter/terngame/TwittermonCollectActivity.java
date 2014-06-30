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

    public final static int s_show_success = 1;
    public final static int s_show_fail = 2;
    public final static int s_show_dupe = 3;

    private String mTwittermonName;
    private Drawable mTwittermonImage;

    // this is pretty hacky
    private LinearLayout mCollectLayout;
    private LinearLayout mNotStartedLayout;
    private LinearLayout mBadNFCLayout;
    private LinearLayout mCollectResultLayout;

    private ImageView mCreatureImage;
    private TextView mCreatureName;
    private EditText mTrapCodeEdit;
    private TextView mCollectResultPrompt;
    private Button mEnterButton;
    private Button mNYSOkButton;
    private Button mBadNFCButton;
    private Button mCollectOKButton;

    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_collect);

        mSession = Session.getInstance(this);

        mCollectLayout = (LinearLayout) findViewById(R.id.collect_layout);
        mNotStartedLayout = (LinearLayout) findViewById(R.id.not_started_layout);
        mBadNFCLayout = (LinearLayout) findViewById(R.id.bad_nfc_layout);
        mCollectResultLayout = (LinearLayout) findViewById(R.id.collect_result_layout);

        mCreatureImage = (ImageView) findViewById(R.id.twittermon_image);
        mCreatureName = (TextView) findViewById(R.id.twittermon_text);

        mTrapCodeEdit = (EditText) findViewById(R.id.trap_code_edit);
        mCollectResultPrompt = (TextView) findViewById(R.id.collect_result_prompt);
        mEnterButton = (Button) findViewById(R.id.collect_button);
        mEnterButton.setOnClickListener(this);
        mEnterButton.setEnabled(false);

        mNYSOkButton = (Button) findViewById(R.id.not_started_ok);
        mNYSOkButton.setOnClickListener(this);

        mBadNFCButton = (Button) findViewById(R.id.bad_read_ok);
        mBadNFCButton.setOnClickListener(this);

        mCollectOKButton = (Button) findViewById(R.id.collect_result_ok);
        mCollectOKButton.setOnClickListener(this);

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
            mTwittermonImage = mSession.getTwittermonImage(mTwittermonName);

            if (mTwittermonImage != TwittermonInfo.mDefaultPict) {
                setIntent(new Intent());
                mCreatureName.setText(mTwittermonName);
                mCreatureImage.setImageDrawable(mTwittermonImage);

                if (curPuzzle != null && curPuzzle.equals(PuzzleExtraInfo.s_twittermon)) {
                    showCollectUX();
                } else {
                    showPuzzleNotStartedUX();
                }
            } else {
                showBadReadUX();
            }
        }
    }

    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.collect_button:
                if (mSession.verifyTwittermonTrapCode(mTwittermonName,
                        mTrapCodeEdit.getText().toString())) {

                    if (mSession.hasTwittermon(mTwittermonName)) {
                        showCollectResult(s_show_dupe);
                    } else {
                        mSession.collectTwittermon(mTwittermonName);
                        showCollectResult(s_show_success);
                    }
                } else {
                    showCollectResult(s_show_fail);
                }
                break;
            case R.id.not_started_ok:
            case R.id.bad_read_ok:
            case R.id.collect_result_ok:
                // TODO - why is this causing a flash on dismiss?
                this.finish();
        }
    }

    private void showCollectUX() {
        setTitle(R.string.collect_title);
        mCollectLayout.setVisibility(View.VISIBLE);
        mNotStartedLayout.setVisibility(View.GONE);
        mBadNFCLayout.setVisibility(View.GONE);
        mCollectResultLayout.setVisibility(View.GONE);
    }

    private void showBadReadUX() {
        setTitle(R.string.bad_nfc_title);
        mCollectLayout.setVisibility(View.GONE);
        mNotStartedLayout.setVisibility(View.GONE);
        mBadNFCLayout.setVisibility(View.VISIBLE);
        mCollectResultLayout.setVisibility(View.GONE);
    }

    private void showPuzzleNotStartedUX() {
        setTitle(R.string.too_soon_title);
        mCollectLayout.setVisibility(View.GONE);
        mNotStartedLayout.setVisibility(View.VISIBLE);
        mBadNFCLayout.setVisibility(View.GONE);
        mCollectResultLayout.setVisibility(View.GONE);
    }

    private void showCollectResult(int action) {
        String prompt = "";
        switch (action) {
            case s_show_success:
                setTitle(R.string.collect_succeed_title);
                prompt = mTwittermonName + " has been added to your collection!";
                break;
            case s_show_dupe:
                setTitle(R.string.collect_dupe_title);
                prompt = "You already have " + mTwittermonName + " in your collection.";
                break;
            case s_show_fail:
                setTitle(R.string.collect_fail_title);
                prompt = "Doh! " + mTwittermonName + " got away!  Better luck next time!";
                break;
        }
        mCollectResultPrompt.setText(prompt);

        mCollectLayout.setVisibility(View.GONE);
        mNotStartedLayout.setVisibility(View.GONE);
        mBadNFCLayout.setVisibility(View.GONE);
        mCollectResultLayout.setVisibility(View.VISIBLE);
    }

}
