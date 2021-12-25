/*
Create a login Page for accessing
*/
package com.tut.localstreamer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.HandlerThread
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_connect_device).setOnClickListener {
            startActivity(Intent(this, ConnectionActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        println("Explorer MainActivity Active Threads: ${HandlerThread.activeCount()}")
    }
}