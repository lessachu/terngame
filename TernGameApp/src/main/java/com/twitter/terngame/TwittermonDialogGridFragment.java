package com.twitter.terngame;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
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

public class TwittermonDialogGridFragment extends DialogFragment {

    private ArrayList<String> mTwittermon;

    private TwittermonArrayAdapter mAdapter;
    private GridView mGridView;
    private TwittermonGridSelectionListener mListener;

    public interface TwittermonGridSelectionListener {
        public void onTwittermonGridSelection(String creature);
    }

    static TwittermonDialogGridFragment newInstance() {
        return new TwittermonDialogGridFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.twittermon_grid_fragment, container, false);

        Log.d("terngame", "I'm in onCreatEview in the DialogGridFragment");

        final Activity activity = getActivity();
        if (activity != null) {
            final Context context = getActivity().getApplicationContext();

            Session s = Session.getInstance(context);
            PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
            TwittermonInfo ti = pei.getTwittermonInfo();

            mTwittermon = ti.getCollectedList();
            mAdapter = new TwittermonArrayAdapter(activity, mTwittermon);
            mAdapter.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TwittermonDialogGridFragment.this.mListener != null) {
                        TwittermonDialogGridFragment.this.mListener.onTwittermonGridSelection((String) v.getTag(R.id.grid_name));
                    }
                }
            });

            mGridView = (GridView) view.findViewById(R.id.twittermon_grid);
            mGridView.setAdapter(mAdapter);
        } else {
            Log.d("terngame", "Activity is null");
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.opponent_grid_title);
        return dialog;
    }

    public void setSelectionListener(TwittermonGridSelectionListener selectionListener) {
        mListener = selectionListener;
    }

    public void refreshFragment() {
        mAdapter.notifyDataSetChanged();
    }
}
