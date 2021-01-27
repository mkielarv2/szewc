package com.mkielar.szewc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;


/**
 * In intent to this activity should be passed "players" -> number, "gridSize" -> number
 */
public class BoxesActivity extends AppCompatActivity {
    private BoxesView boxesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boxes);
        setupBoxesView();
    }

    private void setupBoxesView() {
        Bundle bundle = getIntent().getExtras();
        int players = bundle.getInt("players");
        int gridSize = bundle.getInt("gridSize");

        boxesView = new BoxesView(this, players, gridSize);
        FrameLayout frame = findViewById(R.id.frame);
        frame.addView(boxesView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        boxesView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boxesView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        boxesView.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        boxesView.loadInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }
}
