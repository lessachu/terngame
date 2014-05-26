package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

// I think I actually just want this to be a fragment

public class TwittermonCollectDupeActivity extends Activity
        implements View.OnClickListener {

    static public String s_creature = "creature";

    private String mCreature;
    private ImageView mImageView;
    private Button mCollectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_collect_dupe);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCreature = extras.getString(s_creature);
        }
        Session s = Session.getInstance(this);

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        Drawable creatureImage = this.getResources().getDrawable(R.drawable.collect_fail);
        mImageView.setImageDrawable(creatureImage);
        mCollectionButton = (Button) findViewById(R.id.goto_collection_button);
        mCollectionButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.goto_collection_button) {
            Intent i = new Intent(this, TwittermonActivity.class);
            startActivity(i);
        }
    }
}
