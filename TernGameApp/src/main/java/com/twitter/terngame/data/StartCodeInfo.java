package com.twitter.terngame.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
public class StartCodeInfo implements JSONFileResultHandler {

    public static String s_version = "version";
    public static String s_startCodeArray = "start_codes";
    public static String s_startCode = "id";
    public static String s_puzzleName = "name";
    public static String s_answerFile = "answer_file";

    private JSONObject mData;

    private Context mContext;
    private int mVersion;
    private HashMap<String,PuzzleInfo> mStartCodes;

    public StartCodeInfo(Context context) {
        mContext = context;
        mStartCodes = new HashMap<String,PuzzleInfo>();
    }

    // called by JSONFileReaderTask
    public void saveResult(JSONObject jo) {
        mData = jo;

        if(mData != null) {
            try {
                mVersion = mData.getInt(s_version);
                JSONArray ja = mData.getJSONArray(s_startCodeArray);
                int len = ja.length();

                for( int i = 0; i < len; i++) {
                    JSONObject po = (JSONObject) ja.get(i);
                    PuzzleInfo pi = new PuzzleInfo();
                    pi.mName = po.getString(s_puzzleName);
                    pi.mAnswerFile = po.getString(s_answerFile);

                    String code = po.getString(s_startCode);

                    Log.d("terngame", "pID: " + code + pi.mName + pi.mAnswerFile);

                    mStartCodes.put(po.getString(s_startCode), pi);
                 }

                Log.d("terngame", "Version: " + Integer.toString(mVersion));
                initializeAnswers();
            } catch (JSONException e) {
                Log.e("jan", "JsonException loading eventdata");
            }
        }
    }

    public void initialize(Context context, String startCodeFile) {
        try {

            InputStream in = context.getAssets().open(startCodeFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context,
                    "No start code info file exists!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("jan", "No event info file");

        } catch (IOException e) {

            Toast toast = Toast.makeText(context,
                    "IOException in initializePuzzleData!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("jan", "IOException");

        }
    }

    public PuzzleInfo getPuzzleInfo(String startCode) {
        return mStartCodes.get(startCode);
    }

    public void initializeAnswers() {
        for(String code : mStartCodes.keySet()) {
            // walk through the array and pull in the answer set
            PuzzleInfo pi = mStartCodes.get(code);
            pi.initialize(mContext);
        }
    }

}
