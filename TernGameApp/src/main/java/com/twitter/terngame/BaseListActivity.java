package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public abstract class BaseListActivity extends ListActivity
        implements Session.DataLoadedListener {

    protected LinearLayout mContent;
    protected LinearLayout mLoadingLayout;
    protected Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSession = Session.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mContent = (LinearLayout) findViewById(R.id.main_content);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);

        if (mSession.isDataLoaded(this)) {
            showUX();
        } else {
            showLoadingScreen();
        }
    }

    public void showLoadingScreen() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
    }

    public void showUX() {
        mLoadingLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    public void onDataLoaded() {
        showUX();
    }

}
