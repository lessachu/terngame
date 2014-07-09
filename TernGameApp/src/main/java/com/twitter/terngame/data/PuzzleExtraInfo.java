package com.twitter.terngame.data;

import android.content.Context;

import com.twitter.terngame.util.JSONFileReaderTask;

import org.json.JSONObject;

public class PuzzleExtraInfo implements TwittermonPuzzleInfo {

    // list of supported extra puzzles
    public static String s_twittermon = "twittermon";

    private TwittermonInfo mTwittermonInfo;

    public PuzzleExtraInfo(Context context) {
        mTwittermonInfo = new TwittermonInfo(context);
    }

    public void initializePuzzleExtra(String puzzleId, JSONObject puzzleJSON,
            JSONFileReaderTask.JSONFileReaderCompleteListener jfrcl) {
        if (puzzleId.equals(s_twittermon)) {
            mTwittermonInfo.initialize(puzzleJSON, jfrcl);
        }
    }

    public void initializePuzzleStatus(String puzzleId, JSONObject puzzleJSON) {
        if (puzzleId.equals(s_twittermon)) {
            mTwittermonInfo.initializePuzzleStatus(puzzleJSON);
        }
    }

    public void clearPuzzleExtraInfo(String puzzleId) {
        if (puzzleId.equals(s_twittermon)) {
            mTwittermonInfo.clearSavedData();
        }
    }

    public void clearAllPuzzleExtraInfo() {
        mTwittermonInfo.clearSavedData();
    }

    public TwittermonInfo getTwittermonInfo() {
        return mTwittermonInfo;
    }

}
