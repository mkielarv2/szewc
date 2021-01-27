package com.mkielar.szewc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class BoxesView extends View {
    public static int PLAYERS;

    public static final int STATE_BLANK = -1;
    public static final int STATE_RED = 0;
    public static final int STATE_BLUE = 1;
    public static final int STATE_GREEN = 2;
    public static final int STATE_YELLOW = 3;
    private final int gridSize;

    private int width;
    private int height;

    Bitmap gridBmp;
    Canvas gridCnv;

    Bitmap dashBmp;
    Canvas dashCnv;

    private Grid grid;
    private Paint paint;
    private Dash dash;
    private boolean running;

    public BoxesView(Context context, int players, int gridSize) {
        super(context);
        this.gridSize = gridSize;
        paint = new Paint();
        PLAYERS = players;
        running = true;
    }

    int measureStep = 0;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        //it's meant to run on second invocation of onMeasure method
        if (measureStep == 1) {
            //noinspection SuspiciousNameCombination
            gridBmp = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_4444);
            gridCnv = new Canvas(gridBmp);
            grid = new Grid(this, gridSize, width);

            dashBmp = Bitmap.createBitmap(width, height - width, Bitmap.Config.ARGB_4444);
            dashCnv = new Canvas(dashBmp);
            dash = new Dash(getContext(), grid, width, height - width);
        }
        measureStep++;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        grid.onDraw(gridCnv);

        canvas.drawBitmap(gridBmp, 0, 0, paint);

        dash.onDraw(dashCnv);
        canvas.drawBitmap(dashBmp, 0, width, paint);


        //force instant redraw
        if (running)
            invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //delegate motion event to grid
        if (event.getY() < width) {
            grid.onClick(event);
        } else {
            dash.onClick(event.getX(), event.getY() - width);
        }
        return super.onTouchEvent(event);
    }

    public void restart() {
        grid.restart();
    }

    protected void updateScore(int[] scores) {
        dash.updateScore(scores);
    }

    protected static int getColor(int state) {
        switch (state) {
            case STATE_RED:
                return 0xFFCC6D39;
            case STATE_BLUE:
                return 0xFFFFD0A1;
            case STATE_GREEN:
                return 0xFF61B1FF;
            case STATE_YELLOW:
                return 0xFF41AE79;
            default:
                return Color.argb(64, 255, 255, 255);
        }
    }

    public void gameOver(int[] winners) {
        running = false;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.boxes_dialog_winner);
            GradientDrawable bgShape = new GradientDrawable();
            int[] colors;
            if (winners.length < 2) {
                colors = new int[2];
                int color = getColor(winners[0]);
                colors[0] = color;
                colors[1] = color;
            } else {
                colors = new int[winners.length];
                for (int i = 0; i < winners.length; i++) {
                    colors[i] = getColor(winners[i]);
                }
            }
            bgShape.setColors(colors);
            builder.setIcon(bgShape);
            builder.setPositiveButton(R.string.boxes_dialog_restart, (dialog, which) -> {
                running = true;
                invalidate();
                grid.restart();
            });
            builder.setNegativeButton(R.string.boxes_dialog_close, (dialog, which) -> {
                ((Activity) getContext()).finish();
            });
            builder.setCancelable(false);
            builder.show();
        }, 500);
    }


    public void onPause() {
        running = false;
    }

    public void onResume() {
        running = true;
        invalidate();
    }

    public void saveInstanceState(Bundle outState) {
        grid.saveInstanceState(outState);
    }

    public void loadInstanceState(Bundle inState) {
        grid.loadInstanceState(inState);
    }
}
