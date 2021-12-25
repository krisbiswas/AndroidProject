package com.tut.dialer

data class Contact(val name: String, val num: String){
    override fun toString(): String {
        return "$name:-> $num"
    }
}
