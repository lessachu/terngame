package com.twitter.terngame.data;

/**
 * Created by jchong on 2/7/14.
 */
public class Guess {

    int id;
    String puzzleId;
    String guess;

    public Guess() {

    }

    public Guess(int id, String puzzleId, String guess) {
        this.id = id;
        this.puzzleId = puzzleId;
        this.guess = guess;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public int getID() {
        return this.id;
    }

    public String getPuzzleId(){
        return this.puzzleId;
    }

    public String getGuess() {
        return this.guess;
    }
}
