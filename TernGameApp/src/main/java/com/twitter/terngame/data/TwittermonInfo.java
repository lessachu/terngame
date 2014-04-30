package com.twitter.terngame.data;

import com.twitter.terngame.R;

import java.util.ArrayList;

/**
 * Created by jchong on 3/22/14.
 */
public class TwittermonInfo {

    private ArrayList<String> mMenagerie;

    public TwittermonInfo() {
        mMenagerie = new ArrayList<String>();
    }

    // instantiate based on other field
    // save out to a single json string


    // better to return a drawable?  doesn't matter?
    public int getResourceId(String creature) {
//        return R.drawable.twittermon_default;
        return R.drawable.collect_fail;
    }

    public void addNewCreature(String creature) {
        mMenagerie.add(creature);
    }

    public boolean hasCreature(String creature) {
        return mMenagerie.contains(creature);
    }

}
