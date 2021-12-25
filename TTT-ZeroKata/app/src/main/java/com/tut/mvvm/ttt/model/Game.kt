package com.tut.mvvm.ttt.model

import androidx.lifecycle.MutableLiveData
import java.lang.NullPointerException


class Game(var player1: Player?, var player2: Player?) {
    private val BOARD_SIZE = 3

//    var player1: Player?
//    var player2: Player?
    var currentPlayer: Player?
    var board: Array<Array<Cell?>?>?

    var winner = MutableLiveData<Player>()
    var totalGames = 0

    init {
        board = Array(BOARD_SIZE) {
            arrayOfNulls(BOARD_SIZE)
        }
//        this.player1 = Player(player1!!, 'X')
//        this.player2 = Player(player2!!, 'O')
        currentPlayer = this.player1
    }

    fun switchPlayer(){
        currentPlayer = when(currentPlayer){
            this.player1 -> this.player2
            else -> player1
        }
    }
///////////////////
    fun reset() {
        // reinit might be required for players and board
//        player1 = null  //Swap Player1 and Player2
//        player2 = null
//        currentPlayer = null
        board = Array(BOARD_SIZE) {
            arrayOfNulls(BOARD_SIZE)
        }
    }

    fun hasGameEnded(): Boolean {
        if (hasThreeSameHorizontalCells() || hasThreeSameVerticalCells() || hasThreeSameDiagonalCells()) {
            winner.value = currentPlayer
            currentPlayer!!.score++
            totalGames++
            return true
        }
        if (isBoardFull()) {
            winner.value = null
            totalGames++
            return true
        }
        return false
    }

    fun hasThreeSameHorizontalCells(): Boolean {
        return try {
            for (i in 0 until BOARD_SIZE) if (areEqual(
                    board!![i]?.get(0),
                    board!![i]?.get(1),
                    board!![i]?.get(2)
                )
            ) return true
            false
        } catch (e: NullPointerException) {
            false
        }
    }

    fun hasThreeSameVerticalCells(): Boolean {
        return try {
            for (i in 0 until BOARD_SIZE) if (areEqual(
                    board!![0]?.get(i),
                    board!![1]?.get(i),
                    board!![2]?.get(i)
                )
            ) return true
            false
        } catch (e: NullPointerException) {
            false
        }
    }

    fun hasThreeSameDiagonalCells(): Boolean {
        return try {
            areEqual(board!![0]?.get(0), board!![1]?.get(1), board!![2]?.get(2)) ||
                    areEqual(board!![0]?.get(2), board!![1]?.get(1), board!![2]?.get(0))
        } catch (e: NullPointerException) {
            false
        }
    }

    fun isBoardFull(): Boolean {
        for (row in board!!) {
            if (row != null) {
                for (cell in row) {
                    if (cell == null || cell.isEmpty()) return false
                }
            }else{
                return false
            }
        }
        return true
    }

    private fun areEqual(vararg cells: Cell?): Boolean {
        if (cells.isEmpty()) return false
        for (cell in cells) {
            if (cell != null) {
                if (cell.getSign() == null) {
                    return false
                }
            }else{
                return false
            }
        }
        val comparisonBase = cells[0]
        for (cell in cells) {
            if (comparisonBase != null && cell != null) {
                if (comparisonBase.getSign() != cell.getSign()) {
                    return false
                }
            }else{
                return false
            }
        }
        return true
    }

}