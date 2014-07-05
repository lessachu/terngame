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

/**
 * Created by jchong on 2/18/14.
 */
public class HintNotification extends BroadcastReceiver {

    public static final String s_puzzleID = "puzzleID";
    public static final String s_hintNum = "hintNum";
    public static final String s_hintID = "hintID";
    public static final String HINT_INTENT = "com.twitter.terngame.SEND_HINT";

    public static int fireHintNotification(Context context, String puzzleID, String puzzleName,
            int hintNum) {

        int requestCode = (int) System.currentTimeMillis();
        Intent intent = new Intent(context, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.s_puzzleID, puzzleID);
        intent.putExtra(PuzzleActivity.s_hintPrompt, requestCode);
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
        int hintNumber = 0;
        String hintID = null;

        Session s = Session.getInstance(context);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            puzzleID = extras.getString(s_puzzleID);
            hintNumber = extras.getInt(s_hintNum);
            hintID = extras.getString(s_hintID);
        }

        if (puzzleID != null && hintNumber != 0) {
            int notifyID = fireHintNotification(context, puzzleID, s.getPuzzleName(puzzleID), hintNumber);
            s.hintReady(puzzleID, hintID, notifyID);
        } else {
            Log.d("terngame", "Invalid puzzleID/hint number specified");
        }
    }

    public static PendingIntent scheduleHint(Context context, String puzzleID, int hintNumber,
            String hintID, long timeSecs) {
        Intent intent = new Intent(HINT_INTENT);
        intent.putExtra(s_puzzleID, puzzleID);
        intent.putExtra(s_hintNum, hintNumber);
        intent.putExtra(s_hintID, hintID);

        PendingIntent pi = PendingIntent.getBroadcast(context, hintNumber, intent, 0);

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
