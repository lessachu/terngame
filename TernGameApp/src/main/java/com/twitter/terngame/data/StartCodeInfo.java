package com.twitter.terngame.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.twitter.terngame.Session;
import com.twitter.terngame.util.AnswerChecker;
import com.twitter.terngame.util.JSONFileReaderTask;
import com.twitter.terngame.util.JSONFileResultHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class StartCodeInfo implements JSONFileResultHandler, JSONFileReaderTask.JSONFileReaderCompleteListener {

    public static final String s_version = "version";
    public static final String s_startCodeArray = "start_codes";
    public static final String s_startCode = "id";
    public static final String s_puzzleName = "name";
    public static final String s_answerFile = "answer_file";
    public static final String s_instruction = "instruction";
    public static final String s_end_party = "end_party";
    public static final String s_order = "order";
    public static final String s_canonical = "canonical";
    public static final String s_aliases = "aliases";
    public static final String s_puzzleButton = "puzzle_button";
    public static final String s_puzzleButtonText = "button_text";
    public static final String s_puzzleButtonMode = "button_mode";
    public static final String s_puzzleButtonExtra = "button_extra";

    private JSONObject mData;

    private Context mContext;
    private int mVersion;
    private HashMap<String, PuzzleInfo> mStartCodes;
    private HashMap<String, String> mNextInstruction;
    private ArrayList<String> mPuzzleOrder;
    private ArrayList<PuzzleInfoReaderComplete> mPuzzleDataLoaders;
    private boolean mStartCodesLoaded;
    private JSONFileReaderTask.JSONFileReaderCompleteListener mJfrcl;

    public StartCodeInfo(Context context) {
        mContext = context;
        mStartCodes = new HashMap<String, PuzzleInfo>();
        mNextInstruction = new HashMap<String, String>();
        mPuzzleOrder = new ArrayList<String>();
        mPuzzleDataLoaders = new ArrayList<PuzzleInfoReaderComplete>();
        mStartCodesLoaded = false;
        mJfrcl = null;
    }

    public class PuzzleInfoReaderComplete implements JSONFileReaderTask.JSONFileReaderCompleteListener {
        private String mTag;
        public boolean mComplete;

        public PuzzleInfoReaderComplete(String tag) {
            mTag = tag;
            mComplete = false;
        }

        public String getTag() {
            return mTag;
        }

        public void onJSONFileReaderComplete() {
            mComplete = true;

            checkPuzzleDataReadComplete();
            // check the tag and report that the tag is complete to the larger array of tags
            // check if all tags are complete
            Log.d("terngame", "PuzzleInfoReader " + mTag + " is complete!");
        }
    }

    public synchronized void checkPuzzleDataReadComplete() {
        for (PuzzleInfoReaderComplete pirc : mPuzzleDataLoaders) {
            if (!pirc.mComplete) {
                Log.d("terngame", pirc.getTag() + " is not initialized yet");
                return;
            }
        }

        if (mStartCodesLoaded) {
            Log.d("terngame", "All puzzles initialized!");
            mPuzzleDataLoaders.clear();
            if (mJfrcl != null) {
                mJfrcl.onJSONFileReaderComplete();
            }
        } else {
            Log.d("terngame", "puzzles initialized, but start codes are not done yet");
        }
    }

    // called by JSONFileReaderTask
    public void saveResult(JSONObject jo) {
        mData = jo;

        if (mData != null) {
            try {
                mVersion = mData.getInt(s_version);
                final String endPartyLocation = mData.getString(s_end_party);

                JSONArray ja = mData.getJSONArray(s_startCodeArray);
                int len = ja.length();

                for (int i = 0; i < len; i++) {
                    JSONObject po = (JSONObject) ja.get(i);
                    PuzzleInfo pi = new PuzzleInfo();
                    pi.mName = po.getString(s_puzzleName);
                    pi.mAnswerFile = po.getString(s_answerFile);
                    pi.mInstruction = po.getString(s_instruction);

                    String code = po.getString(s_startCode);
                    code = AnswerChecker.stripAnswer(code);

                    if (po.has(s_puzzleButton)) {
                        JSONObject puzzleButton = po.getJSONObject(s_puzzleButton);
                        pi.mPuzzleButtonText = puzzleButton.getString(s_puzzleButtonText);
                        pi.mPuzzleButton = puzzleButton.getString(s_puzzleButtonMode);
                        if (puzzleButton.has(s_puzzleButtonExtra)) {
                            Session s = Session.getInstance(mContext);
                            s.initializePuzzleExtra(code, puzzleButton.getJSONObject(s_puzzleButtonExtra));
                        }
                    }
                    mStartCodes.put(code, pi);
                }

                final JSONArray puzzleOrder = mData.getJSONArray(s_order);
                len = puzzleOrder.length();
                for (int i = 0; i < len; i++) {
                    String instruction = endPartyLocation;
                    if (i < len - 1) {
                        String next = getPuzzleIDAt(puzzleOrder, i + 1);
                        PuzzleInfo pi = mStartCodes.get(next);
                        if (pi != null) {
                            instruction = pi.mInstruction;
                        } else {
                            Log.d("terngame", "next: " + next + " puzzle not found");
                        }
                    }

                    ArrayList<String> puzzles = getPuzzleAliasesAt(puzzleOrder, i);
                    for (String puzzleName : puzzles) {
                        //       Log.d("terngame", "Adding " + puzzleName + " with instruction: " + instruction);
                        mPuzzleOrder.add(puzzleName);
                        mNextInstruction.put(puzzleName, instruction);
                    }
                }
                initializeAnswers();

            } catch (JSONException e) {
                Log.e("terngame", "JsonException loading start code data");
                e.printStackTrace();
            }
        }
    }

    String getPuzzleIDAt(JSONArray ja, int index)
            throws JSONException {
        JSONObject jo = ja.optJSONObject(index);
        if (jo != null) {
            return jo.getString(s_canonical);
        }
        return ja.getString(index);
    }

    ArrayList<String> getPuzzleAliasesAt(JSONArray ja, int index)
            throws JSONException {
        ArrayList<String> puzzles = new ArrayList<String>();

        JSONObject jo = ja.optJSONObject(index);
        if (jo != null) {
            JSONArray puzzArray = jo.getJSONArray(s_aliases);
            for (int i = 0; i < puzzArray.length(); i++) {
                puzzles.add(puzzArray.getString(i));
            }
        } else {
            puzzles.add(ja.getString(index));
        }
        return puzzles;
    }

    public void initialize(Context context, String startCodeFile,
            JSONFileReaderTask.JSONFileReaderCompleteListener jfrcl) {
        // intercept this, so we can wait for all the puzzle loads to be done too.
        mJfrcl = jfrcl;

        try {

            InputStream in = context.getAssets().open(startCodeFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this, this);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context,
                    "No start code info file exists!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("terngame", "No event info file");

        } catch (IOException e) {

            Toast toast = Toast.makeText(context,
                    "IOException in initializePuzzleData!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("terngame", "IOException");
        }
    }

    public void onJSONFileReaderComplete() {
        mStartCodesLoaded = true;
        checkPuzzleDataReadComplete();
    }

    public String getNextInstruction(String startCode) {
        return mNextInstruction.get(startCode);
    }

    public PuzzleInfo getPuzzleInfo(String startCode) {
        return mStartCodes.get(startCode);
    }

    public String getPuzzleName(String startCode) {
        PuzzleInfo pi = mStartCodes.get(startCode);
        if (pi != null) {
            return pi.mName;
        }
        return "";
    }

    public boolean showPuzzleButton(String startCode) {
        PuzzleInfo pi = mStartCodes.get(startCode);
        return (pi != null && pi.mPuzzleButton != null);
    }

    public String getPuzzleButtonText(String startCode) {
        PuzzleInfo pi = mStartCodes.get(startCode);
        if (pi != null) {
            return pi.mPuzzleButtonText;
        }
        return "";
    }

    public void initializeAnswers() {
        for (String code : mStartCodes.keySet()) {
            // walk through the array and pull in the answer set
            PuzzleInfo pi = mStartCodes.get(code);
            PuzzleInfoReaderComplete pirc = new PuzzleInfoReaderComplete(code);
            mPuzzleDataLoaders.add(pirc);
            pi.initialize(mContext, pirc);
        }
    }

    public ArrayList<String> getPuzzleList() {
        return (ArrayList<String>) mPuzzleOrder.clone();
    }

    public ArrayList<HintInfo> getHintList(String puzzleID) {
        Log.d("terngame", "StartCodeInfo getHintList " + puzzleID);
        PuzzleInfo pi = mStartCodes.get(puzzleID);

        if (pi != null) {
            Log.d("terngame", "StartCodeInfo pi is not null" + pi.toString());
            return pi.getHintCopy();
        } else {
            Log.d("terngame", "StartCodeInfo pi is null");
        }
        return null;
    }

    public JSONObject getExtra(String puzzleID) {
        PuzzleInfo pi = mStartCodes.get(puzzleID);
        if (pi != null) {
            return pi.getExtra();
        }
        return null;
    }
}
