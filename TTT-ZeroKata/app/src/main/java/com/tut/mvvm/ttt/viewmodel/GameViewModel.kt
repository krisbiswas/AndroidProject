package com.tut.mvvm.ttt.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.ViewModel
import com.tut.mvvm.ttt.model.Player

import androidx.lifecycle.LiveData
import com.tut.mvvm.ttt.R

import com.tut.mvvm.ttt.model.Cell

import com.tut.mvvm.ttt.model.Game

class GameViewModel: ViewModel() {
    var cells: ObservableArrayMap<String, Int>? = null
    var scores = ObservableArrayList<String>()
    private var game: Game? = null
    lateinit var players: Array<String>

    fun init(player1: String?, player2: String?) {
        game = Game(Player(player1!!, R.drawable.x),
            Player(player2!!, R.drawable.o))
        players = arrayOf(player1,player2)
        cells = ObservableArrayMap()
        for(row in 0..2){
            for (col in 0..2){
                cells?.put("$row$col", R.color.black)
            }
        }
        scores.add("0")
        scores.add("0")
    }

    fun onClickedCellAt(row: Int, column: Int) {
        if(game != null){
            if (game!!.board!![row]?.get(column) == null) {
                game!!.board!![row]?.set(column, Cell(game!!.currentPlayer))
                cells?.put("$row$column", game!!.currentPlayer!!.value)
                if (game!!.hasGameEnded()) {
                    scores[0] = game!!.player1!!.score.toString()
                    scores[1] = game!!.player2!!.score.toString()
                    println("Scores: P1=${game!!.player1!!.score}, P2=${game!!.player2!!.score}")
                    reset()
                } else {
                    game!!.switchPlayer()
                }
            }
        }
    }

    fun reset(){
        for(r in 0..2){
            for (c in 0..2){
                cells?.put("$r$c", R.color.black)
            }
        }
        game!!.reset()
    }

    fun getWinner(): LiveData<Player?> {
        return game!!.winner
    }
}