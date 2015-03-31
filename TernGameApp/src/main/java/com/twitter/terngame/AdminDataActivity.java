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
    private ListView mAnswerDataListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_data_activity);

        mAnswerDataListView= (ListView) findViewById(R.id.data_list);
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

            ArrayList<Pair<String,String>> fullList = new ArrayList<>();
            fullList.add(new Pair<String,String>(session.getPuzzleName(puzzleID),null));
            fullList.add(new Pair<>("START CODE:",puzzleID));
            fullList.add(new Pair<>("INSTRUCTION:",session.getInstruction(puzzleID)));
            fullList.add(new Pair<String,String>("ANSWERS", null));
            fullList.addAll(session.getAnswers(puzzleID));
            fullList.add(new Pair<String,String>("PARTIALS",null));
            fullList.addAll(session.getPartials(puzzleID));
            fullList.add(new Pair<String,String>("HINTS", null));
            fullList.addAll(session.getHints(puzzleID));

            AdminAnswerArrayAdapter answers = new AdminAnswerArrayAdapter(this, fullList);
            mAnswerDataListView.setAdapter(answers);
        }
    }

}

