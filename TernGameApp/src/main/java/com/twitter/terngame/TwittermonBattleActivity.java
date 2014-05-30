package com.twitter.terngame;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.nio.charset.Charset;

public class TwittermonBattleActivity extends Activity {

    public static String s_creature = "creature";

    private Session mSession;
    private String mCreature;
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

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Your device doesn't support NFC.",
                    Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

        // Add a URI to for filtering purposes
        NdefRecord uriRecord = NdefRecord.createUri("http://terngame");

        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(getPackageName());

        // Record with actual data we care about
        NdefRecord dataRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                new String("application/" + getPackageName()).getBytes(Charset.forName("US-ASCII")),
                null, mCreature.getBytes());

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{uriRecord, appRecord, dataRecord});
        mNfcAdapter.setNdefPushMessage(ndefMessage, this);
    }


}
