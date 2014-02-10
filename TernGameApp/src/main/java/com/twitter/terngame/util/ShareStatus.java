package com.twitter.terngame.util;

import android.content.Intent;
import android.util.Log;

import com.twitter.terngame.Session;

/**
 * Created by jchong on 2/9/14.
 */
public class ShareStatus {
    public static int TWEET_MAX = 140;

    public static Intent getGameStatusIntent(Session s) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);

        int puzzleNumber = s.getPuzzlesSkipped() + s.getPuzzlesSolved();
        String tweetBody = " just solved puzzle " + Integer.toString(puzzleNumber) +
                " of the #terngame!";
        String tweetText = "Team ";
        String teamName = s.getTeamName();
        // truncate down to 140 characters if need be.
        int total = teamName.length() + tweetText.length() + tweetBody.length();
        if (total > TWEET_MAX) {
            int trim = total - TWEET_MAX;

            // if we'd have to trim too much to fit, then fuck it, we'll just let
            // Twitter truncate the Tweet.

            if (trim < teamName.length()) {
                teamName = teamName.substring(0, teamName.length() - trim);
            }
        }
        Log.d("terngame", Integer.toString(total) + " " + Integer.toString(TWEET_MAX) + " " +
                teamName);

        sendIntent.putExtra(Intent.EXTRA_TEXT, tweetText + teamName + tweetBody);
        sendIntent.setType("text/plain");
        return sendIntent;
    }
}
