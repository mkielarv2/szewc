package com.mkielar.szewc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Arrays;

import static com.mkielar.szewc.Utils.scaleToRange;
import static com.mkielar.szewc.BoxesView.STATE_BLUE;
import static com.mkielar.szewc.BoxesView.STATE_GREEN;
import static com.mkielar.szewc.BoxesView.PLAYERS;
import static com.mkielar.szewc.BoxesView.STATE_RED;
import static com.mkielar.szewc.BoxesView.STATE_YELLOW;

/**
 * Bottom section of BoxesView that contains information about scores of
 * all players and who's turn is it.
 * Created by MarcinKielar on 15.03.2018.
 */
class Dash {
    private final int width;
    private final int height;
    private final int digitWidth;
    private final int digitHeight;
    private final Grid grid;
    private Paint paint;

    private int activeTarget;

    private Dot[] dots;
    private int[] scores;

    private Bitmap retake;
    private Bitmap restart;
    private Bitmap frameLeft;
    private Bitmap frameRight;

    public Dash(Context context, Grid grid, int width, int height) {
        this.grid = grid;
        this.width = width;
        this.height = height;
        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setTextSize(100);
        Rect rect = new Rect();
        paint.getTextBounds("0", 0, 1, rect);
        digitWidth = rect.width();
        digitHeight = rect.height();

        retake = BitmapFactory.decodeResource(context.getResources(), R.drawable.retake);
        restart = BitmapFactory.decodeResource(context.getResources(), R.drawable.restart);
        frameLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.left_frame);
        frameRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.right_frame);

        dots = new Dot[PLAYERS];
        dots[0] = new Dot(STATE_RED, width / 4, height / 4);
        switch (PLAYERS) {
            case 4:
                dots[3] = new Dot(STATE_YELLOW, width * 3 / 4, height * 3 / 4);
            case 3:
                dots[2] = new Dot(STATE_GREEN, width / 4, height * 3 / 4);
            case 2:
                dots[1] = new Dot(STATE_BLUE, width * 3 / 4, height / 4);
        }
        restart();
    }

    public void restart() {
        scores = new int[PLAYERS];
        Arrays.fill(scores, 0);
        activeTarget = STATE_RED;
        dots[0].setFocused(true);
    }

    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0xFF1F1E4F);
        //draw horizontal divider
        paint.setStrokeWidth(1);
        for (int i = 0; i < 14; i++) {
            paint.setColor(Color.argb((14 - i) * 4, 0, 0, 0));
            canvas.drawLine(0, i, canvas.getWidth(), i, paint);
        }

        if (activeTarget != grid.getTurn()) {
            activeTarget = grid.getTurn();
            for (int i = 0; i < dots.length; i++) {
                if (i == activeTarget) {
                    dots[i].setFocused(true);
                } else {
                    dots[i].setFocused(false);
                }
            }
        }

        paint.setColor(Color.RED);
        for (Dot dot : dots) {
            dot.update(canvas);
        }

        float edge = width / 4;

        paint.setColor(Color.WHITE);
        drawScore(canvas, canvas.getWidth() / 10, canvas.getHeight() / 4, scores[STATE_RED]);
        canvas.drawBitmap(frameLeft, null, new RectF(0, height / 4 - edge / 2, width / 4, height / 4 + edge / 2), paint);
        drawScore(canvas, canvas.getWidth() * 9 / 10, canvas.getHeight() / 4, scores[STATE_BLUE]);
        canvas.drawBitmap(frameRight, null, new RectF(width * 3 / 4, height / 4 - edge / 2, width, height / 4 + edge / 2), paint);
        if (PLAYERS >= 3) {
            drawScore(canvas, canvas.getWidth() / 10, canvas.getHeight() * 3 / 4, scores[STATE_GREEN]);
            canvas.drawBitmap(frameLeft, null, new RectF(0, height * 3 / 4 - edge / 2, width / 4, height * 3 / 4 + edge / 2), paint);
        }
        if (PLAYERS >= 4) {
            drawScore(canvas, canvas.getWidth() * 9 / 10, canvas.getHeight() * 3 / 4, scores[STATE_YELLOW]);
            canvas.drawBitmap(frameRight, null, new RectF(width * 3 / 4, height * 3 / 4 - edge / 2, width, height * 3 / 4 + edge / 2), paint);
        }

        float size = width / 10;
        canvas.drawBitmap(retake, null, new RectF(width / 2 - size / 2, height / 4 - size / 2, width / 2 + size / 2, height / 4 + size / 2), paint);
        canvas.drawBitmap(restart, null, new RectF(width / 2 - size / 2, height * 3 / 4 - size / 2, width / 2 + size / 2, height * 3 / 4 + size / 2), paint);

    }

    public void drawScore(Canvas canvas, int x, int y, int score) {
        String text = Integer.toString(score);
        canvas.drawText(text, x - (digitWidth / 2 * text.length()),
                y + digitHeight / 2, paint);

    }

    public void onClick(float x, float y) {
        if (Math.sqrt(Math.pow(x - width / 2, 2) + Math.pow(y - height / 4, 2)) <= width / 8) {
            grid.revert();
        }
        if (Math.sqrt(Math.pow(x - width / 2, 2) + Math.pow(y - height * 3 / 4, 2)) <= width / 8) {
            grid.restart();
        }


    }

    public void updateScore(int[] scores) {
        this.scores = scores;
    }

    private class Dot {

        private int target;
        private final int mainX;
        private int mainY;

        float progress;
        int duration = 150;
        long baseTime;
        private boolean focused;

        public Dot(int target, int x, int y) {
            this.target = target;
            this.mainX = x;
            this.mainY = y;
            progress = 0;
        }

        public void setFocused(boolean focused) {
            this.focused = focused;
            baseTime = System.currentTimeMillis();
        }

        public void update(Canvas canvas) {
            if (focused) {
                if (progress < 100) {
                    progress = scaleToRange(System.currentTimeMillis() - baseTime, 0, duration, 0, 100);
                    if (progress > 100) {
                        progress = 100;
                    }
                }
            } else {
                if (progress > 0) {
                    progress = 100 - scaleToRange(System.currentTimeMillis() - baseTime, 0, duration, 0, 100);
                    if (progress < 0) {
                        progress = 0;
                    }
                }
            }

            paint.setColor(BoxesView.getColor(target));
            canvas.drawCircle(scaleToRange(progress, 0, 100, mainX, width / 2),
                    scaleToRange(progress, 0, 100, mainY, height / 2),
                    scaleToRange(Math.abs(progress - 50), 0, 50, width / 32, width / 16), paint);
        }


    }
}
