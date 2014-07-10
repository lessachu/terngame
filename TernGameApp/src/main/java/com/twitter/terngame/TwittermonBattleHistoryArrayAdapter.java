package com.twitter.terngame;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

/**
 * Created by jchong on 2/16/14.
 */
public class TwittermonBattleHistoryArrayAdapter extends ArrayAdapter<TwittermonInfo.BattleInfo> {
    private final Context mContext;

    public TwittermonBattleHistoryArrayAdapter(Context context, ArrayList<TwittermonInfo.BattleInfo> values) {
        super(context, R.layout.twittermon_battle_row, values);
        mContext = context;
    }

    // TODO: try adding support for view recycling
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.twittermon_battle_row, parent, false);

        TwittermonInfo.BattleInfo bi = getItem(position);

        final int result = bi.mResult;
        String resultText = "";
        final String vsStr = " vs ";
        int spanStart = 0;
        int spanEnd = 0;

        switch (result) {
            case TwittermonInfo.s_win:
                resultText = "WIN";
                spanStart = 0;
                spanEnd = bi.mCreature.length();
                break;
            case TwittermonInfo.s_lose:
                resultText = "LOSE";
                spanStart = bi.mCreature.length() + vsStr.length();
                spanEnd = spanStart + bi.mOpponent.length();
                break;
            case TwittermonInfo.s_tie:
                resultText = "TIE";
                break;
        }

        final SpannableStringBuilder sb = new SpannableStringBuilder(bi.mCreature + vsStr + bi.mOpponent);
        if (spanEnd != 0) {
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            sb.setSpan(bss, spanStart, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        final TextView battleDescText = (TextView) rowView.findViewById(R.id.battle_text);
        battleDescText.setText(sb);

        final TextView battleResultText = (TextView) rowView.findViewById(R.id.battle_result_text);
        battleResultText.setText(resultText);

        return rowView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
