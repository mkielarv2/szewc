package com.mkielar.szewc;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;


public class BoxesSettingsDialog extends DialogFragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boxes_settings, container, false);

        NumberPicker gridSizePicker = initGridSizePicker(view);
        NumberPicker playersPicker = initPlayersPicker(view);

        Button button = view.findViewById(R.id.start);
        button.setOnClickListener(v -> {
            int gridSize = gridSizePicker.getValue();
            int players = playersPicker.getValue();

            Intent intent = new Intent(getActivity(), BoxesActivity.class)
                    .putExtra("gridSize", gridSize)
                    .putExtra("players", players);
            startActivity(intent);
            dismiss();
        });

        return view;
    }

    @NonNull
    private NumberPicker initGridSizePicker(View view) {
        NumberPicker gridSizePicker = view.findViewById(R.id.gridSizePicker);
        gridSizePicker.setMinValue(3);
        gridSizePicker.setMaxValue(7);
        gridSizePicker.setValue(3);
        gridSizePicker.setWrapSelectorWheel(false);
        return gridSizePicker;
    }

    @NonNull
    private NumberPicker initPlayersPicker(View view) {
        NumberPicker playersPicker = view.findViewById(R.id.playersPicker);
        playersPicker.setMinValue(2);
        playersPicker.setMaxValue(4);
        playersPicker.setValue(2);
        playersPicker.setWrapSelectorWheel(false);
        return playersPicker;
    }
}
