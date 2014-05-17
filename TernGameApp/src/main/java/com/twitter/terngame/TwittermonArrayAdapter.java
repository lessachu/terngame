package com.twitter.terngame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jchong on 2/16/14.
 */
public class TwittermonArrayAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final Session mSession;
    private final ArrayList<String> mValues;

    public TwittermonArrayAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.twittermon_row, values);
        mContext = context;
        mSession = Session.getInstance(context);
        mValues = values;
    }

    // TODO: try adding support for view recycling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.twittermon_row, parent, false);

        String name = mValues.get(position);

        final TextView nameText = (TextView) rowView.findViewById(R.id.twittermon_text);
        nameText.setText(name);

        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
