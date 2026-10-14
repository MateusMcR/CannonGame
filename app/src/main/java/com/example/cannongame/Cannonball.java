package com.example.cannongame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;

public class Cannonball extends GameElement {
    private float velocityX;
    private boolean onScreen;

    private ArrayList<PointF> trail = new ArrayList<>();
    private static final int MAX_TRAIL_SIZE = 12;

    private static class PointF {
        float x, y;
        PointF(float x, float y) { this.x = x; this.y = y; }
    }

    public Cannonball(CannonView view, int color, int soundId, int x, int y, int radius, float velocityX, float velocityY) {
        super(view, color, soundId, x, y, 2 * radius, 2 * radius, velocityY);
        this.velocityX = velocityX;
        this.onScreen = true;
    }

    public int getRadius() {
        return (shape.right - shape.left) / 2;
    }

    public boolean collidesWith(GameElement element) {
        return Rect.intersects(shape, element.shape);
    }

    public boolean isOnScreen() {
        return onScreen;
    }

    public void reverseVelocityX() {
        velocityX *= -1;
    }

    @Override
    public void update(double interval) {
        super.update(interval);
        shape.offset((int) (velocityX * interval), 0);

        float centerX = shape.left + getRadius();
        float centerY = shape.top + getRadius();
        trail.add(new PointF(centerX, centerY));

        if (trail.size() > MAX_TRAIL_SIZE) {
            trail.remove(0);
        }

        if (shape.top < 0 || shape.bottom > view.getScreenHeight() ||
                shape.left < 0 || shape.right > view.getScreenWidth()) {
            onScreen = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Paint trailPaint = new Paint();
        trailPaint.setAntiAlias(true);
        for (int i = 0; i < trail.size(); i++) {
            PointF p = trail.get(i);
            float ratio = (float) i / trail.size();
            int alpha = (int) (ratio * 150);
            float currentRadius = getRadius() * (0.3f + 0.7f * ratio);

            trailPaint.setColor(Color.rgb(255, (int) (120 + 135 * ratio), (int) (200 * ratio)));
            trailPaint.setAlpha(alpha);

            canvas.drawCircle(p.x, p.y, currentRadius, trailPaint);
        }

        float centerX = shape.left + getRadius();
        float centerY = shape.top + getRadius();

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(centerX, centerY, getRadius(), paint);

        Paint highlight = new Paint();
        highlight.setColor(Color.WHITE);
        highlight.setAlpha(180);
        canvas.drawCircle(centerX - getRadius()/3f, centerY - getRadius()/3f, getRadius()/4f, highlight);
    }
}