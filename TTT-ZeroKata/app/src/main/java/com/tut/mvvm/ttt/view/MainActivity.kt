package com.tut.mvvm.ttt.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.tut.mvvm.ttt.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun onClick(v: View){
        val p1name = findViewById<EditText>(R.id.et_p1name).text.toString()
        val p2name = findViewById<EditText>(R.id.et_p2name).text.toString()
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("player1",p1name).putExtra("player2",p2name)
        startActivity(intent)
    }
}