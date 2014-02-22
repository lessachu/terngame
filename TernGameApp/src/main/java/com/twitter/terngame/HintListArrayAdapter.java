package com.twitter.terngame;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.terngame.data.HintInfo;

import java.util.ArrayList;

/**
 * Created by jchong on 2/16/14.
 */
public class HintListArrayAdapter extends ArrayAdapter<HintInfo>
        implements View.OnClickListener {
    private final Context mContext;
    private final Session mSession;
    private final String mPuzzleID;
    private final ArrayList<HintInfo> mValues;

    public HintListArrayAdapter(Context context, String puzzleID, ArrayList<HintInfo> values) {
        super(context, R.layout.hintlist_row, values);
        mContext = context;
        mSession = Session.getInstance(context);
        mPuzzleID = puzzleID;
        mValues = values;
    }

    // TODO: try adding support for view recycling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.hintlist_row, parent, false);

        HintInfo hi = mValues.get(position);
        final TextView hintTitle = (TextView) rowView.findViewById(R.id.hint_cost_text);

        if (hi.mCost != 0) {
            hintTitle.setText("Cost: " + Integer.toString(hi.mCost));
        } else {
            hintTitle.setVisibility(View.GONE);
        }

        final Button hintButton = (Button) rowView.findViewById(R.id.hint_button);
        hintButton.setText("Hint " + Integer.toString(position + 1));
        hintButton.setOnClickListener(this);
        hintButton.setTag(R.id.hint_info_key, hi);
        hintButton.setTag(R.id.hint_num_key, position + 1);

        // only enable the hintButton if the elapsed time is greater than the hintTime
        hintButton.setEnabled(mSession.puzzleSolved(mPuzzleID) ||
                mSession.puzzleSkipped(mPuzzleID) ||
                SystemClock.elapsedRealtime() >
                        mSession.getPuzzleStartTime(mPuzzleID) + (hi.mTimeSecs * 1000));

        return rowView;
    }

    public void onClick(View view) {
        // TODO: show an "are you sure? prompt?
        HintInfo hi = (HintInfo) view.getTag(R.id.hint_info_key);
        Integer position = (Integer) view.getTag(R.id.hint_num_key);

        Log.d("terngame", "Taking hint " + hi.mID + " for puzzle: " + mPuzzleID);
        mSession.hintTaken(mPuzzleID, hi.mID);

        Intent i = new Intent(mContext, HintActivity.class);
        i.putExtra(HintActivity.s_hint_title, "Hint " + position.toString() + ":");
        i.putExtra(HintActivity.s_puzzle_title, mSession.getPuzzleName(mPuzzleID));
        i.putExtra(HintActivity.s_hint_text, hi.mText);

        mContext.startActivity(i);
    }
}
