package com.twitter.terngame;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jchong on 2/16/14.
 */
public class AdminAnswerArrayAdapter extends ArrayAdapter<Pair<String,String>> {
    private final Context mContext;

    public AdminAnswerArrayAdapter(Context context, ArrayList<Pair<String, String>> values) {
        super(context, R.layout.admin_answerlist_row, values);
        mContext = context;
    }

    // TODO: try adding support for view recycling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.admin_answerlist_row, parent, false);

        Pair<String, String> answerInfo = getItem(position);

        final TextView wordText = (TextView) rowView.findViewById(R.id.answer_word_text);
        wordText.setText(answerInfo.first);

        final TextView responseText = (TextView) rowView.findViewById(R.id.answer_response_text);
        responseText.setText(answerInfo.second);

        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
