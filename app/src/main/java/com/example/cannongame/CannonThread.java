package com.example.cannongame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class CannonThread extends Thread {
    private final SurfaceHolder surfaceHolder;
    private boolean threadIsRunning = true;
    private final CannonView cannonView;

    public CannonThread(SurfaceHolder holder, CannonView cannonView) {
        this.surfaceHolder = holder;
        this.cannonView = cannonView;
    }

    public void setRunning(boolean running) {
        threadIsRunning = running;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        long previousFrameTime = System.currentTimeMillis();

        while (threadIsRunning) {
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    long currentTime = System.currentTimeMillis();
                    double elapsedTimeMS = currentTime - previousFrameTime;

                    if (elapsedTimeMS > 30) {
                        elapsedTimeMS = 30;
                    }

                    cannonView.updatePositions(elapsedTimeMS);
                    cannonView.drawGameElements(canvas);

                    previousFrameTime = currentTime;
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}