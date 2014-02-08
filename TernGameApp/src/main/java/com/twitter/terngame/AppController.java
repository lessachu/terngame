package com.twitter.terngame;

import android.content.Context;

import com.twitter.terngame.Session;

/**
 * Created by jchong on 1/11/14.
 */
public class AppController {

    private static AppController sInstance;

    final Context mAppContext;
    private Session mSession;

    private AppController(Context context) {
        mAppContext = context;
        mSession = Session.getInstance(context);

    }

    public static synchronized AppController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppController(context.getApplicationContext());
            sInstance.initializeApp();
        }
        return sInstance;
    }

    void initializeApp() {
        mSession.loadEventInformation();
    }


}
