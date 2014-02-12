package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by jchong on 2/11/14.
 */
public class AdminActivity extends Activity
        implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_activity);

        // TODO: populate the current puzzle spinner
        // TODO: hook up the puzzle and skip count number selectors
        // TODO: add UX to edit puzzle data
    }

    public void onClick(View view) {
        final int id = view.getId();

        Session s = Session.getInstance(this);
        if (id == R.id.admin_clear_all_button) {
            // TODO: show an "are you sure? prompt?
            // TODO: session should clear all data
        }
    }
}
