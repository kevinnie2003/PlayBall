package com.example.playball;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BallViewImpl extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean followTouch = true;
    private float x = 500, y = 500; // Default position, adjust based on screen size
    private int ballRadius = 100; // Default radius
    private int ballColor = Color.BLUE; // Default color
    private SharedPreferences prefs;

    public BallViewImpl(Context context) {
        super(context);
        init(context);
    }

    public BallViewImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BallViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        prefs = context.getSharedPreferences("BallPrefs", Context.MODE_PRIVATE);
        loadBallPosition();
        paint.setColor(ballColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(x, y, ballRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (followTouch) {
            x = event.getX();
            y = event.getY();
            // Make sure we draw the ball within the boundaries of the view
            x = Math.max(ballRadius, Math.min(x, getWidth() - ballRadius));
            y = Math.max(ballRadius, Math.min(y, getHeight() - ballRadius));
            saveBallPosition();
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void updatePosition(float xAccel, float yAccel) {
        if (!followTouch) {
            x += xAccel * 20; // Adjust the multiplier for sensitivity
            y += yAccel * 20;
            // Make sure we draw the ball within the boundaries of the view
            x = Math.max(ballRadius, Math.min(x, getWidth() - ballRadius));
            y = Math.max(ballRadius, Math.min(y, getHeight() - ballRadius));
            saveBallPosition();
            invalidate();
        }
    }

    public void setGravityMode(boolean enabled) {
        this.followTouch = !enabled;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("GravityMode", !enabled);
        editor.apply();
    }

    public void setBallRadius(int ballRadius) {
        this.ballRadius = ballRadius;
        invalidate(); // Redraw with the new settings
    }

    public void setBallColor(int ballColor) {
        this.ballColor = ballColor;
        paint.setColor(ballColor);
        invalidate(); // Redraw with the new settings
    }

    public int getBallRadius() {
        return ballRadius;
    }

    public int getBallColor() {
        return ballColor;
    }

    private void loadBallPosition() {
        x = prefs.getFloat("BallX", 500);
        y = prefs.getFloat("BallY", 500);
    }

    private void saveBallPosition() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("BallX", x);
        editor.putFloat("BallY", y);
        editor.apply();
    }
}
