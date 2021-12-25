package com.tut.mvvm.ttt.model

import android.graphics.drawable.Drawable

class Cell(var player: Player?) {

    fun isEmpty() : Boolean{
        return false
    }

    fun getSign(): Int? {
        return if (!isEmpty()) player!!.value else null
    }

    fun setSign(player: Player){
        this.player = player
    }

    override fun equals(other: Any?): Boolean {
        return (other as Cell).player?.value?.equals(player?.value) == true
    }

    override fun hashCode(): Int {
        return player.hashCode()
    }
}