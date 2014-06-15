package com.twitter.terngame;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

public class TwittermonGridFragment extends Fragment {

    private ArrayList<String> mTwittermon;

    private TwittermonArrayAdapter mAdapter;
    private GridView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.twittermon_grid_fragment, container, false);

        final Activity activity = getActivity();
        if (activity != null) {
            final Context context = getActivity().getApplicationContext();

            Session s = Session.getInstance(context);
            PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
            TwittermonInfo ti = pei.getTwittermonInfo();

            mTwittermon = ti.getCollectedList();
            mAdapter = new TwittermonArrayAdapter(activity, mTwittermon);

            mGridView = (GridView) view.findViewById(R.id.twittermon_grid);
            mGridView.setAdapter(mAdapter);
        } else {
            Log.d("terngame", "Activity is null");
        }

        return view;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mAdapter.setClickListener(clickListener);
    }
}
