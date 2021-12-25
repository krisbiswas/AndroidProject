package com.tut.locationquery.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tut.locationquery.MainActivity
import com.tut.locationquery.adaptors.LocCardAdaptor
import com.tut.locationquery.databinding.FragmentPinListBinding

/**
 * A placeholder fragment containing a simple view.
 */
class ListViewFragment : Fragment() {

    private var _binding: FragmentPinListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPinListBinding.inflate(inflater, container, false)
        val root = binding.root

        val rv = binding.rvLocDetailCards
        rv.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv.adapter = (activity as MainActivity).listDataAdaptor

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}