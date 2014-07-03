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

public class LoginInfo implements JSONFileResultHandler {

    public static final String s_version = "version";
    public static final String s_teamList = "teams";
    public static final String s_teamName = "name";
    public static final String s_teamPass = "password";

    private JSONObject mData;
    public int mVersion;
    public HashMap<String, String> mLoginInfo;

    public LoginInfo() {
        mLoginInfo = new HashMap<String, String>();
    }

    // called by JSONFileReaderTask
    public void saveResult(JSONObject jo) {
        mData = jo;
        if (mData != null) {
            try {
                mVersion = mData.getInt(s_version);

                JSONArray ja = mData.getJSONArray(s_teamList);
                int len = ja.length();
                for (int i = 0; i < len; i++) {
                    JSONObject teamObj = ja.getJSONObject(i);
                    String teamName = teamObj.getString(s_teamName);
                    String password = teamObj.getString(s_teamPass);
                    mLoginInfo.put(teamName, password);
                }
                Log.d("terngame", mLoginInfo.toString());
            } catch (JSONException e) {
                Log.e("terngame", "JsonException loading teamdata");
            }
        }
    }

    public void initialize(Context context, String dataFile) {
        try {

            Log.d("terngame", "LoginInfo Datafile: " + dataFile);
            InputStream in = context.getAssets().open(dataFile);
            JSONFileReaderTask readerTask = new JSONFileReaderTask(this, null);
            readerTask.execute(in);

        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(context,
                    "No team info file exists!",
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

    public boolean isValidLogin(String teamName, String password) {
        final String teamPass = mLoginInfo.get(teamName);
        return teamPass != null && teamPass.equals(password);
    }

}
