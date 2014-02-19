package com.twitter.terngame.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.twitter.terngame.PuzzleActivity;
import com.twitter.terngame.R;

/**
 * Created by jchong on 2/18/14.
 */
public class HintNotification {

    private static int mID = 0;


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
}
