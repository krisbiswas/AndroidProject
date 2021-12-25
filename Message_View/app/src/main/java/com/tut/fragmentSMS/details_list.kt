package com.tut.fragmentSMS

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tut.fragmentSMS.details_holder.DetailsholderContent
/**
 * A simple [Fragment] subclass.
 * Use the [msg_detail.newInstance] factory method to
 * create an instance of this fragment.
 */
class msg_detail : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = DetailsRecyclerViewAdapter(DetailsholderContent.ITEMS)
            }
        }
        return view
    }

}