package com.example.playball;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends Activity implements SensorEventListener, BallView {
    private BallPresenter presenter;
    private SharedPreferences prefs;
    private RelativeLayout layout;
    private BallViewImpl ballView;
    private boolean isGravityMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("BallPrefs", MODE_PRIVATE);
        presenter = new BallPresenter(this, this);

        layout = new RelativeLayout(this);
        ballView = new BallViewImpl(this);
        layout.addView(ballView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        Button settingsButton = new Button(this);
        settingsButton.setText("设置");
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        settingsButton.setLayoutParams(buttonParams);
        settingsButton.setOnClickListener(v -> showSettingsDialog());
        layout.addView(settingsButton);

        setContentView(layout);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onViewInitialized();
    }


    public void onSensorChanged(SensorEvent event) {
        if (isGravityMode) {
            float x = event.values[0];
            float y = event.values[1];
            ballView.updatePosition(-x, y);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    public void setBallRadius(int radius) {
        ballView.setBallRadius(radius);
    }

    @Override
    public void setBallColor(int color) {
        ballView.setBallColor(color);
    }

    @Override
    public void setGravityMode(boolean enabled) {
        isGravityMode = enabled;
    }


    private void showSettingsDialog() {
        // Settings dialog implementation including the gravity mode toggle
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final SeekBar sizeSeekBar = new SeekBar(this);
        sizeSeekBar.setMax(200);
        sizeSeekBar.setProgress(ballView.getBallRadius());
        layout.addView(sizeSeekBar);

        final String[] colors = {"红色", "蓝色", "绿色", "黄色"};
        final int[] colorValues = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

        Button colorButton = new Button(this);
        colorButton.setText("选择颜色");
        colorButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("选择颜色")
                    .setItems(colors, (dialog, which) -> {
                        ballView.setBallColor(colorValues[which]);
                        saveSettings(ballView.getBallRadius(), colorValues[which]);
                    })
                    .show();
        });
        layout.addView(colorButton);

        Button toggleModeButton = new Button(this);
        toggleModeButton.setText(isGravityMode ? "切换为触控模式" : "切换为重力模式");
        toggleModeButton.setOnClickListener(v -> {
            isGravityMode = !isGravityMode;
            toggleModeButton.setText(isGravityMode ? "切换为触控模式" : "切换为重力模式");
            ballView.setGravityMode(isGravityMode);
        });
        layout.addView(toggleModeButton);

        new AlertDialog.Builder(this)
                .setTitle("调整大小")
                .setView(layout)
                .setPositiveButton("确定", (dialog, which) -> {
                    int newSize = sizeSeekBar.getProgress();
                    ballView.setBallRadius(newSize);
                    saveSettings(newSize, ballView.getBallColor());
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveSettings(int ballSize, int ballColor) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("BallSize", ballSize);
        editor.putInt("BallColor", ballColor);
        editor.putBoolean("GravityMode", isGravityMode);
        editor.apply();
    }

}
