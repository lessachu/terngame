package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class GuessLogActivity extends ListActivity {

    public static final String s_guess_key = "guesses";

    private ListView mGuessList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guess_log);

        mGuessList = (ListView) findViewById(android.R.id.list);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> guessArray = new ArrayList<String>();
        if (extras != null) {
            guessArray = extras.getStringArrayList(s_guess_key);
        }

        if (guessArray != null) { // should really never be the case
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, guessArray);
            setListAdapter(adapter);

            mGuessList.post(new Runnable() {
                @Override
                public void run() {
                    mGuessList.setSelection(adapter.getCount() - 1);
                }
            });
        }


    }

}
