package com.twitter.terngame.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

    public static final String s_collected = "collected";
    public static final String s_history = "history";
    public static final String s_creatureInfo = "creature_info";
    public static final String s_creatureName = "name";
    public static final String s_creatureCode = "code";
    public static final String s_creatureType = "type";
    public static final String s_creaturePict = "image";
    public static final String s_filename = "twittermon_file";

    public static final int s_creature = 0;
    public static final int s_opponent = 1;
    public static final int s_result = 2;

    public final static int s_win = 1;
    public final static int s_lose = 2;
    public final static int s_tie = 3;

    public final static int[][] s_winChart =
            {{s_tie, s_lose, s_win}, {s_win, s_tie, s_lose}, {s_lose, s_win, s_tie}};

    private ArrayList<String> mCollected;
    private ArrayList<BattleInfo> mHistory;
    private HashMap<String, CreatureInfo> mCreatureDict;
    private Context mContext;
    private JSONObject mData;
    private Drawable mDefaultPict;

    public class CreatureInfo {
        public String mCode;
        public int mType;
        public Drawable mPict;
    }

    public class BattleInfo {

        public BattleInfo(String creature, String opponent, int result) {
            mCreature = creature;
            mOpponent = opponent;
            mResult = result;
        }

        public String mCreature;
        public String mOpponent;
        public int mResult;

    }

    public TwittermonInfo(Context context) {
        mContext = context;
        mCollected = new ArrayList<String>();
        mHistory = new ArrayList<BattleInfo>();
        mCreatureDict = new HashMap<String, CreatureInfo>();
        mData = new JSONObject();
        mDefaultPict = mContext.getResources().getDrawable(R.drawable.rockdove);
    }

    public void saveResult(JSONObject jo) {
        if (jo != null) {
            try {
                JSONArray ja = jo.getJSONArray(s_creatureInfo);
                int len = ja.length();
                for (int i = 0; i < len; i++) {
                    JSONObject co = ja.getJSONObject(i);

                    CreatureInfo ci = new CreatureInfo();
                    ci.mCode = co.getString(s_creatureCode);
                    ci.mType = co.getInt(s_creatureType);

                    String drawableFileName = co.getString(s_creaturePict);
                    int resourceID = mContext.getResources().getIdentifier(drawableFileName, "drawable",
                            mContext.getPackageName());
                    ci.mPict = mContext.getResources().getDrawable(resourceID);
                    mCreatureDict.put(co.getString(s_creatureName), ci);

                    Log.d("terngame", "Adding entry for " + co.getString(s_creatureName) + " : " +
                            ci.mCode);

                }
            } catch (JSONException e) {
                Log.e("terngame", "JSONException reading in Twittermon info");
                e.printStackTrace();
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

                ja = mData.getJSONArray(s_history);
                len = ja.length();
                for (int i = 0; i < len; i++) {
                    JSONArray ba = ja.getJSONArray(i);

                    BattleInfo bi = new BattleInfo(ba.getString(s_creature),
                            ba.getString(s_opponent), ba.getInt(s_result));
                    mHistory.add(bi);
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

        JSONArray battleArray = new JSONArray();
        for (BattleInfo bi : mHistory) {
            JSONArray battle = new JSONArray();
            battle.put(bi.mCreature);
            battle.put(bi.mOpponent);
            battle.put(bi.mResult);

            battleArray.put(battle);
        }

        if (mData == null) {
            Log.e("terngame", "Hrm, the save data is corrupted.  Starting anew");
            mData = new JSONObject();
        }

        try {
            mData.put(s_collected, creatureArray);
            mData.put(s_history, battleArray);

        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public Drawable getCreatureDrawable(String creature) {
        if (mCreatureDict.containsKey(creature)) {
            Log.d("terngame", "looking up image for " + creature);
            return mCreatureDict.get(creature).mPict;
        } else {
            return mDefaultPict;
        }
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

    public int battle(String creature1, String creature2) {
        Log.d("terngame", "battle: " + creature1 + " and " + creature2);

        CreatureInfo ci1 = mCreatureDict.get(creature1);
        CreatureInfo ci2 = mCreatureDict.get(creature2);

        if (ci1 != null && ci2 != null) {

            int type1 = mCreatureDict.get(creature1).mType;
            int type2 = mCreatureDict.get(creature2).mType;

            // R = 1, P = 2, S = 3
            return s_winChart[type1 - 1][type2 - 1];
        }
        return s_win;
    }

    public void logBattle(String creature1, String creature2, int result) {
        BattleInfo bi = new BattleInfo(creature1, creature2, result);
        mHistory.add(bi);
    }

    public ArrayList<String> getCollectedList() {
        return mCollected;
    }

    public ArrayList<BattleInfo> getBattleList() {
        return mHistory;
    }

    public void clearSavedData() {
        mCollected.clear();
        mHistory.clear();
    }
}
