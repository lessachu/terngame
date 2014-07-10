package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public abstract class BaseListActivity extends ListActivity
        implements Session.DataLoadedListener {

    private static final String s_teamname = "teamname";

    protected LinearLayout mContent;
    protected LinearLayout mLoadingLayout;
    protected Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSession = Session.getInstance(this);
        if (savedInstanceState != null) {
            String teamname = savedInstanceState.getString(s_teamname);
            mSession.restoreLogin(teamname);
        }
        Log.d("terngame", "BaseListActivity onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();

        mContent = (LinearLayout) findViewById(R.id.main_content);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);

        Log.d("terngame", "BaseList OnResume called");
        if (mSession.isDataLoaded(this)) {
            showUX();
        } else {
            showLoadingScreen();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(s_teamname, mSession.getTeamName());
    }

    public void showLoadingScreen() {
        Log.d("terngame", "BaseListActivity showLoadingScreen");
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
    }

    public void showUX() {
        Log.d("terngame", "BaseListActivity show Ux");
        mLoadingLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    public void onDataLoaded() {
        Log.d("terngame", "BaseListActivity in onDataLoaded, restoring normal UX");
        showUX();
    }

}
