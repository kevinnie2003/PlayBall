package com.example.playball;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class BallModel {
    private SharedPreferences prefs;

    public BallModel(Context context) {
        prefs = context.getSharedPreferences("BallPrefs", Context.MODE_PRIVATE);
    }

    public int getBallSize() {
        return prefs.getInt("BallSize", 100);
    }

    public int getBallColor() {
        return prefs.getInt("BallColor", Color.BLUE);
    }

    public boolean isGravityMode() {
        return prefs.getBoolean("GravityMode", false);
    }

    public void saveSettings(int ballSize, int ballColor, boolean isGravityMode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("BallSize", ballSize);
        editor.putInt("BallColor", ballColor);
        editor.putBoolean("GravityMode", isGravityMode);
        editor.apply();
    }
}
