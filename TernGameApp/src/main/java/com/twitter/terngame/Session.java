package com.twitter.terngame;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.data.EventInfo;
import com.twitter.terngame.data.HintInfo;
import com.twitter.terngame.data.LoginInfo;
import com.twitter.terngame.data.PuzzleInfo;
import com.twitter.terngame.data.StartCodeInfo;
import com.twitter.terngame.data.TeamStatus;
import com.twitter.terngame.util.AnswerChecker;
import com.twitter.terngame.util.HintNotification;

import java.util.ArrayList;


/**
 * Created by jchong on 1/11/14.
 */

public class Session implements EventInfo.EventInfoListener {

    private static Session sInstance = null;
    private Context mContext;
    private boolean mLoggedIn;
    private PuzzleInfo mCurrentPuzzle;
    private ArrayList<PendingIntent> mPendingHints;

    private TeamStatus mTeamStatus;   // static?
    private EventInfo mEventInfo;
    private LoginInfo mLoginInfo;
    private StartCodeInfo mStartCodeInfo;


    private Session(Context context) {
        mContext = context;
        mCurrentPuzzle = new PuzzleInfo();
        mTeamStatus = new TeamStatus();
        mEventInfo = new EventInfo(this);
        mLoginInfo = new LoginInfo();
        mStartCodeInfo = new StartCodeInfo(context);
        mPendingHints = new ArrayList<PendingIntent>();
    }

    public static Session getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Session(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean isLoggedIn() {
        return mLoggedIn;
    }

    public String getEventName() {
        return mEventInfo.getEventName();
    }

    public String getSkipCode() {
        return mEventInfo.getSkipCode();
    }

    public boolean puzzleStarted() {
        return mTeamStatus.getCurrentPuzzle() != null;
    }

    public boolean puzzleSolved(String puzzleID) {
        return mTeamStatus.isPuzzleSolved(puzzleID);
    }

    public boolean puzzleSkipped(String puzzleID) {
        return mTeamStatus.isPuzzleSkipped(puzzleID);
    }

    public boolean showPuzzleButton(String puzzleID) {
        return mStartCodeInfo.showPuzzleButton(puzzleID);
    }

    public String getPuzzleButtonText(String puzzleID) {
        return mStartCodeInfo.getPuzzleButtonText(puzzleID);
    }

    public String getTeamName() {
        String name = mTeamStatus.getTeamName();
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    public String getPuzzleName(String puzzleID) {
        return mStartCodeInfo.getPuzzleName(puzzleID);
    }

    public String getDuplicateAnswerString() {
        return mEventInfo.getDuplicateAnswerString();
    }

    public int getPuzzlesSolved() {
        return mTeamStatus.getNumSolved();
    }

    public int getPuzzlesSkipped() {
        return mTeamStatus.getNumSkipped();
    }

    public ArrayList<HintInfo> getHintStatus(String puzzleID) {
        ArrayList<HintInfo> hiArray = mStartCodeInfo.getHintList(puzzleID);

        // TODO: get hint status from teamStatus and set the mUsed field
        // accordingly

        return hiArray;
    }

    public TeamStatus.PuzzleStatus getPuzzleStatus(String puzzleID) {
        return mTeamStatus.getPuzzleStatus(puzzleID);
    }

    public String getCurrentPuzzleID() {
        return mTeamStatus.getCurrentPuzzle();
    }

    public String getCurrentInstruction() {
        String instruction = mTeamStatus.getLastInstruction();
        if (instruction == null) {
            instruction = mContext.getString(R.string.default_instructions);
        }
        return instruction;
    }

    public ArrayList<String> getGuesses(String puzzleID) {
        return mTeamStatus.getGuesses(puzzleID);
    }

    public long getPuzzleStartTime(String puzzleID) {
        return mTeamStatus.getStartTime(puzzleID);
    }

    public long getPuzzleEndTime(String puzzleID) {
        return mTeamStatus.getEndTime(puzzleID);
    }

    public ArrayList<String> getPuzzleList() {
        return mStartCodeInfo.getPuzzleList();
    }

    public boolean login(String teamName, String password) {
        mLoggedIn = true;

        if (mLoginInfo.isValidLogin(teamName, password)) {
            // load team information if there is any
            mTeamStatus.initializeTeam(mContext, teamName);
            return true;
        }

        return false;
    }

    public boolean isValidStartCode(String start_code) {
        start_code = AnswerChecker.stripAnswer(start_code);
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(start_code);

        if (pi != null) {
            if (mTeamStatus.startNewPuzzle(start_code)) {
                mCurrentPuzzle = pi;

                ArrayList<HintInfo> hintList = mStartCodeInfo.getHintList(start_code);
                int len = hintList.size();
                for (int i = 0; i < len; i++) {
                    HintInfo hi = hintList.get(i);
                    mPendingHints.add(HintNotification.scheduleHint(mContext, start_code, i + 1,
                            hi.mID, hi.mTimeSecs));

                    // TODO: if we're returning to a puzzle, account for time passed
                }
            }
            return true;
        }
        return false;
    }

    public String getCorrectAnswer(String puzzleID) {
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        return pi.getCorrectAnswer();
    }

    public String skipPuzzle(String puzzleID) {
        String response = mStartCodeInfo.getNextInstruction(puzzleID);
        if (mTeamStatus.skipPuzzle(puzzleID, response)) {
            for (PendingIntent pi : mPendingHints) {
                HintNotification.cancelHintAlarms(mContext, pi);
            }
        }
        return response;
    }

    public AnswerInfo guessAnswer(String answer) {
        answer = AnswerChecker.stripAnswer(answer);

        Log.d("terngame", "Guessing : " + answer);
        AnswerInfo ai = mCurrentPuzzle.getAnswerInfo(answer);
        String puzzleID = mTeamStatus.getCurrentPuzzle();
        boolean isDupe = mTeamStatus.addGuess(puzzleID, answer);

        if (ai == null) {
            ai = new AnswerInfo();
            ai.mResponse = mEventInfo.getWrongAnswerString();
            ai.mCorrect = false;
        }

        ai.mDuplicate = isDupe;

        if (ai.mCorrect) {
            ai.mResponse = mStartCodeInfo.getNextInstruction(puzzleID);
            if (mTeamStatus.solvePuzzle(puzzleID, ai.mResponse)) {
                for (PendingIntent pi : mPendingHints) {
                    HintNotification.cancelHintAlarms(mContext, pi);
                }
            }
        }
        return ai;
    }

    public void hintReady(String puzzleID, String hintID) {
        // remove event from pending intent array
    }

    public void hintTaken(String puzzleID, String hintID) {
        mTeamStatus.markHintTaken(puzzleID, hintID);
    }

    public void loadEventInformation() {
        mEventInfo.initializeEvent(mContext);
    }

    public void onEventInfoLoadComplete() {
        mLoginInfo.initialize(mContext, mEventInfo.getTeamFileName());
        mStartCodeInfo.initialize(mContext, mEventInfo.getStartCodeFileName());
    }

    public void clearCurrentPuzzle() {
        mTeamStatus.clearCurrentPuzzle();
    }

    public void clearTeamData() {
        mTeamStatus.clearData();
    }
}
