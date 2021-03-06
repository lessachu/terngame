package com.twitter.terngame.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.twitter.terngame.util.JSONFileReaderTask;
import com.twitter.terngame.util.JSONFileResultHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jchong on 1/25/14.
 */
public class EventInfo implements JSONFileResultHandler {

    public static final String s_eventFile = "event.json";

    public static final String s_version = "version";
    public static final String s_eventName = "name";
    public static final String s_codeFile = "code_file";
    public static final String s_wrongStr = "wrong_str";
    public static final String s_dupeStr = "dupe_str";
    public static final String s_skipCode = "skip_code";

    private JSONObject mData;

    // data fields
    public int mVersion;
    public String mEventName;
    public String mStartCodeFileName;
    public String mWrongAnswerStr;
    public String mDuplicateAnswerStr;
    public String mSkipCode;

    public EventInfo() {
    }

    // called by JSONFileReaderTask
    public void saveResult(JSONObject jo) {
        mData = jo;

        if (mData != null) {
            try {
                mVersion = mData.getInt(s_version);
                mEventName = mData.getString(s_eventName);
                mStartCodeFileName = mData.getString(s_codeFile);
                mWrongAnswerStr = mData.getString(s_wrongStr);
                mDuplicateAnswerStr = mData.getString(s_dupeStr);
                mSkipCode = mData.getString(s_skipCode);

            } catch (JSONException e) {
                Log.e("terngame", "JsonException loading eventdata");
            }
        }
    }

    public void initializeEvent(Context context, JSONFileReaderTask.JSONFileReaderCompleteListener jfrcl) {
        try {

            InputStream in = context.getAssets().open(s_eventFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this, jfrcl);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context,
                    "No event info file exists!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("terngame", "No event info file");

        } catch (IOException e) {

            Toast toast = Toast.makeText(context,
                    "IOException!",
                    Toast.LENGTH_SHORT);
            toast.show();
            Log.e("terngame", "IOException");

        }
    }

    public int getVersion() {
        return mVersion;
    }

    public String getEventName() {
        return mEventName;
    }

    public String getStartCodeFileName() {
        return mStartCodeFileName;
    }

    public String getWrongAnswerString() {
        return mWrongAnswerStr;
    }

    public String getDuplicateAnswerString() {
        return mDuplicateAnswerStr;
    }

    public String getSkipCode() {
        return mSkipCode;
    }
}
