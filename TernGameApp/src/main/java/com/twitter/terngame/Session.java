package com.twitter.terngame;

import android.content.Context;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.data.EventInfo;
import com.twitter.terngame.data.LoginInfo;
import com.twitter.terngame.data.PuzzleInfo;
import com.twitter.terngame.data.StartCodeInfo;
import com.twitter.terngame.data.TeamStatus;
import com.twitter.terngame.util.AnswerChecker;

import java.util.ArrayList;


/**
 * Created by jchong on 1/11/14.
 */

public class Session implements EventInfo.EventInfoListener {

    private static Session sInstance = null;
    private Context mContext;
    private boolean mLoggedIn;
    private boolean mPuzzleStarted;
    private boolean mPuzzleButton;   // does this puzzle need a button
    private PuzzleInfo mCurrentPuzzle;

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

    public boolean puzzleStarted() {
        return mTeamStatus.getCurrentPuzzle() != null;
    }

    public boolean puzzleSolved(String puzzleID) {
        return mTeamStatus.isPuzzleSolved(puzzleID);
    }

    public boolean puzzleSkipped(String puzzleID) {
        return mTeamStatus.isPuzzleSkipped(puzzleID);
    }

    public boolean showPuzzleButton() {
        return mPuzzleButton;  // temp
    }

    public String getPuzzleButtonText() {
        return "Battle!";
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
            mTeamStatus.startNewPuzzle(start_code);

            // test code
            if (start_code.equals("battle")) {
                mPuzzleButton = true;
            } else {
                mPuzzleButton = false;
            }

            mPuzzleStarted = true;
            mCurrentPuzzle = pi;
            return true;
        }
        return false;
    }

    public String getCorrectAnswer(String puzzleID) {
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        return pi.getCorrectAnswer();
    }

    public String skipPuzzle(String puzzleID, String answer) {
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        AnswerInfo ai = pi.getAnswerInfo(answer);
        mTeamStatus.skipPuzzle(puzzleID, ai.mResponse);
        return ai.mResponse;
    }

    public AnswerInfo guessAnswer(String answer) {
        answer = AnswerChecker.stripAnswer(answer);
        AnswerInfo ai = mCurrentPuzzle.getAnswerInfo(answer);
        String puzzleId = mTeamStatus.getCurrentPuzzle();
        boolean isDupe = mTeamStatus.addGuess(puzzleId, answer);

        if (ai == null) {
            ai = new AnswerInfo();
            ai.mResponse = mEventInfo.getWrongAnswerString();
            ai.mCorrect = false;
        }

        ai.mDuplicate = isDupe;

        if (ai.mCorrect) {
            mTeamStatus.solvePuzzle(puzzleId, ai.mResponse);
        }
        return ai;
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
