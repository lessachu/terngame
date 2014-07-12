package com.twitter.terngame.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.twitter.terngame.MainActivity;
import com.twitter.terngame.PuzzleActivity;
import com.twitter.terngame.R;
import com.twitter.terngame.Session;

import java.util.ArrayList;

/**
 * Created by jchong on 2/18/14.
 */
public class HintNotification extends BroadcastReceiver
        implements Session.DataLoadedListener {

    public static final String s_puzzleID = "puzzleID";
    public static final String s_puzzleName = "puzzleName";
    public static final String s_hintNum = "hintNum";
    public static final String s_hintID = "hintID";
    public static final String s_teamName = "teamName";
    public static final String HINT_INTENT = "com.twitter.terngame.SEND_HINT";

    private static ArrayList<NotificationInfo> mNotifications = new ArrayList<NotificationInfo>();

    public class NotificationInfo {
        public String mPuzzleID;
        public String mPuzzleName;
        public String mHintID;
        public int mHintNum;
        Context mContext;
    }

    public static int fireHintNotification(Context context, String puzzleID, String puzzleName,
            int hintNum) {

        int requestCode = (int) System.currentTimeMillis();
        Intent intent = new Intent(context, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.s_puzzleID, puzzleID);
        intent.putExtra(PuzzleActivity.s_hintPrompt, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent mainIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(mainIntent);
        stackBuilder.addNextIntent(intent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(requestCode, 0);

        String subject = "Hint " + Integer.toString(hintNum) + " for " + puzzleName +
                " is now available.";

        Notification n = new NotificationCompat.Builder(context)
                .setVibrate(new long[]{1000, 1000, 1000, 1000})
                .setContentTitle(context.getString(R.string.notif_hint_title))
                .setContentText(subject).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(requestCode, n);
        return requestCode;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String puzzleID = null;
        String puzzleName = null;
        int hintNumber = 0;
        String hintID = null;
        String teamName = null;

        Session s = Session.getInstance(context);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            puzzleID = extras.getString(s_puzzleID);
            puzzleName = extras.getString(s_puzzleName);
            hintNumber = extras.getInt(s_hintNum);
            hintID = extras.getString(s_hintID);
            teamName = extras.getString(s_teamName);
        }

        if (puzzleID != null && hintNumber != 0) {
            if (s.isDataLoaded(this)) {
                int notifyID = fireHintNotification(context, puzzleID, puzzleName, hintNumber);
                s.hintReady(puzzleID, hintID, notifyID);
            } else {
                s.restoreLogin(teamName);
                NotificationInfo ni = new NotificationInfo();
                ni.mContext = context;
                ni.mPuzzleID = puzzleID;
                ni.mPuzzleName = puzzleName;
                ni.mHintNum = hintNumber;
                ni.mHintID = hintID;
                mNotifications.add(ni);
            }
        } else {
            Log.d("terngame", "Invalid puzzleID/hint number specified");
        }
    }

    public void onDataLoaded() {
        for (NotificationInfo ni : mNotifications) {
            Session s = Session.getInstance(ni.mContext);
            int notifyID = fireHintNotification(ni.mContext, ni.mPuzzleID, ni.mPuzzleName, ni.mHintNum);
            s.hintReady(ni.mPuzzleID, ni.mHintID, notifyID);
        }
        mNotifications.clear();
    }

    public static PendingIntent scheduleHint(Context context, String puzzleID, String puzzleName, int hintNumber,
            String hintID, String teamName, long timeSecs) {
        Intent intent = new Intent(HINT_INTENT);
        intent.putExtra(s_puzzleID, puzzleID);
        intent.putExtra(s_hintNum, hintNumber);
        intent.putExtra(s_puzzleName, puzzleName);
        intent.putExtra(s_hintID, hintID);
        intent.putExtra(s_teamName, teamName);

        PendingIntent pi = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                    timeSecs * 1000, pi);
        } else {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                    timeSecs * 1000, pi);
        }

        Log.d("terngame", "alarm set for hint " + Integer.toString(hintNumber) + " for " + puzzleID +
                " at " + Long.toString(timeSecs) + " secs");
        return pi;
    }

    public static void cancelHintAlarms(Context context, PendingIntent pi) {
        if (pi != null) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pi);
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void cancelHint(Context context, int notifID) {
        if (notifID != -1) {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notifID);
        }
    }
}
