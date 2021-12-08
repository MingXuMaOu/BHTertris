package com.example.bhtertris.model;

public class ScoreModel {
    public static int score;

    public void addScore(int lines) {
        if(lines == 0){
            return;
        }
        int add = lines + (lines - 1);
        score += add;
    }
}
