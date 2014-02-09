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

    public String getPuzzleName() {
        if (mCurrentPuzzle.mName == null) {
            return "";
        }
        return mCurrentPuzzle.mName;
    }

    public String getWrongAnswerString() {
        return mEventInfo.getWrongAnswerString();
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

    public ArrayList<String> getGuesses() {
        return mTeamStatus.getGuesses();
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
            if (start_code.contentEquals("battle")) {
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

    public void skipPuzzle() {
        mTeamStatus.skipCurrentPuzzle();
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
            mTeamStatus.solvePuzzle(puzzleId);
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

}
