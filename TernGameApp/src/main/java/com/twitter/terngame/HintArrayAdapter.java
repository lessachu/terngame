package com.twitter.terngame;

import android.content.Context;
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
        View rowView = inflater.inflate(R.layout.hint_row, parent, false);

        HintInfo hi = mValues.get(position);
        final TextView hintTitle = (TextView) rowView.findViewById(R.id.hint_cost_text);

        if (hi.mCost != 0) {
            hintTitle.setText("Cost: " + Integer.toString(hi.mCost));
        } else {
            hintTitle.setVisibility(View.GONE);
        }

        final Button hintButton = (Button) rowView.findViewById(R.id.hint_button);
        hintButton.setText("Hint " + Integer.toString(position));

        return rowView;
    }
}
