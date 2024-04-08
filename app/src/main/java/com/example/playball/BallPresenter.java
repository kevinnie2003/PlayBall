package com.example.playball;

import android.content.Context;

public class BallPresenter {
    private BallView view;
    private BallModel model;

    public BallPresenter(BallView view, Context context) {
        this.view = view;
        model = new BallModel(context);
    }

    public void onViewInitialized() {
        view.setBallRadius(model.getBallSize());
        view.setBallColor(model.getBallColor());
        view.setGravityMode(model.isGravityMode());
    }

    public void onSettingsChanged(int ballSize, int ballColor, boolean isGravityMode) {
        model.saveSettings(ballSize, ballColor, isGravityMode);
    }
}
