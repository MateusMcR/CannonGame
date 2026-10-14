package com.example.cannongame;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {
    protected CannonView view;
    protected Paint paint = new Paint();
    protected Rect shape;
    private float velocityY;
    private int soundId;

    public GameElement(CannonView view, int color, int soundId, int x, int y, int width, int length, float velocityY) {
        this.view = view;
        paint.setColor(color);
        shape = new Rect(x, y, x + width, y + length);
        this.soundId = soundId;
        this.velocityY = velocityY;
    }

    public void update(double interval) {
        shape.offset(0, (int) (velocityY * interval));

        // Se bater no teto
        if (shape.top < 0 && velocityY < 0) {
            velocityY *= -1;
            shape.offset(0, -shape.top);
        }
        // Se bater no chão
        else if (shape.bottom > view.getScreenHeight() && velocityY > 0) {
            velocityY *= -1;
            shape.offset(0, view.getScreenHeight() - shape.bottom);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(shape, paint);
    }

    public void playSound() {
        view.playSound(soundId);
    }
}