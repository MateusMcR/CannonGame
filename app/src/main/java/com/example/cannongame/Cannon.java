package com.example.cannongame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Cannon {
    private int baseRadius;
    private int barrelLength;
    private Point barrelEnd = new Point();
    private double barrelAngle;
    private Cannonball cannonball;
    private Paint paint = new Paint();
    private CannonView view;

    public Cannon(CannonView view, int baseRadius, int barrelLength, int barrelWidth) {
        this.view = view;
        this.baseRadius = baseRadius;
        this.barrelLength = barrelLength;
        paint.setStrokeWidth(barrelWidth);
        paint.setColor(Color.BLACK);
        align(Math.PI / 2);
    }

    public void align(double barrelAngle) {
        this.barrelAngle = barrelAngle;
        barrelEnd.x = (int) (barrelLength * Math.sin(barrelAngle));
        barrelEnd.y = (int) (view.getScreenHeight() / 2 - barrelLength * Math.cos(barrelAngle));
    }

    public void fireCannonball() {
        int x = (int) (barrelLength * Math.sin(barrelAngle));
        int y = (int) (view.getScreenHeight() / 2 - barrelLength * Math.cos(barrelAngle));

        float velocityX = (float) (CannonView.CANNONBALL_SPEED * Math.sin(barrelAngle));
        float velocityY = (float) (-CannonView.CANNONBALL_SPEED * Math.cos(barrelAngle));

        int radius = (int) (barrelLength * CannonView.CANNONBALL_RADIUS_PERCENT);

        cannonball = new Cannonball(view, Color.BLACK, CannonView.CANNON_SOUND_ID,
                x - radius, y - radius, radius, velocityX, velocityY);

        cannonball.playSound();
    }

    public void draw(Canvas canvas) {
        canvas.drawLine(0, view.getScreenHeight() / 2, barrelEnd.x, barrelEnd.y, paint);

        canvas.drawCircle(0, view.getScreenHeight() / 2, baseRadius, paint);
    }

    public Cannonball getCannonball() {
        return cannonball;
    }

    public void removeCannonball() {
        cannonball = null;
    }
}