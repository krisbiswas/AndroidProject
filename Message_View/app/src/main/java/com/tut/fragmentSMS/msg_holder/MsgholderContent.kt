package com.tut.fragmentSMS.msg_holder

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object MsgholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<MsgholderItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, MsgholderItem> = HashMap()

    init {
        // Add some sample items.

    }

    fun addItem(item: MsgholderItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.address, item)
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class MsgholderItem(val address: String, val msg: String) {
        override fun toString(): String = msg
    }
}