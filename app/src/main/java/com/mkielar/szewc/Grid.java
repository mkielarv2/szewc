package com.mkielar.szewc;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.mkielar.szewc.BoxesView.STATE_BLANK;
import static com.mkielar.szewc.BoxesView.STATE_BLUE;
import static com.mkielar.szewc.BoxesView.STATE_GREEN;
import static com.mkielar.szewc.BoxesView.PLAYERS;
import static com.mkielar.szewc.BoxesView.STATE_RED;
import static com.mkielar.szewc.BoxesView.STATE_YELLOW;

/**
 * Top section of BoxesView that contains playground of dots and boxes game
 */
public class Grid {
    private final int gridSize;
    private final BoxesView boxesView;
    private final int margin;
    private final int cellSize;
    private Element[] vertical;
    private Element[] horizontal;
    private Element[] grid;
    private int[] scores;
    private int turn;
    private boolean moved;
    private final Paint paint;
    private boolean gameOver;
    private Element element;
    private Element[] prevGrid;
    private int[] prevScores;
    private boolean wasBoxClosed;
    private boolean reverted;

    public Grid(BoxesView boxesView, int gridSize, int length) {
        this.boxesView = boxesView;
        this.gridSize = gridSize;
        final double percentOfScreenForMargin = 0.075;
        margin = (int) (percentOfScreenForMargin * length);
        cellSize = (length - (margin * 2)) / gridSize;

        vertical = new Element[gridSize * gridSize + gridSize];
        horizontal = new Element[gridSize * gridSize + gridSize];
        grid = new Element[gridSize * gridSize];
        scores = new int[PLAYERS];

        prevGrid = new Element[this.gridSize * this.gridSize];
        prevScores = new int[PLAYERS];

        paint = new Paint();
        paint.setAntiAlias(true);

        restart();
    }

    public void restart() {
        fillArray(vertical);
        fillArray(horizontal);
        fillArray(grid);
        fillArray(prevGrid);
        Arrays.fill(scores, 0);
        Arrays.fill(prevScores, 0);
        gameOver = false;
        turn = STATE_RED;
        wasBoxClosed = false;
        reverted = false;
    }

    public void onDraw(Canvas canvas) {
        //draw background
        canvas.drawColor(0xFF1F1E4F);
        int num = 0;
        for (int i = 0; i < grid.length; i++) {
            if (!grid[i].isBlank()) {
                num++;
            }
        }
        if (num == gridSize * gridSize) {
            gameOver = true;
            boxesView.gameOver(getWinners());
        }
        //fill squares
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                //check square only if it is empty
                if (grid[i * gridSize + j].isBlank()) {
                    //check if square is surrounded from all sides
                    if (!horizontal[i * gridSize + j].isBlank() && !horizontal[(i + 1) * gridSize + j].isBlank() &&
                            !vertical[j * gridSize + i].isBlank() && !vertical[(j + 1) * gridSize + i].isBlank()) {
                        //set state of the square
                        prevGrid = copyArray(grid);
                        grid[i * gridSize + j].setState(turn);
                        //update scores
                        prevScores = Arrays.copyOf(scores, scores.length);
                        scores[turn]++;
                        boxesView.updateScore(scores);
                        //give another move to player who closed the square
                        moved = false;
                        wasBoxClosed = true;
                    }
                }

                //draw coloured squares
                if (!grid[j * gridSize + i].isBlank()) {
                    //get color of square
                    paint.setColor((grid[j * gridSize + i].getColor() & 0x00FFFFFF) | 0x7F000000);
                    //draw single square
                    canvas.drawRect(i * cellSize + margin, j * cellSize + margin,
                            i * cellSize + cellSize + margin, j * cellSize + margin + cellSize, paint);
                    //restore default paint color
                    paint.setColor(Color.WHITE);
                }
            }
        }

        //switch turns
        if (moved) {
            nextTurn();
            moved = false;
        }

        paint.setColor(0xFFFFFFFF);
        for (int i = 0; i <= gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                //draw horizontal lines
                paint.setColor(horizontal[i * gridSize + j].getColor());
                drawHorizontalLine(canvas, i, j);
                paint.setColor(Color.WHITE);

                //draw vertical lines
                paint.setColor(vertical[i * gridSize + j].getColor());
                drawVerticalLine(canvas, i, j);
                paint.setColor(Color.WHITE);
            }
        }

    }

    private Element[] copyArray(Element[] arr) {
        Element[] elements = new Element[arr.length];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new Element(arr[i]);
        }
        return elements;
    }

    public void onClick(MotionEvent event) {
        //determine which segments have been clicked
        //gameOver = true;
        for (int i = 0; i <= gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (contains(new double[]{j * cellSize + margin, j * cellSize + cellSize / 2 + margin, j * cellSize + cellSize + margin, j * cellSize + cellSize / 2 + margin},
                        new double[]{i * cellSize + margin, i * cellSize - cellSize / 2 + margin, i * cellSize + margin, i * cellSize + cellSize / 2 + margin},
                        event.getX(), event.getY())) {
                    //get clicked segment
                    if (horizontal[i * gridSize + j].isBlank()) {
                        element = horizontal[i * gridSize + j];
                        element.setState(turn);
                        moved = true;
                        wasBoxClosed = false;
                        reverted = false;
                    }
                    break;
                }

                if (contains(new double[]{i * cellSize + margin - cellSize / 2, i * cellSize + margin, i * cellSize + margin + cellSize / 2, i * cellSize + margin},
                        new double[]{j * cellSize + margin + cellSize / 2, j * cellSize + margin, j * cellSize + margin + cellSize / 2, j * cellSize + cellSize + margin},
                        event.getX(), event.getY())) {
                    //get clicked segment
                    if (vertical[i * gridSize + j].isBlank()) {
                        element = vertical[i * gridSize + j];
                        element.setState(turn);
                        moved = true;
                        wasBoxClosed = false;
                        reverted = false;
                    }
                    break;
                }
            }
        }
    }

    private void nextTurn() {
        //switch to next player
        turn++;
        if (turn >= PLAYERS) {
            turn = 0;
        }
    }

    private void prevTurn() {
        //switch to previous player
        turn--;
        if (turn < 0) {
            turn = PLAYERS - 1;
        }
    }

    private void fillArray(Element[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new Element();
        }
    }

    //width of one segment
    public static final int LINE_WIDTH = 15;

    private void drawVerticalLine(Canvas canvas, int i, int j) {
        //create segment shape and draw it
        Path path = new Path();
        path.moveTo(i * cellSize + margin, j * cellSize + margin);
        path.lineTo(i * cellSize + margin - LINE_WIDTH, j * cellSize + margin + LINE_WIDTH);
        path.lineTo(i * cellSize + margin - LINE_WIDTH, j * cellSize + cellSize + margin - LINE_WIDTH);
        path.lineTo(i * cellSize + margin, j * cellSize + cellSize + margin);
        path.lineTo(i * cellSize + margin + LINE_WIDTH, j * cellSize + cellSize + margin - LINE_WIDTH);
        path.lineTo(i * cellSize + margin + LINE_WIDTH, j * cellSize + margin + LINE_WIDTH);
        path.lineTo(i * cellSize + margin, j * cellSize + margin);
        canvas.drawPath(path, paint);
    }

    private void drawHorizontalLine(Canvas canvas, int i, int j) {
        //create segment shape and draw it
        Path path = new Path();
        path.moveTo(j * cellSize + margin, i * cellSize + margin);
        path.lineTo(j * cellSize + margin + LINE_WIDTH, i * cellSize + margin - LINE_WIDTH);
        path.lineTo(j * cellSize + cellSize + margin - LINE_WIDTH, i * cellSize + margin - LINE_WIDTH);
        path.lineTo(j * cellSize + cellSize + margin, i * cellSize + margin);
        path.lineTo(j * cellSize + cellSize + margin - LINE_WIDTH, i * cellSize + margin + LINE_WIDTH);
        path.lineTo(j * cellSize + margin + LINE_WIDTH, i * cellSize + margin + LINE_WIDTH);
        path.lineTo(j * cellSize + margin, i * cellSize + margin);
        canvas.drawPath(path, paint);
    }

    //checks if point belongs to given figure
    private static boolean contains(double[] vertx, double[] verty, double testx, double testy) {
        int nvert = vertx.length;
        int i, j;
        boolean c = false;
        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            if (((verty[i] > testy) != (verty[j] > testy)) &&
                    (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                c = !c;
        }
        return c;
    }

    public int getTurn() {
        return turn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void revert() {
        if (!reverted) {
            element.setState(STATE_BLANK);
            if (wasBoxClosed) {
                grid = Arrays.copyOf(prevGrid, prevGrid.length);
            } else {
                prevTurn();
            }
            scores = Arrays.copyOf(prevScores, prevScores.length);
            reverted = true;
        }
    }

    public int[] getWinners() {
        if (!isGameOver()) throw new IllegalStateException();
        Map<Integer, Integer> players = new HashMap<Integer, Integer>() {{
            put(STATE_RED, 0);
            put(STATE_BLUE, 0);
            put(STATE_GREEN, 0);
            put(STATE_YELLOW, 0);
        }};

        for (int i = 0; i < grid.length; i++) {
            int state = grid[i].getState();
            players.put(state, players.get(state) + 1);
        }

        int score = 0;
        for (int i = 0; i < 4; i++) {
            int val = players.get(i);
            if (val > score) {
                score = val;
            }
        }
        List<Integer> temp = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            int val = players.get(i);
            if (val == score) temp.add(i);
        }

        int[] ret = new int[temp.size()];
        int i = 0;
        for (Integer e : temp)
            ret[i++] = e;
        return ret;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArray("vertical", vertical);
        outState.putParcelableArray("horizontal", horizontal);
        outState.putParcelableArray("grid", grid);
        outState.putParcelableArray("prevGrid", prevGrid);
        outState.putIntArray("scores", scores);
        outState.putIntArray("prevScores", prevScores);
        outState.putInt("turn", turn);
        outState.putBoolean("gameOver", gameOver);
        outState.putBoolean("wasBoxClosed", wasBoxClosed);
        outState.putBoolean("reverted", reverted);
    }

    public void loadInstanceState(Bundle state) {
        vertical = (Element[]) state.getParcelableArray("vertical");
        horizontal = (Element[]) state.getParcelableArray("horizontal");
        grid = (Element[]) state.getParcelableArray("grid");
        prevGrid = (Element[]) state.getParcelableArray("prevGrid");
        scores = state.getIntArray("scores");
        prevScores = state.getIntArray("prevScores");
        turn = state.getInt("turn");
        gameOver = state.getBoolean("gameOver");
        wasBoxClosed = state.getBoolean("wasBoxClosed");
        reverted = state.getBoolean("reverted");
    }

    static class Element implements Parcelable {
        private int state;

        public Element() {
            state = STATE_BLANK;
        }

        public Element(Element element) {
            this.state = element.getState();
        }

        protected Element(Parcel in) {
            state = in.readInt();
        }

        public static final Creator<Element> CREATOR = new Creator<Element>() {
            @Override
            public Element createFromParcel(Parcel in) {
                return new Element(in);
            }

            @Override
            public Element[] newArray(int size) {
                return new Element[size];
            }
        };

        public void setState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public int getColor() {
            return BoxesView.getColor(state);
        }

        public boolean isBlank() {
            if (state == STATE_BLANK) return true;
            return false;
        }

        @Override
        public String toString() {
            switch (state) {
                case STATE_BLANK:
                    return "0";
                case STATE_RED:
                    return "R";
                case STATE_BLUE:
                    return "B";
                case STATE_GREEN:
                    return "G";
                case STATE_YELLOW:
                    return "Y";
            }
            return null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(state);
        }
    }

}

