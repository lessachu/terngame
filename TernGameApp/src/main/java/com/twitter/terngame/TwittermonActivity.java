package com.twitter.terngame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

public class TwittermonActivity extends Activity
        implements View.OnClickListener, TwittermonDialogGridFragment.TwittermonGridSelectionListener {

    public static final String s_new_creature = "new_creature";
    public static final int s_total_to_collect = 6;

    private ArrayList<String> mTwittermon;

    private FragmentManager mFragmentManager;
    private TwittermonDialogGridFragment mGridFragment;

    private LinearLayout mNoTwittermonLayout;
    private TextView mCollectPrompt;
    private TextView mTitle;
    private LinearLayout mBattleButtons;
    private Button mHistoryButton;
    private Button mFinaleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_activity);

        Session s = Session.getInstance(this);
        PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        final Context context = this;

        mTwittermon = ti.getCollectedList();

        mFragmentManager = getFragmentManager();
        mGridFragment = (TwittermonDialogGridFragment) mFragmentManager.findFragmentById(R.id.twittermon_grid);
        mGridFragment.setSelectionListener(this);

        mBattleButtons = (LinearLayout) findViewById(R.id.battle_buttons);
        mHistoryButton = (Button) findViewById(R.id.battle_history);
        mHistoryButton.setOnClickListener(this);

        mFinaleButton = (Button) findViewById(R.id.battle_finale);
        mFinaleButton.setOnClickListener(this);

        mTitle = (TextView) findViewById(R.id.twittermon_collection_title_text);
        mCollectPrompt = (TextView) findViewById(R.id.collect_prompt);
        mNoTwittermonLayout = (LinearLayout) findViewById(R.id.empty_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (mTwittermon.isEmpty()) {
            mNoTwittermonLayout.setVisibility(View.VISIBLE);
            ft.hide(mGridFragment);
            mTitle.setVisibility(View.GONE);
            mCollectPrompt.setVisibility(View.GONE);
            mBattleButtons.setVisibility(View.GONE);
        } else {
            mNoTwittermonLayout.setVisibility(View.GONE);
            ft.show(mGridFragment);
            mTitle.setVisibility(View.VISIBLE);

            // only show the collect prompt if there are more to collect
            if (mTwittermon.size() < TwittermonInfo.s_max_collect) {
                mCollectPrompt.setVisibility(View.VISIBLE);
            } else {
                mCollectPrompt.setVisibility(View.GONE);
            }
            mBattleButtons.setVisibility(View.VISIBLE);
        }
        ft.commit();
    }


    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.battle_history) {
            Intent i = new Intent(this, TwittermonBattleHistoryActivity.class);
            startActivity(i);
        } else if (id == R.id.battle_finale) {

            if (mTwittermon.size() < s_total_to_collect) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Come back later");
                alertDialogBuilder.setMessage("You need to collect all six Twittermon before you can enter the Battle Royale!")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            } else {
                Intent i = new Intent(this, TwittermonBattleRoyaleActivity.class);
                startActivity(i);
            }
        }
    }

    public void onTwittermonGridSelection(String creature) {
        Intent i = new Intent(this, TwittermonBattleActivity.class);
        i.putExtra(TwittermonBattleActivity.s_creature, creature);
        startActivityForResult(i, TwittermonBattleActivity.NEW_CREATURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TwittermonBattleActivity.NEW_CREATURE_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null) {
            Boolean fNewCreature = data.getBooleanExtra(s_new_creature, false);
            if (fNewCreature) {
                mGridFragment.refreshFragment();
            }
        }
    }
}
