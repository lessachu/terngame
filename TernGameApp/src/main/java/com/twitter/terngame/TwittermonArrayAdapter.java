package com.twitter.terngame;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jchong on 2/16/14.
 */
public class TwittermonArrayAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final Session mSession;
    private final ArrayList<String> mValues;
    private View.OnClickListener mClickListener;

    private static final int s_name = 1;

    public TwittermonArrayAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.twittermon_grid_single, values);
        mContext = context;
        mSession = Session.getInstance(context);
        mValues = values;
    }

    // TODO: try adding support for view recycling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = inflater.inflate(R.layout.twittermon_grid_single, parent, false);

        String name = mValues.get(position);
        gridView.setTag(R.id.grid_name, name);

        final TextView nameText = (TextView) gridView.findViewById(R.id.twittermon_text);
        nameText.setText(name);

        final ImageView image = (ImageView) gridView.findViewById(R.id.twittermon_image);
        image.setImageResource(mSession.getTwittermonImage(name));

        if (mClickListener != null) {
            gridView.setOnClickListener(mClickListener);
        } else {
            Log.d("terngame", "mClickListener is null here");
        }
        return gridView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

}
