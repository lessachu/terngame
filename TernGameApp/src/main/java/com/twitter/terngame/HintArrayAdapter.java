package com.twitter.terngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.twitter.terngame.data.HintInfo;

import java.util.ArrayList;

/**
 * Created by jchong on 2/16/14.
 */
public class HintArrayAdapter extends ArrayAdapter<HintInfo> {
    private final Context mContext;
    private final Session mSession;
    private final ArrayList<HintInfo> mValues;

    public HintArrayAdapter(Context context, ArrayList<HintInfo> values) {
        super(context, R.layout.hint_row, values);
        mContext = context;
        mSession = Session.getInstance(context);
        mValues = values;
    }

    // TODO: try adding support for view recycling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.status_row, parent, false);

       /* HintInfo ps = mValues.get(position);

        final TextView nameText = (TextView) rowView.findViewById(R.id.puzzle_name_text);
        nameText.setText(mSession.getPuzzleName(ps.mID));

        final TextView startCodeText = (TextView) rowView.findViewById(R.id.start_code_text);
        startCodeText.setText(ps.mID);

        final TextView timeSpentText = (TextView) rowView.findViewById(R.id.time_spent_text);
        String timeElapsed;
        if (ps.mEndTime != 0) {
            timeElapsed = DateUtils.formatElapsedTime((ps.mEndTime - ps.mStartTime) / 1000);
        } else {
            timeElapsed = DateUtils.formatElapsedTime((SystemClock.elapsedRealtime() -
                    ps.mStartTime) / 1000) + "+";
        }
        timeSpentText.setText(timeElapsed);

        final TextView statusText = (TextView) rowView.findViewById(R.id.status_text);
        String status = mContext.getString(R.string.in_progress_text);
        if (ps.mSolved) {
            status = mContext.getString(R.string.solved_text);
        } else if (ps.mSkipped) {
            status = mContext.getString(R.string.skipped_text);
        }
        statusText.setText(status);

        if (ps.mSolved || ps.mSkipped) {
            final TextView answerText = (TextView) rowView.findViewById(R.id.answer_text);
            answerText.setText(mSession.getCorrectAnswer(ps.mID));
        } else {
            final View answerRow = rowView.findViewById(R.id.answer_row);
            answerRow.setVisibility(View.GONE);
        }

        final TextView guessText = (TextView) rowView.findViewById(R.id.guess_text);
        guessText.setText(Integer.toString(ps.mGuesses.size()));

        final TextView hintText = (TextView) rowView.findViewById(R.id.hint_text);
        hintText.setText("0 of 0");  // TODO: change when hints are hooked up
*/
        return rowView;
    }
}
