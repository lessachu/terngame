package com.twitter.terngame.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.twitter.terngame.PuzzleActivity;
import com.twitter.terngame.R;
import com.twitter.terngame.Session;

/**
 * Created by jchong on 2/18/14.
 */
public class HintNotification extends BroadcastReceiver {

    private static int mID = 0;
    public static final String s_puzzleID = "puzzleID";
    public static final String s_hintNum = "hintNum";
    public static final String HINT_INTENT = "com.twitter.terngame.SEND_HINT";

    public static void fireHintNotification(Context context, String puzzleID, String puzzleName,
            int hintNum) {
        Intent intent = new Intent(context, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.s_puzzleID, puzzleID);
        intent.putExtra(PuzzleActivity.s_hintPrompt, true);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // TODO: correct handling of the backstack seems annoying

        String subject = "Hint " + Integer.toString(hintNum) + " for " + puzzleName +
                " is now available.";

        Notification n = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.notif_hint_title))
                .setContentText(subject).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(mID++, n);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String puzzleID = null;
        int hintNumber = 0;

        Session s = Session.getInstance(context);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            puzzleID = extras.getString(s_puzzleID);
            hintNumber = extras.getInt(s_hintNum);
        }

        if (puzzleID != null && hintNumber != 0) {
            fireHintNotification(context, puzzleID, s.getPuzzleName(puzzleID), hintNumber);
        } else {
            Log.d("terngame", "Invalid puzzleID/hint number specified");
        }
    }

    public static void setAlarm(Context context, String puzzleID, int hintNumber, long timeSecs) {
        Intent intent = new Intent(HINT_INTENT);
        intent.putExtra(s_puzzleID, puzzleID);
        intent.putExtra(s_hintNum, hintNumber);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                timeSecs * 1000, pi);

        Log.d("terngame", "alarm set for hint " + Integer.toString(hintNumber) + " for " + puzzleID);

    }
}
