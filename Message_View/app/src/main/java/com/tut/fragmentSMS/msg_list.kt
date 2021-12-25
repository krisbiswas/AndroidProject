package com.tut.fragmentSMS

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tut.fragmentSMS.msg_holder.MsgholderContent


/**
 * A fragment representing a list of Items.
 */
class msg_list : Fragment() {


    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_msg_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MenuRecyclerViewAdapter(MsgholderContent.ITEMS)
            }
        }
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println(item)
        return super.onOptionsItemSelected(item)
    }

}