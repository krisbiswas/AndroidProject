package com.tut.localstreamer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomListAdaptor(private val dirs: MutableList<String>, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<CustomListAdaptor.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    class ViewHolder(itemView: View, val onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var dir: TextView = itemView.findViewById(R.id.dir_container)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemClickListener.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dir_view_row,parent,false)
        return ViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dir.text = dirs.elementAtOrNull(position)
    }

    override fun getItemCount(): Int {
        return dirs.size
    }
}