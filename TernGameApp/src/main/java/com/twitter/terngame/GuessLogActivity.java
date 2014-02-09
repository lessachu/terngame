package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class GuessLogActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guess_log);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> guessArray = new ArrayList<String>();
        if (extras != null) {
            guessArray = extras.getStringArrayList("guesses");
        }

        if (guessArray != null) { // should really never be the case
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, guessArray);
            setListAdapter(adapter);
        }
    }


}
