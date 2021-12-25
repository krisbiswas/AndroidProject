package com.tut.fragmentSMS

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.tut.fragmentSMS.databinding.FragmentDetailsRowBinding
import com.tut.fragmentSMS.details_holder.DetailsholderContent

/**
 * [RecyclerView.Adapter] that can display a [MsgholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class DetailsRecyclerViewAdapter(
    private val values: List<DetailsholderContent.DetailholderItem>
) : RecyclerView.Adapter<DetailsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentDetailsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.message.text = item.msg
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentDetailsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val message: TextView = binding.message
    }

}