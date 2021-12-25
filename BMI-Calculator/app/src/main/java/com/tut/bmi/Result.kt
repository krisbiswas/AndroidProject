package com.tut.bmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class Result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val desc = findViewById<TextView>(R.id.description)

        val bmi_val = intent.getDoubleExtra("bmi",-1.0)
        findViewById<TextView>(R.id.bmi_val).text = String.format("%.2f", bmi_val)

        val im = findViewById<ImageView>(R.id.user_fitness)
        if (bmi_val > 24){
            desc.text = "You are overweight!\nShould have control over your dietary intake"
//            im.setImageResource(when(intent.getStringExtra("gender")){
//                "M" -> R.drawable.boy_overweight
//                "F" -> R.drawable.girl_overweight
//                else -> {
//                    0
//                }
//            })
            if(intent.getStringExtra("gender").equals("M")) {
                im.setImageResource(R.drawable.boy_overweight)
            }else{
                im.setImageResource(R.drawable.girl_overweight)
            }
        }else{
            desc.text = "So Fit!\nGreat job maintaining your fitness"
            if(intent.getStringExtra("gender").equals("M")) {
                im.setImageResource(R.drawable.boy_normal)
            }else{
                im.setImageResource(R.drawable.girl_normal)
            }
        }
    }
}