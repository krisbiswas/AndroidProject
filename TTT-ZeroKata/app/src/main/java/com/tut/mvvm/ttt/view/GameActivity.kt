package com.tut.mvvm.ttt.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.tut.mvvm.ttt.R
import com.tut.mvvm.ttt.model.Player
import com.tut.mvvm.ttt.viewmodel.GameViewModel
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tut.mvvm.ttt.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity() {

    private val NO_WINNER = "No one"
    private var gameViewModel: GameViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var p1 = intent.getStringExtra("player1").orEmpty()
        var p2 = intent.getStringExtra("player2").orEmpty()
        p1 = if (p1.isEmpty()) "Player 1" else p1
        p2 = if (p2.isEmpty()) "Player 2" else p2
        println(p1+" & "+p2)
        onPlayersSet(p1,p2)
    }

    private fun onPlayersSet(player1: String, player2: String) {
        initDataBinding(player1, player2)
    }

    private fun initDataBinding(player1: String, player2: String) {
        val activityGameBinding: ActivityGameBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_game)
        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        gameViewModel!!.init(player1, player2)
        activityGameBinding.gameViewModel = gameViewModel
        setUpOnGameEndListener()
    }

    private fun setUpOnGameEndListener() {
        gameViewModel!!.getWinner().observe(this, Observer { winner: Player? ->
            onGameWinnerChanged(winner)
        })
    }

    private fun onGameWinnerChanged(winner: Player?) {
        val winnerName =
            winner?.name ?: NO_WINNER
        Toast.makeText(this, "$winnerName won this game", Toast.LENGTH_LONG).show()
    }
}
