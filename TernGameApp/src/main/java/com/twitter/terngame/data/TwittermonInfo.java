package com.twitter.terngame.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.twitter.terngame.R;
import com.twitter.terngame.Session;
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

/**
 * Created by jchong on 3/22/14.
 */
public class TwittermonInfo implements JSONFileResultHandler {

    public static String s_collected = "collected";
    public static String s_creatureInfo = "creature_info";
    public static String s_creatureName = "name";
    public static String s_creatureCode = "code";
    public static String s_creatureType = "type";
    public static String s_creaturePict = "image";
    public static String s_filename = "twittermon_file";

    private ArrayList<String> mCollected;
    private HashMap<String, CreatureInfo> mCreatureDict;
    private Context mContext;
    private JSONObject mData;

    public class CreatureInfo {
        public String mCode;
        public int mType;
        public String mPict;
    }

    public TwittermonInfo(Context context) {
        mContext = context;
        mCollected = new ArrayList<String>();
        mCreatureDict = new HashMap<String, CreatureInfo>();
        mData = new JSONObject();
    }

    public void saveResult(JSONObject jo) {
        if (jo != null) {
            try {
                JSONArray ja = jo.getJSONArray(s_creatureInfo);
                int len = ja.length();
                for (int i = 0; i < len; i++) {
                    JSONObject co = (JSONObject) ja.getJSONObject(i);

                    CreatureInfo ci = new CreatureInfo();
                    ci.mCode = co.getString(s_creatureCode);
                    ci.mType = co.getInt(s_creatureType);
                    ci.mPict = co.getString(s_creaturePict);

                    mCreatureDict.put(co.getString(s_creatureName), ci);

                    Log.d("terngame", "Adding entry for " + co.getString(s_creatureName) + " : " +
                            ci.mCode);

                }
            } catch (JSONException e) {
                Log.e("terngame", "JSONException reading in Twittermon info");
            }
        }
    }

    public void initialize(JSONObject jo) {

        String filename = null;
        if (jo != null) {
            try {
                filename = jo.getString(s_filename);
            } catch (JSONException e) {
                Log.e("terngame", "JSONException reading in Twittermon info filename");
            }
        }

        if (filename != null) {
            try {

                InputStream in = mContext.getAssets().open(filename);
                JSONFileReaderTask readerTask = new JSONFileReaderTask(this);
                readerTask.execute(in);

            } catch (FileNotFoundException e) {
                Toast toast = Toast.makeText(mContext,
                        "No Twittermon file exists!",
                        Toast.LENGTH_SHORT);
                toast.show();
                Log.e("terngame", "No Twittermon file (" + filename + ") exists!");

            } catch (IOException e) {

                Toast toast = Toast.makeText(mContext,
                        "IOException!",
                        Toast.LENGTH_SHORT);
                toast.show();
                Log.e("terngame", "IOException");
            }
        }
    }

    public void initializePuzzleStatus(JSONObject jo) {
        mData = jo;
        mCollected.clear();

        if (mData != null) {
            try {
                JSONArray ja = mData.getJSONArray(s_collected);
                int len = ja.length();
                for (int i = 0; i < len; i++) {
                    String creature = ja.getString(i);
                    mCollected.add(creature);
                }
            } catch (JSONException e) {
                Log.e("terngame", "JSONException reading in Twittermon status");
            }
        }
    }

    private boolean updateJSONData() {

        JSONArray creatureArray = new JSONArray();

        for (String creature : mCollected) {
            creatureArray.put(creature);
        }

        if (mData == null) {
            Log.e("terngame", "Hrm, the save data is corrupted.  Starting anew");
            mData = new JSONObject();
        }

        try {
            mData.put(s_collected, creatureArray);

        } catch (JSONException e) {
            return false;
        }
        return true;
    }


    // better to return a drawable?  doesn't matter?
    public int getResourceId(String creature) {
//        return R.drawable.twittermon_default;
        return R.drawable.collect_fail;
    }

    public void addNewCreature(String creature) {
        mCollected.add(creature);
        updateJSONData();
        Session.getInstance(mContext).updateExtra("twittermon", mData);
    }

    public boolean verifyTrapCode(String creature, String code) {
        if (mCreatureDict.containsKey(creature)) {
//            Log.d("terngame", "real code is: " + mCreatureDict.get(creature).mCode);
            return mCreatureDict.get(creature).mCode.equalsIgnoreCase(code);
        }
        return false;
    }

    public boolean hasCreature(String creature) {
        return mCollected.contains(creature);
    }

    public ArrayList<String> getCollectedList() {
        return mCollected;
    }
}
