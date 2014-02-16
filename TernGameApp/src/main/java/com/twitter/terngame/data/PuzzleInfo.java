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
import java.util.HashMap;

/**
 * Created by jchong on 2/4/14.
 */
public class PuzzleInfo implements JSONFileResultHandler {

    public static String s_version = "version";
    public static String s_answerArray = "answer_list";
    public static String s_answer = "answer";
    public static String s_response = "response";
    public static String s_correct = "correct";
    public static String s_canonical = "canonical";

    private JSONObject mData;

    public String mName;
    public String mAnswerFile;
    public int mAnswerFileVersion;
    public String mCanonicalAnswer;
    public String mInstruction;
    public HashMap<String, AnswerInfo> mAnswers;

    public PuzzleInfo() {
        mAnswers = new HashMap<String, AnswerInfo>();
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
                    String answer = ao.getString(s_answer);
                    if (ao.has(s_canonical)) {
                        mCanonicalAnswer = answer;
                    }
                    Log.d("terngame", "answer: " + answer + ai.mResponse);
                    answer = AnswerChecker.stripAnswer(answer);
                    mAnswers.put(answer, ai);
                }
            } catch (JSONException e) {
                Log.e("jan", "JsonException loading answerdata");
            }
        }
    }

    public void initialize(Context context) {
        try {

            InputStream in = context.getAssets().open(mAnswerFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context,
                    "No answer file exists!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("jan", "No answer file (" + mAnswerFile + ") exists!");

        } catch (IOException e) {

            Toast toast = Toast.makeText(context,
                    "IOException!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("jan", "IOException");

        }
    }

    public AnswerInfo getAnswerInfo(String answer) {
        return mAnswers.get(answer);
    }

    public String getCorrectAnswer() {
        return mCanonicalAnswer;
    }

}
