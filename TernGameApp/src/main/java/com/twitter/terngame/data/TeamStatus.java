package com.twitter.terngame.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.twitter.terngame.util.JSONFileReaderTask;
import com.twitter.terngame.util.JSONFileResultHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Meant to hold all the team data that should be saved
 * Created by jchong on 1/16/14.
 */
public class TeamStatus implements JSONFileResultHandler {
    // TODO: savefile should incorporate a hash of the team name
    // else you'll overwrite another team's progress just by logging into their app
    public static String s_saveFile = "teamStatus.json";
    private static String s_teamName = "teamName";
    private static String s_numSolved = "numSolved";
    private static String s_numSkipped = "numSkipped";
    private static String s_puzzles = "puzzles";
    private static String s_currentPuzzle = "curPuzzle";
    private static String s_eventName = "eventName";
    private static String s_lastUpdate = "lastUpdate";
    private static String s_puzzleID = "id";
    private static String s_puzzStart = "startTime";
    private static String s_puzzEnd = "endTime";
    private static String s_puzzSolved = "solved";
    private static String s_puzzSkipped = "skipped";
    private static String s_puzzGuesses = "guesses";

    private Context mContext;
    private JSONObject mData;

    // data fields
    public String mTeamName;
    public String mCurrentPuzzle;
    public int mNumSolved;
    public int mNumSkipped;
    public HashMap<String, PuzzleStatus> mPuzzles;
    public Date mLastUpdate;

    public class PuzzleStatus {
        public String mID;
        public Date mStartTime;
        public Date mEndTime;
        public boolean mSolved;
        public boolean mSkipped;
        public ArrayList<String> mGuesses;
    }

    public TeamStatus() {
        mData = new JSONObject();
        mPuzzles = new HashMap<String, PuzzleStatus>();
    }

    public void clearCurrentPuzzle() {
        mCurrentPuzzle = null;
        save();
    }

    public void clearData() {
        mCurrentPuzzle = null;
        mNumSolved = 0;
        mNumSkipped = 0;
        mPuzzles.clear();
        updateTimeStamp();
        save();
    }

    // called by JSONFileReaderTask
    public void saveResult(JSONObject jo) {
        mData = jo;
        if (mData != null) {
            try {
                // should probably validate the info is for this team
                assert (mData.getString(s_teamName).equals(mTeamName));
                // populate fields based on the data
                if (mData.has(s_currentPuzzle)) {
                    mCurrentPuzzle = mData.getString(s_currentPuzzle);
                }
                mNumSolved = mData.getInt(s_numSolved);
                mNumSkipped = mData.getInt(s_numSkipped);

                JSONArray ja = mData.getJSONArray(s_puzzles);
                int len = ja.length();

                for (int i = 0; i < len; i++) {
                    JSONObject po = (JSONObject) ja.get(i);
                    PuzzleStatus ps = new PuzzleStatus();
                    ps.mID = po.getString(s_puzzleID);
                    ps.mSolved = po.getBoolean(s_puzzSolved);
                    ps.mSkipped = po.getBoolean(s_puzzSkipped);
                    // ps.mStartTime =
                    // ps.mEndTime =

                    JSONArray guessArray = po.getJSONArray(s_puzzGuesses);
                    ps.mGuesses = new ArrayList<String>();
                    int guesslen = guessArray.length();
                    for (int j = 0; j < guesslen; j++) {
                        ps.mGuesses.add(guessArray.getString(j));
                    }

                    mPuzzles.put(ps.mID, ps);
                }
            } catch (JSONException e) {
                Log.e("terngame", "JSONException reading in team status");
            }
        }
    }

    public void initializeTeam(Context context, String teamName) {
        mTeamName = teamName;
        mContext = context;
        // if there is a savefile present, load from that
        try {
            Log.d("terngame", "reading in team status");
            File f = new File(context.getFilesDir(), s_saveFile);
            InputStream in = new BufferedInputStream(new FileInputStream(f));
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(mContext,
                    "No save file exists!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("jan", "No save file");

        }
    }

    public void save() {

        if (!updateJSONData()) {
            Toast toast = Toast.makeText(mContext,
                    "I couldn't save your team progress. Continue at your own risk.",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Log.d("terngame", mData.toString());

        TeamDataSaverTask saverTask = new TeamDataSaverTask();
        saverTask.execute(mData);
        Log.d("terngame", "after: " + mData.toString());
    }

    private boolean updateJSONData() {

        JSONArray puzzleArray = new JSONArray();

        for (String puzzleID : mPuzzles.keySet()) {
            PuzzleStatus ps = mPuzzles.get(puzzleID);
            JSONObject jo = new JSONObject();
            try {
                jo.put(s_puzzleID, ps.mID);
                //     jo.put(s_puzzStart, ps.mStartTime);   // TODO
                //     jo.put(s_puzzEnd, ps.mEndTime);       // TODO
                jo.put(s_puzzSolved, ps.mSolved);
                jo.put(s_puzzSkipped, ps.mSkipped);

                JSONArray guessArray = new JSONArray();
                for (String guess : ps.mGuesses) {
                    guessArray.put(guess);
                }
                jo.put(s_puzzGuesses, guessArray);
                puzzleArray.put(jo);

            } catch (JSONException e) {
                return false;
            }
        }

        if (mData == null) {
            Log.e("terngame", "Hrm, the save file is corrupted.  Starting anew");
            mData = new JSONObject();
        }

        try {
            mData.put(s_puzzles, puzzleArray);
            mData.put(s_teamName, mTeamName);
            mData.put(s_numSolved, mNumSolved);
            mData.put(s_numSkipped, mNumSkipped);
            mData.put(s_currentPuzzle, mCurrentPuzzle);

        } catch (JSONException e) {
            return false;
        }

        return true;
    }

    public String getTeamName() {
        return mTeamName;
    }

    public String getCurrentPuzzle() {
        return mCurrentPuzzle;
    }

    public int getNumSolved() {
        return mNumSolved;
    }

    public int getNumSkipped() {
        return mNumSkipped;
    }

    public ArrayList<String> getGuesses() {
        if (mCurrentPuzzle != null) {
            PuzzleStatus ps = mPuzzles.get(mCurrentPuzzle);
            if (ps != null) {
                return ps.mGuesses;
            }
        }
        return null;
    }

    public void startNewPuzzle(String puzzleID) {
        if (!mPuzzles.containsKey(puzzleID)) {
            PuzzleStatus ps = new PuzzleStatus();
            ps.mID = puzzleID;
            ps.mStartTime = new Date();
            ps.mGuesses = new ArrayList<String>();

            mPuzzles.put(puzzleID, ps);
            mCurrentPuzzle = puzzleID;
            updateTimeStamp();
        } else {
            Log.d("terngame", "Um, we're trying to start " + puzzleID + ", we already started.");
        }
    }

    public boolean addGuess(String puzzleID, String guess) {
        // hash into hash map, add the guess
        PuzzleStatus ps = mPuzzles.get(puzzleID);
        guess = JSONObject.quote(guess);
        boolean isDupe = false;
        if (ps != null && !ps.mSolved && !ps.mSkipped) {
            isDupe = isDuplicate(guess, ps.mGuesses);
            if (!isDupe) {
                ps.mGuesses.add(guess);
                updateTimeStamp();
                save();
            }
        } else {
            Log.d("terngame", "Guess " + guess + " made for invalid puzzle " + puzzleID);
        }
        return isDupe;
    }

    public void solvePuzzle(String puzzleID) {
        if (mCurrentPuzzle != null && mCurrentPuzzle.equals(puzzleID)) {
            PuzzleStatus ps = mPuzzles.get(puzzleID);
            if (ps != null && !ps.mSolved && !ps.mSkipped) {
                ps.mSolved = true;
                ps.mEndTime = new Date();
                mNumSolved++;
                mCurrentPuzzle = null;
                updateTimeStamp();
                save();
            } else {
                Log.d("terngame", "Invalid solve for " + puzzleID);
            }
        } else {
            Log.d("terngame", "Trying to solve non-current puzzle " + puzzleID);
        }
    }

    public void skipPuzzle(String puzzleID) {
        if (mCurrentPuzzle != null && mCurrentPuzzle.equals(puzzleID)) {
            PuzzleStatus ps = mPuzzles.get(puzzleID);
            if (ps != null && !ps.mSolved && !ps.mSkipped) {
                ps.mSkipped = true;
                ps.mEndTime = new Date();
                mNumSkipped++;
                mCurrentPuzzle = null;
                updateTimeStamp();
                save();
            }
        } else {
            Log.d("terngame", "Trying to skip non-current puzzle " + puzzleID);
        }
    }

    public void skipCurrentPuzzle() {
        skipPuzzle(mCurrentPuzzle);
    }

    private void updateTimeStamp() {
        mLastUpdate = new Date();
    }

    private boolean isDuplicate(String guess, ArrayList<String> al) {
        for (String s : al) {
            Log.d("terngame", "Comparing " + s + " to " + guess);
            if (s.equals(guess)) {
                Log.d("terngame", "Dupe!");
                return true;
            }
        }
        return false;
    }


    public class TeamDataSaverTask extends AsyncTask<JSONObject, Void, Boolean> {

        protected Boolean doInBackground(JSONObject... params) {
            try {
                FileOutputStream out = mContext.openFileOutput(s_saveFile, Context.MODE_PRIVATE);
                JSONObject jo = params[0];
                out.write(jo.toString().getBytes());
                out.flush();
                out.close();

                Log.d("terngame", "Information saved: " + jo.toString());
            } catch (FileNotFoundException fnf) {
                return false;
            } catch (IOException io) {
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(mContext,
                        "I couldn't save your team progress. Continue at your own risk.",
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(mContext,
                        "Progress saved.",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

}
