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

public class StartCodeInfo implements JSONFileResultHandler {

    public static final String s_version = "version";
    public static final String s_startCodeArray = "start_codes";
    public static final String s_startCode = "id";
    public static final String s_puzzleName = "name";
    public static final String s_answerFile = "answer_file";
    public static final String s_instruction = "instruction";
    public static final String s_end_party = "end_party";
    public static final String s_order = "order";
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

    public StartCodeInfo(Context context) {
        mContext = context;
        mStartCodes = new HashMap<String, PuzzleInfo>();
        mNextInstruction = new HashMap<String, String>();
        mPuzzleOrder = new ArrayList<String>();
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
                for (int i = 0; i < len - 1; i++) {
                    String puzzleID = puzzleOrder.getString(i);
                    mPuzzleOrder.add(AnswerChecker.stripAnswer(puzzleID));

                    String next = puzzleOrder.getString(i + 1);
                    PuzzleInfo pi = mStartCodes.get(next);
                    if (pi != null) {
                        mNextInstruction.put(puzzleID, pi.mInstruction);
                    } else {
                        Log.e("terngame", "Invalid puzzleID " + next + " in event order array.");
                    }
                }

                String lastPuzzle = puzzleOrder.getString(len - 1);
                mPuzzleOrder.add(AnswerChecker.stripAnswer(lastPuzzle));
                mNextInstruction.put(lastPuzzle, endPartyLocation);
                Log.d("terngame", "NextInstruction: " + mNextInstruction.toString());
                initializeAnswers();

            } catch (JSONException e) {
                Log.e("terngame", "JsonException loading start code data");
                e.printStackTrace();
            }
        }
    }

    public void initialize(Context context, String startCodeFile,
            JSONFileReaderTask.JSONFileReaderCompleteListener jfrcl) {
        try {

            InputStream in = context.getAssets().open(startCodeFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this, jfrcl);
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
            pi.initialize(mContext);
        }
    }

    public ArrayList<String> getPuzzleList() {
        return (ArrayList<String>) mPuzzleOrder.clone();
    }

    public ArrayList<HintInfo> getHintList(String puzzleID) {
        PuzzleInfo pi = mStartCodes.get(puzzleID);
        if (pi != null) {
            return pi.getHintCopy();
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
