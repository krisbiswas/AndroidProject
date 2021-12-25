package com.tut.fragmentSMS

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.tut.fragmentSMS.msg_holder.MsgholderContent.MsgholderItem
import com.tut.fragmentSMS.databinding.FragmentMsgRowBinding

/**
 * [RecyclerView.Adapter] that can display a [MsgholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MenuRecyclerViewAdapter(private val values: List<MsgholderItem>)
    : RecyclerView.Adapter<MenuRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentMsgRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.address.text = item.address
        holder.message.text = item.msg
        holder.img.setImageResource(R.drawable.person)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentMsgRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val address: TextView = binding.address
        val message: TextView = binding.message
        val img: ImageView = binding.imgClip
    }

}