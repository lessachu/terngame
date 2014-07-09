package com.twitter.terngame;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.data.EventInfo;
import com.twitter.terngame.data.HintInfo;
import com.twitter.terngame.data.LoginInfo;
import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.PuzzleInfo;
import com.twitter.terngame.data.StartCodeInfo;
import com.twitter.terngame.data.TeamStatus;
import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.AnswerChecker;
import com.twitter.terngame.util.HintNotification;
import com.twitter.terngame.util.JSONFileReaderTask;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by jchong on 1/11/14.
 */

public class Session {

    public interface HintListener {
        public void onHintReady(String puzzleID, String hintID, int notificationID);
    }

    public interface LoginLoadedListener {
        public void onLoginLoaded();
    }

    public interface DataLoadedListener {
        public void onDataLoaded();
    }

    private static Session sInstance = null;
    private Context mContext;

    private boolean mLoggedIn;
    private boolean mEventDataLoaded;
    private boolean mLoginDataLoaded;
    private boolean mStartCodeDataLoaded;
    private boolean mPuzzleExtraInfoLoaded;
    private boolean mTeamDataLoaded;

    private ArrayList<PendingIntent> mPendingHints;
    private ArrayList<HintListener> mHintListeners;
    private ArrayList<DataLoadedListener> mDataListeners;

    private TeamStatus mTeamStatus;   // static?
    private EventInfo mEventInfo;
    private LoginInfo mLoginInfo;
    private StartCodeInfo mStartCodeInfo;
    private PuzzleExtraInfo mPuzzleExtraInfo;

    private LoginLoadedListener mLoginLoadedListener;

    private Session(Context context) {
        mContext = context;
        mLoggedIn = false;
        mEventDataLoaded = false;
        mLoginDataLoaded = false;
        mStartCodeDataLoaded = false;
        mPuzzleExtraInfoLoaded = false;
        mTeamStatus = new TeamStatus();
        mEventInfo = new EventInfo();
        mLoginInfo = new LoginInfo();
        mPuzzleExtraInfo = new PuzzleExtraInfo(context);
        mStartCodeInfo = new StartCodeInfo(context);
        mPendingHints = new ArrayList<PendingIntent>();
        mHintListeners = new ArrayList<HintListener>();
        mDataListeners = new ArrayList<DataLoadedListener>();
        mLoginLoadedListener = null;
    }

    public static synchronized Session getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Session(context.getApplicationContext());
            sInstance.loadEventInformation();
        }
        return sInstance;
    }

    public void setLoginLoadedListener(LoginLoadedListener lll) {
        mLoginLoadedListener = lll;
    }

    public boolean isDataLoaded(DataLoadedListener dll) {

        Log.d("terngame", "isDataLoaded?");
        Log.d("terngame", "mLoggedIn: " + mLoggedIn + "EventData: " + mEventDataLoaded + " LoginData: " + mLoginDataLoaded + " mStartCode: " + mStartCodeDataLoaded +
                " mPUzzleExtra: " + mPuzzleExtraInfoLoaded + " mTeamData: " + mTeamDataLoaded);
        // check if all data is loaded
        if (isLoggedIn() && mEventDataLoaded && mLoginDataLoaded && mStartCodeDataLoaded
                && mPuzzleExtraInfoLoaded && mTeamDataLoaded) {
            Log.d("terngame", "isDataLoaded?  Yes it is!");
            return true;
        }

        if (dll != null) {
            Log.d("terngame", "Adding listener");
            mDataListeners.add(dll);
        }
        return false;
    }

    private void checkDataLoadComplete() {
        Log.d("terngame", "Checking if data load is complete!");

        Log.d("terngame", "mLoggedIn: " + mLoggedIn + "EventData: " + mEventDataLoaded + " LoginData: " + mLoginDataLoaded + " mStartCode: " + mStartCodeDataLoaded +
                " mPUzzleExtra: " + mPuzzleExtraInfoLoaded + " mTeamData: " + mTeamDataLoaded);

        if (isLoggedIn() && mEventDataLoaded && mLoginDataLoaded && mStartCodeDataLoaded
                && mPuzzleExtraInfoLoaded && mTeamDataLoaded) {

            Log.d("terngame", "Yay all the data is loaded!");
            for (DataLoadedListener dl : mDataListeners) {
                Log.d("terngame", "Calling on data loaded");
                dl.onDataLoaded();
            }
        }
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
        return mStartCodeInfo.getHintList(puzzleID);
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

    public PuzzleExtraInfo getPuzzleExtraInfo() {
        return mPuzzleExtraInfo;
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

        if (mLoginInfo.isValidLogin(teamName, password)) {
            mLoggedIn = true;
            // load team information if there is any
            mTeamStatus.initializeTeam(mContext, teamName,
                    new JSONFileReaderTask.JSONFileReaderCompleteListener() {
                        @Override
                        public void onJSONFileReaderComplete() {
                            mTeamDataLoaded = true;
                            checkDataLoadComplete();
                        }
                    });
            return true;
        }
        return false;
    }

    public void restoreLogin(String teamName) {
        mLoggedIn = true;
        // load team information if there is any
        mTeamStatus.initializeTeam(mContext, teamName,
                new JSONFileReaderTask.JSONFileReaderCompleteListener() {
                    @Override
                    public void onJSONFileReaderComplete() {
                        mTeamDataLoaded = true;
                        checkDataLoadComplete();
                    }
                });
    }


    public boolean isValidStartCode(String start_code) {
        start_code = AnswerChecker.stripAnswer(start_code);
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(start_code);
        return (pi != null);
    }

    public void startPuzzle(String start_code) {
        start_code = AnswerChecker.stripAnswer(start_code);
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(start_code);

        if (pi != null) {
            if (mTeamStatus.startNewPuzzle(start_code)) {
                ArrayList<HintInfo> hintList = mStartCodeInfo.getHintList(start_code);
                int len = hintList.size();
                for (int i = 0; i < len; i++) {
                    HintInfo hi = hintList.get(i);
                    Log.d("terngame", "Adding hints for : " + start_code);
                    mPendingHints.add(HintNotification.scheduleHint(mContext, start_code, i + 1,
                            hi.mID, hi.mTimeSecs));

                    // TODO: if we're returning to a puzzle, account for time passed
                }
            }
        }
    }

    public String getCorrectAnswer(String puzzleID) {
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        return pi.getCorrectAnswer();
    }

    public String skipPuzzle(String puzzleID) {
        String response = mStartCodeInfo.getNextInstruction(puzzleID);
        if (mTeamStatus.skipPuzzle(puzzleID, response)) {
            clearHintNotifications();
        }
        return response;
    }

    public AnswerInfo guessAnswer(String answer) {
        answer = AnswerChecker.stripAnswer(answer);

        String puzzleID = mTeamStatus.getCurrentPuzzle();

        // NPE here after the app goes out of memory
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        AnswerInfo ai = pi.getAnswerInfo(answer);
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
                clearHintNotifications();
            }
        }
        return ai;
    }

    public void updateExtra(String puzzleID, JSONObject newExtra) {
        mTeamStatus.updateExtra(puzzleID, newExtra);
    }

    // TODO: consider how to make this thread safe
    public void registerHintListener(HintListener hl) {
        mHintListeners.add(hl);
    }

    public void unregisterHintListener(HintListener hl) {
        mHintListeners.remove(hl);
    }

    public void hintReady(String puzzleID, String hintID, int notificationID) {
        // register the notification with the session
        // remove event from pending intent array
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        pi.registerHintNotification(hintID, notificationID);

        for (HintListener hl : mHintListeners) {
            if (hl != null) {
                hl.onHintReady(puzzleID, hintID, notificationID);
            }
        }
    }

    public void hintTaken(String puzzleID, String hintID) {
        mTeamStatus.markHintTaken(puzzleID, hintID);
        PuzzleInfo pi = mStartCodeInfo.getPuzzleInfo(puzzleID);
        HintNotification.cancelHint(mContext, pi.getHintNotificationID(hintID));
    }

    public void loadEventInformation() {
        Log.d("terngame", "Initializing event info start");
        mEventInfo.initializeEvent(mContext, new JSONFileReaderTask.JSONFileReaderCompleteListener() {
            @Override
            public void onJSONFileReaderComplete() {
                mEventDataLoaded = true;
                mLoginInfo.initialize(mContext, mEventInfo.getTeamFileName(),
                        new JSONFileReaderTask.JSONFileReaderCompleteListener() {
                            @Override
                            public void onJSONFileReaderComplete() {
                                mLoginDataLoaded = true;
                                Log.d("terngame", "Initializing login info end");
                                if (mLoginLoadedListener != null) {
                                    mLoginLoadedListener.onLoginLoaded();
                                }
                                checkDataLoadComplete();
                            }
                        });
                Log.d("terngame", "Initializing start code info start");
                mStartCodeInfo.initialize(mContext, mEventInfo.getStartCodeFileName(),
                        new JSONFileReaderTask.JSONFileReaderCompleteListener() {
                            @Override
                            public void onJSONFileReaderComplete() {
                                mStartCodeDataLoaded = true;
                                checkDataLoadComplete();
                            }
                        });
            }
        });
    }

    public void initializePuzzleExtra(String puzzleId, JSONObject puzzleExtraJSON) {
        // add callback for puzzleExtraInfo here.
        mPuzzleExtraInfo.initializePuzzleExtra(puzzleId, puzzleExtraJSON,
                new JSONFileReaderTask.JSONFileReaderCompleteListener() {
                    @Override
                    public void onJSONFileReaderComplete() {
                        mPuzzleExtraInfoLoaded = true;
                        checkDataLoadComplete();
                    }
                });
    }

    public void clearPuzzleData(String puzzleID) {
        Log.d("terngame", "in Session clear puzzle data");
        // if it's the current puzzle, clear hint notifications

        String curPuzzle = mTeamStatus.getCurrentPuzzle();
        if (curPuzzle != null && curPuzzle.equals(puzzleID)) {
            Log.d("terngame", "clearing notifications for " + puzzleID);
            clearHintNotifications();
        }

        // if it has an extra, clear the puzzle extra info
        mPuzzleExtraInfo.clearPuzzleExtraInfo(puzzleID);
        mTeamStatus.clearPuzzleData(puzzleID);
    }

    public void clearTeamData() {
        mTeamStatus.clearData();
        mPuzzleExtraInfo.clearAllPuzzleExtraInfo();
        clearHintNotifications();
    }

    public void clearHintNotifications() {
        for (PendingIntent pi : mPendingHints) {
            HintNotification.cancelHintAlarms(mContext, pi);
        }
        mPendingHints.clear();
    }

    // Twittermon stuff
    public Drawable getTwittermonImage(String creature) {
        return mPuzzleExtraInfo.getTwittermonInfo().getCreatureDrawable(creature);
    }

    public boolean hasTwittermon(String creature) {
        return mPuzzleExtraInfo.getTwittermonInfo().hasCreature(creature);
    }

    public boolean verifyTwittermonTrapCode(String creature, String code) {
        return mPuzzleExtraInfo.getTwittermonInfo().verifyTrapCode(creature, code);
    }

    public void collectTwittermon(String creature) {
        mPuzzleExtraInfo.getTwittermonInfo().addNewCreature(creature);
    }

    // returns s_win, s_lose or s_tie
    public int battleTwittermon(String us, String them) {
        return mPuzzleExtraInfo.getTwittermonInfo().battle(us, them);
    }

    public void logTwittermonBattle(String us, String them, int result) {
        mPuzzleExtraInfo.getTwittermonInfo().logBattle(us, them, result);
    }

    public void logTwittermonRoyaleComplete() {
        mPuzzleExtraInfo.getTwittermonInfo().royaleComplete();
    }

    public boolean isTwittermonRoyaleComplete() {
        return mPuzzleExtraInfo.getTwittermonInfo().isRoyaleComplete();
    }

    public ArrayList<TwittermonInfo.BattleInfo> getBattleList() {
        return mPuzzleExtraInfo.getTwittermonInfo().getBattleList();
    }
}
