package com.twitter.terngame;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jchong on 2/11/14.
 */

public class AdminDataActivity extends BaseActivity {

    public static final String s_puzzleID = "puzzleID";
    private TextView mInstruction;
    private TextView mStartCode;
    private TextView mPuzzleName;
    private ListView mAnswerListView;
    private ListView mPartialListView;
    private ListView mHintListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_data_activity);

        mPuzzleName = (TextView) findViewById(R.id.puzzle_name_text);
        mInstruction = (TextView) findViewById(R.id.status_text);
        mStartCode = (TextView) findViewById(R.id.start_code_text);
        mAnswerListView = (ListView) findViewById(R.id.answer_list);
        mPartialListView = (ListView) findViewById(R.id.partial_list);
        mHintListView = (ListView) findViewById(R.id.hint_list);
    }

    public void showUX() {
        super.showUX();

        Bundle extras = getIntent().getExtras();
        String puzzleID = null;
        if (extras != null) {
            puzzleID = extras.getString(s_puzzleID);
        }

        if (puzzleID != null) {
            Session session = Session.getInstance(this);
            mPuzzleName.setText(session.getPuzzleName(puzzleID));
            mStartCode.setText(puzzleID);
            mInstruction.setText(session.getInstruction(puzzleID));

            ArrayList<Pair<String,String>> answerList = new ArrayList<Pair<String,String>>();
            answerList.add(new Pair<String,String>("answer", "response"));
            answerList.add(new Pair<String,String>("answer2", "a much longer response that takes a lot of space"));

            AdminAnswerArrayAdapter answers = new AdminAnswerArrayAdapter(this, answerList);
            mAnswerListView.setAdapter(answers);

            ArrayList<Pair<String,String>> partialList = new ArrayList<Pair<String,String>>();
            partialList.add(new Pair<String,String>("partial1", "response"));
            partialList.add(new Pair<String,String>("partial2", "a much longer response that takes a lot of space"));

            AdminAnswerArrayAdapter partials = new AdminAnswerArrayAdapter(this, partialList);
            mPartialListView.setAdapter(partials);


            ArrayList<Pair<String,String>> hintList = session.getHints(puzzleID);

            AdminAnswerArrayAdapter hints = new AdminAnswerArrayAdapter(this, hintList);
            mHintListView.setAdapter(hints);

        }
    }

}

