package com.twitter.terngame.util;

import android.util.Log;

import com.twitter.terngame.data.TwittermonInfo;

import java.util.HashSet;

/**
 * Created by jchong on 3/22/14.
 */
public class TwittermonBattleRoyalHelper {

    public final static int s_total = 10;

    private HashSet<String> mBattles;
    private int mCorrect;
    private TwittermonInfo mTwittermonInfo;

    public TwittermonBattleRoyalHelper(TwittermonInfo ti) {
        mTwittermonInfo = ti;
        mBattles = new HashSet<String>();
        mCorrect = 0;
    }

    public TwittermonInfo.BattleInfo getMatchup() {
        // generate a match that doesn't include what the user has against themselves
        // and no repeat match ups.
        TwittermonInfo.BattleInfo bi = null;
        boolean matchupReady = false;
        while (!matchupReady) {
            bi = mTwittermonInfo.getRandomMatchup();

            Log.d("terngame", "Random: " + bi.mCreature + " vs " + bi.mOpponent);

            // if it's between the same creature, forget it
            if (bi.mCreature.equals(bi.mOpponent)) {
                Log.d("terngame", "battle between same creature, try again");
                continue;
            }

            // if it's between two twittermon we've collected, try again
            if (mTwittermonInfo.hasCreature(bi.mCreature) &&
                    mTwittermonInfo.hasCreature(bi.mOpponent)) {
                Log.d("terngame", "we have both those creatures, trying again");
                continue;
            }

            String battleName;
            // if it's a battle we've already seen, try again
            if (bi.mCreature.compareTo(bi.mOpponent) < 0) {
                battleName = bi.mCreature + "+" + bi.mOpponent;
            } else {
                battleName = bi.mOpponent + "+" + bi.mCreature;
            }

            if (mBattles.contains(battleName)) {
                Log.d("terngame", "we've already seen this battle");
                continue;
            }

            matchupReady = true;
            mBattles.add(battleName);
        }
        return bi;
    }

    public void logCorrect() {
        mCorrect++;
    }

    public int getCorrect() {
        return mCorrect;
    }

    // TODO: HACKY MCHACKS ALOT
    public void setCorrect(int correct) {
        mCorrect = correct;
    }

    public void clearData() {
        mBattles.clear();
        mCorrect = 0;
    }
}
