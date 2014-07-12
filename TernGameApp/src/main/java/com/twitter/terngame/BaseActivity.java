package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public abstract class BaseActivity extends Activity
        implements Session.DataLoadedListener {

    private static final String s_teamname = "teamname";

    protected LinearLayout mContent;
    protected LinearLayout mLoadingLayout;
    protected Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("terngame", "BaseActivity onCreate");
        mSession = Session.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mContent = (LinearLayout) findViewById(R.id.main_content);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);

        Log.d("terngame", "BaseActivity OnResume called");
        if (mSession.isDataLoaded(this)) {
            showUX();
        } else {
            showLoadingScreen();
        }
    }

    /*  @Override
      public void onSaveInstanceState(Bundle outState) {
          super.onSaveInstanceState(outState);
          outState.putString(s_teamname, mSession.getTeamName());
      }

  */
    public void showLoadingScreen() {
        Log.d("terngame", "BaseActivity showLoadingScreen");
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
    }

    public void showUX() {
        Log.d("terngame", "BaseActivity show Ux");
        mLoadingLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
    }

    public void onDataLoaded() {
        Log.d("terngame", "BaseActivity in onDataLoaded, restoring normal UX");
        showUX();
    }

}
