package com.tut.fragmentSMS.details_holder

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DetailsholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<DetailholderItem> = ArrayList()

    fun addItem(item: DetailholderItem) {
        ITEMS.add(item)
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class DetailholderItem(val msg: String) {
        override fun toString(): String = msg
    }
}