package com.twitter.terngame;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TwittermonCollectFailActivity extends Activity
        implements View.OnClickListener {

    private ImageView mImageView;
    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_collect_fail);

        mImageView = (ImageView) findViewById(R.id.twittermon_image_fail);
        Drawable failImage = this.getResources().getDrawable(R.drawable.collect_fail);
        mImageView.setImageDrawable(failImage);
        mRetryButton = (Button) findViewById(R.id.collect_try_again);
        mRetryButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.collect_try_again) {
            super.finish();
        }
    }
}
