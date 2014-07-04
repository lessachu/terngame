package com.twitter.terngame.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.twitter.terngame.data.TwittermonInfo;

import java.util.HashSet;

/**
 * Created by jchong on 3/22/14.
 */
public class TwittermonBattleRoyalHelper implements Parcelable {

    public final static int s_total = 5;

    private HashSet<String> mBattles;
    private int mCorrect;
    private TwittermonInfo mTwittermonInfo;

    public TwittermonBattleRoyalHelper() {
        mBattles = new HashSet<String>();
        mCorrect = 0;
        mTwittermonInfo = null;
    }

    public void setTwittermonInfo(TwittermonInfo ti) {
        mTwittermonInfo = ti;
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

    public void clearData() {
        mBattles.clear();
        mCorrect = 0;
    }

    // Parcelable
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mCorrect);
        out.writeInt(mBattles.size());
        for (String ba : mBattles) {
            out.writeString(ba);
        }
    }

    public static final Parcelable.Creator<TwittermonBattleRoyalHelper> CREATOR
            = new Parcelable.Creator<TwittermonBattleRoyalHelper>() {
        public TwittermonBattleRoyalHelper createFromParcel(Parcel in) {
            return new TwittermonBattleRoyalHelper(in);
        }

        public TwittermonBattleRoyalHelper[] newArray(int size) {
            return new TwittermonBattleRoyalHelper[size];
        }
    };

    private TwittermonBattleRoyalHelper(Parcel in) {
        mCorrect = in.readInt();
        final int numBattles = in.readInt();
        mBattles = new HashSet<String>();

        for (int i = 0; i < numBattles; i++) {
            String ba = in.readString();
            mBattles.add(ba);
        }

        mTwittermonInfo = null;
    }

}
