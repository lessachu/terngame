package com.twitter.terngame.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

public class PuzzleInfo implements JSONFileResultHandler {

    public static final String s_version = "version";
    public static final String s_answerArray = "answer_list";
    public static final String s_answer = "answer";
    public static final String s_response = "response";
    public static final String s_correct = "correct";
    public static final String s_canonical = "canonical";
    public static final String s_hintUnlock = "hint_unlock";
    public static final String s_hintArray = "hints";
    public static final String s_hintTime = "time";
    public static final String s_hintID = "id";
    public static final String s_hintText = "text";
    public static final String s_hintCost = "cost";


    private JSONObject mData;

    public String mName;
    public String mAnswerFile;
    public int mAnswerFileVersion;
    public String mPuzzleButton;
    public String mPuzzleButtonText;
    public JSONObject mPuzzleButtonExtra;
    public String mCanonicalAnswer;
    public String mInstruction;
    public HashMap<String, AnswerInfo> mAnswers;
    public ArrayList<HintInfo> mHints;

    public PuzzleInfo() {
        mAnswers = new HashMap<String, AnswerInfo>();
        mHints = new ArrayList<HintInfo>();
    }

    // called by JSONFileReaderTask
    public void saveResult(JSONObject jo) {
        mData = jo;

        if (mData != null) {
            try {
                mAnswerFileVersion = mData.getInt(s_version);
                JSONArray ja = mData.getJSONArray(s_answerArray);
                int len = ja.length();

                for (int i = 0; i < len; i++) {
                    JSONObject ao = (JSONObject) ja.get(i);
                    AnswerInfo ai = new AnswerInfo();
                    if (ao.has(s_response)) {
                        ai.mResponse = ao.getString(s_response);
                    }
                    if (ao.has(s_correct)) {
                        ai.mCorrect = ao.getBoolean(s_correct);
                    }
                    if (ao.has(s_hintUnlock)) {
                        ai.mHintUnlock = ao.getString(s_hintUnlock);
                    }
                    String answer = ao.getString(s_answer);
                    if (ao.has(s_canonical)) {
                        mCanonicalAnswer = answer;
                    }
                    //          Log.d("terngame", "answer: " + answer + ai.mResponse);
                    answer = AnswerChecker.stripAnswer(answer);
                    mAnswers.put(answer, ai);
                }

                if (mData.has(s_hintArray)) {
                    JSONArray hintArray = mData.getJSONArray(s_hintArray);
                    len = hintArray.length();

                    for (int i = 0; i < len; i++) {
                        JSONObject ho = (JSONObject) hintArray.get(i);
                        HintInfo hi = new HintInfo();
                        hi.mTimeSecs = ho.getLong(s_hintTime);
                        hi.mID = ho.getString(s_hintID);
                        hi.mNotificationID = -1;
                        hi.mText = ho.getString(s_hintText);
                        if (ho.has(s_hintCost)) {
                            hi.mCost = ho.getInt(s_hintCost);
                        }
                        addToHintsInOrder(hi);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize(Context context, JSONFileReaderTask.JSONFileReaderCompleteListener jfrcl) {
        try {

            InputStream in = context.getAssets().open(mAnswerFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this, jfrcl);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context,
                    "No answer file exists!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("terngame", "No answer file (" + mAnswerFile + ") exists!");

        } catch (IOException e) {

            Toast toast = Toast.makeText(context,
                    "IOException!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("terngame", "IOException");
        }
    }

    public String getName() {
        return mName;
    }

    public AnswerInfo getAnswerInfo(String answer) {
        return mAnswers.get(answer);
    }

    public String getCorrectAnswer() {
        return mCanonicalAnswer;
    }

    public ArrayList<HintInfo> getHintCopy() {
        return (ArrayList<HintInfo>) mHints.clone();
    }

    public JSONObject getExtra() {
        return mPuzzleButtonExtra;
    }

    // sorting is for pussies.
    private void addToHintsInOrder(HintInfo newHi) {
        int size = mHints.size();
        for (int i = 0; i < size; i++) {
            HintInfo hi = mHints.get(i);
            if (hi.mTimeSecs > newHi.mTimeSecs) {
                mHints.add(i, newHi);
                return;
            }
        }
        mHints.add(newHi);
    }

    public void unlockHints(String hintID) {
        boolean unlock = false;
        int size = mHints.size();
        for (int i = size - 1; i >= 0; i--) {
            HintInfo hi = mHints.get(i);
            if (hintID.equals(hi.mID)) {
                unlock = true;
            }

            if (unlock) {
                hi.mTimeSecs = 0;
                mHints.set(i, hi);
            }
        }
    }

    public void registerHintNotification(String hintID, int notificationID) {
        for (HintInfo hint : mHints) {
            if (hint.mID.equals(hintID)) {
                hint.mNotificationID = notificationID;
            }
        }
    }

    public int getHintNotificationID(String hintID) {
        for (HintInfo hint : mHints) {
            if (hint.mID.equals(hintID)) {
                return hint.mNotificationID;
            }
        }
        return -1;
    }
}
