package com.twitter.terngame.util;

/**
 * Created by jchong on 2/4/14.
 */
public class AnswerChecker {

    // strip whitespace, punctuation and case
    public static String stripAnswer(String answer) {
        return answer.replaceAll("\\W", "").toLowerCase();
    }
}
