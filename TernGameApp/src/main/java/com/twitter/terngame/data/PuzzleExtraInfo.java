package com.twitter.terngame.data;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by jchong on 5/4/14.
 */
public class PuzzleExtraInfo implements TwittermonPuzzleInfo {

    // list of supported extra puzzles
    public static String s_twittermon = "twittermon";

    private TwittermonInfo mTwittermonInfo;

    public PuzzleExtraInfo(Context context) {
        mTwittermonInfo = new TwittermonInfo(context);
    }

    public void initializePuzzleExtra(String puzzleId, JSONObject puzzleJSON) {
        if (puzzleId.equals(s_twittermon)) {
            mTwittermonInfo.initialize(puzzleJSON);
        }
    }

    public void initializePuzzleStatus(String puzzleId, JSONObject puzzleJSON) {
        if (puzzleId.equals(s_twittermon)) {
            mTwittermonInfo.initializePuzzleStatus(puzzleJSON);
        }
    }

    public void clearPuzzleExtraInfo() {
        mTwittermonInfo.clearSavedData();
    }

    public TwittermonInfo getTwittermonInfo() {
        return mTwittermonInfo;
    }


}
