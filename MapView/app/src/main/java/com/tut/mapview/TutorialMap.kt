package com.tut.mapview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TutorialMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial_map)
        findViewById<Button>(R.id.btn_close_tut).setOnClickListener {
            finish()
        }
    }
}