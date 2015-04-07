package com.twitter.terngame.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jchong on 2/7/14.
 */
public class TerngameDatabaseHelper extends SQLiteOpenHelper {

    // db name
    public static final String DATABASE_NAME = "terngame_data";
    // table names
    public static final String TABLE_GUESSES = "guesses";
    // common column names
    public static final String KEY_ID = "id";

    // guess table column names
    public static final String KEY_PUZZLE_ID = "puzzle_id";
    public static final String KEY_GUESS = "guess";


    private static final String CREATE_TABLE_GUESSES = "CREATE TABLE "
            + TABLE_GUESSES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PUZZLE_ID + " STRING," + KEY_GUESS + " STRING" + ")";


    public TerngameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GUESSES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUESSES);

        // create new tables
        onCreate(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public long createGuess(Guess guess, long[] tag_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, guess.getID());
        values.put(KEY_PUZZLE_ID, guess.getPuzzleId());
        values.put(KEY_GUESS, guess.getGuess());

        // insert row
        long guess_id = db.insert(TABLE_GUESSES, null, values);

        return guess_id;
    }


    public List<Guess> getAllGuessByPuzzleID(String puzzleID) {
        List<Guess> guesses = new ArrayList<Guess>();
        String selectQuery = "SELECT  * FROM " + TABLE_GUESSES +
                " WHERE " + KEY_PUZZLE_ID  + " = '" + puzzleID +
                "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Guess g = new Guess();
                g.setID(c.getInt((c.getColumnIndex(KEY_ID))));
                g.setPuzzleId((c.getString(c.getColumnIndex(KEY_PUZZLE_ID))));
                g.setGuess(c.getString(c.getColumnIndex(KEY_GUESS)));

                 guesses.add(g);
            } while (c.moveToNext());
        }

        return guesses;
    }

    public void deleteAllGuessesByPuzzleID(String puzzleID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_GUESSES, KEY_PUZZLE_ID + " = ?",
                new String[] { puzzleID });
    }
}
