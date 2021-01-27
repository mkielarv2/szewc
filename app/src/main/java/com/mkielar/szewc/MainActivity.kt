package com.mkielar.szewc

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        findViewById<Button>(R.id.startButton)
                .setOnClickListener {
                    BoxesSettingsDialog().show(supportFragmentManager, "boxes")
                }
    }
}