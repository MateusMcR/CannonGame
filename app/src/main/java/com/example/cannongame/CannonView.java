package com.example.cannongame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback {
    private CannonThread cannonThread;
    private Activity activity;
    private boolean dialogIsDisplayed = false;

    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    public static final double CANNONBALL_SPEED = 1500;
    public static final double CANNONBALL_RADIUS_PERCENT = 0.04;

    private int screenWidth;
    private int screenHeight;

    private SoundPool soundPool;
    private SparseIntArray soundMap;

    private Cannon cannon;
    private Blocker blocker;
    private ArrayList<Target> targets;

    private ArrayList<Particle> particles = new ArrayList<>();
    private Random random = new Random();

    private int shotsFired;
    private double totalElapsedTime;
    private boolean gameOver;

    private int currentLevel = 1;
    private Paint hudPaint;
    private Paint shadowPaint;

    private static class Particle {
        float x, y;
        float vx, vy;
        int color;
        int alpha = 255;
        float radius;

        Particle(float x, float y, float vx, float vy, int color, float radius) {
            this.x = x; this.y = y;
            this.vx = vx; this.vy = vy;
            this.color = color;
            this.radius = radius;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.2f;
            alpha -= 8;
        }

        void draw(Canvas canvas, Paint paint) {
            paint.setColor(color);
            paint.setAlpha(Math.max(0, alpha));
            canvas.drawCircle(x, y, radius, paint);
        }
    }

    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (Activity) context;
        getHolder().addCallback(this);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new SparseIntArray(3);
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));

        hudPaint = new Paint();
        hudPaint.setColor(Color.WHITE);
        hudPaint.setTextSize(55);
        hudPaint.setAntiAlias(true);
        hudPaint.setFakeBoldText(true);
        hudPaint.setTextAlign(Paint.Align.CENTER);

        shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setTextSize(55);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setFakeBoldText(true);
        shadowPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        newGame();
    }

    public void newGame() {
        cannon = new Cannon(this, (int) (screenHeight * 0.15), (int) (screenWidth * 0.15), (int) (screenHeight * 0.05));
        targets = new ArrayList<>();
        particles.clear();

        int blockerWidth = (int) (screenWidth * 0.05);
        int targetWidth = (int) (screenWidth * 0.05);

        int targetLength = (int) (screenHeight * 0.15) - (currentLevel * 5);
        if (targetLength < 20) targetLength = 20;

        int blockerVelocity = (int) ((screenHeight * 0.5) + (currentLevel * 100));

        blocker = new Blocker(this, Color.BLACK, 3, (int) (screenWidth * 0.5), 0, blockerWidth, (int) (screenHeight * 0.3), blockerVelocity);

        int targetVelocity = (currentLevel > 1) ? (int) (screenHeight * 0.15 + (currentLevel * 10)) : 0;
        int targetX = (int) (screenWidth * 0.7);
        int startY = (int) (screenHeight * 0.1);

        for (int i = 0; i < 4; i++) {
            int direction = (i % 2 == 0) ? 1 : -1;
            targets.add(new Target(this, Color.CYAN, 2, targetX, startY + i * (targetLength + 10), targetWidth, targetLength, targetVelocity * direction));
            targetX += targetWidth + 20;
        }

        shotsFired = 0;
        totalElapsedTime = 0.0;
        gameOver = false;

        if (cannonThread != null && !cannonThread.isAlive()) {
            cannonThread = new CannonThread(getHolder(), this);
            cannonThread.start();
        }
    }

    public void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0;
        totalElapsedTime += interval;

        blocker.update(interval);
        for (Target target : targets) target.update(interval);

        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update();
            if (p.alpha <= 0) {
                particles.remove(i);
            }
        }

        Cannonball cannonball = cannon.getCannonball();
        if (cannonball != null) {
            cannonball.update(interval);
            testForCollisions(cannonball);
            if (!cannonball.isOnScreen()) {
                cannon.removeCannonball();
            }
        }
    }

    private void createExplosion(float x, float y, int color) {
        int particleCount = 25;
        for (int i = 0; i < particleCount; i++) {
            float angle = random.nextFloat() * (float)Math.PI * 2;
            float speed = 5 + random.nextFloat() * 12;
            float vx = (float)Math.cos(angle) * speed;
            float vy = (float)Math.sin(angle) * speed;
            float radius = 4 + random.nextFloat() * 8;
            particles.add(new Particle(x, y, vx, vy, color, radius));
        }
    }

    private void testForCollisions(Cannonball cannonball) {
        if (cannonball.collidesWith(blocker)) {
            cannonball.reverseVelocityX();
            cannonball.playSound();
            createExplosion(cannonball.shape.centerX(), cannonball.shape.centerY(), Color.YELLOW);
        }

        for (int i = 0; i < targets.size(); i++) {
            Target target = targets.get(i);
            if (cannonball.collidesWith(target)) {
                target.playSound();

                createExplosion(target.shape.centerX(), target.shape.centerY(), Color.CYAN);

                targets.remove(i);
                cannon.removeCannonball();
                break;
            }
        }

        if (targets.isEmpty()) {
            cannonThread.setRunning(false);
            showGameOverDialog(R.string.win);
            gameOver = true;
        }
    }

    public void drawGameElements(Canvas canvas) {
        if (canvas != null) {
            int bgTop = Color.rgb(15, 32, 67);
            int bgBottom = Color.rgb(44, 83, 100);
            if (currentLevel == 2) {
                bgTop = Color.rgb(65, 21, 100);
                bgBottom = Color.rgb(120, 30, 90);
            } else if (currentLevel >= 3) {
                bgTop = Color.rgb(20, 20, 40);
                bgBottom = Color.rgb(10, 10, 20);
            }
            Paint bgPaint = new Paint();
            bgPaint.setShader(new LinearGradient(0, 0, 0, screenHeight, bgTop, bgBottom, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, screenWidth, screenHeight, bgPaint);


            String hudText = "NÍVEL: " + currentLevel + "   |   TIROS: " + shotsFired + "   |   TEMPO: " + String.format("%.1f", totalElapsedTime) + "s";

            float textX = screenWidth / 2f;
            float textY = 110;

            canvas.drawText(hudText, textX + 4, textY + 4, shadowPaint);
            canvas.drawText(hudText, textX, textY, hudPaint);

            Paint blockerPaint = new Paint();
            blockerPaint.setShader(new LinearGradient(blocker.shape.left, 0, blocker.shape.right, 0,
                    Color.YELLOW, Color.rgb(120, 90, 0), Shader.TileMode.CLAMP));
            canvas.drawRect(blocker.shape, blockerPaint);

            for (Target target : targets) {
                Paint targetPaint = new Paint();
                targetPaint.setShader(new LinearGradient(target.shape.left, 0, target.shape.right, 0,
                        Color.CYAN, Color.rgb(0, 130, 200), Shader.TileMode.CLAMP));
                canvas.drawRect(target.shape, targetPaint);
            }

            Paint partPaint = new Paint();
            for (Particle p : particles) {
                p.draw(canvas, partPaint);
            }

            cannon.draw(canvas);

            Cannonball cannonball = cannon.getCannonball();
            if (cannonball != null) {
                cannonball.draw(canvas);
            }
        }
    }

    private void showGameOverDialog(final int messageId) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogIsDisplayed = true;
                currentLevel++;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle(getResources().getString(messageId));
                dialogBuilder.setMessage(getResources().getString(R.string.results) + "\n" +
                        getResources().getString(R.string.shots_fired, shotsFired) + "\n" +
                        getResources().getString(R.string.total_time, totalElapsedTime));
                dialogBuilder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsDisplayed = false;
                        newGame();
                        cannonThread = new CannonThread(getHolder(), CannonView.this);
                        cannonThread.start();
                    }
                });
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();
            }
        });
    }

    public int getScreenWidth() { return screenWidth; }
    public int getScreenHeight() { return screenHeight; }

    public void playSound(int soundId) {
        soundPool.play(soundMap.get(soundId), 1, 1, 1, 0, 1f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            double angle = alignCannon(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN && cannon.getCannonball() == null) {
                cannon.fireCannonball();
                shotsFired++;
            }
        }
        return true;
    }

    private double alignCannon(MotionEvent event) {
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());
        double centerMinusY = (screenHeight / 2) - touchPoint.y;
        double angle = Math.atan2(touchPoint.x, centerMinusY);
        cannon.align(angle);
        return angle;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed) {
            cannonThread = new CannonThread(holder, this);
            cannonThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        cannonThread.setRunning(false);
        while (retry) {
            try {
                cannonThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }
}