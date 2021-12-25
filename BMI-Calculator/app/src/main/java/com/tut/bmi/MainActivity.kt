package com.tut.bmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var input_gender = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val height_input = findViewById<EditText>(R.id.input_ht)
        val weight_input = findViewById<EditText>(R.id.input_wt)
        val submit_btn = findViewById<Button>(R.id.submit_btn)

        val gender_radio = findViewById<RadioGroup>(R.id.gender_rg)

        gender_radio.setOnCheckedChangeListener { _, checkedId ->
            input_gender = when (checkedId){
                R.id.male_rb -> "M"
                R.id.female_rb -> "F"
                else -> {
                    "T"
                }
            }
        }

        submit_btn.setOnClickListener {
            val height = height_input.text.toString().toDouble()
            val weight = weight_input.text.toString().toDouble()
            val bmi = (weight*10000/(height*height))
            val showResult = Intent(this, Result::class.java)
            showResult.putExtra("bmi", bmi)
            showResult.putExtra("gender", input_gender)
            startActivity(showResult)
        }
    }
}