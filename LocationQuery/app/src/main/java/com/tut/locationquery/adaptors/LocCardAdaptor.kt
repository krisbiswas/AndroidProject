package com.tut.locationquery.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tut.locationquery.R
import com.tut.locationquery.container.CardData

class LocCardAdaptor(var itemList: List<CardData>) : RecyclerView.Adapter<LocCardAdaptor.ViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(data: CardData, pos: Int)
    }
    lateinit var itemClickListener: OnItemClickListener

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_title)
        var address: TextView = itemView.findViewById(R.id.tv_address)
        var distance: TextView = itemView.findViewById(R.id.tv_distance)
        init {
            itemView.setOnClickListener{
                itemClickListener.onItemClick(itemList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_loc_details_card, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = itemList[position].title
        holder.address.text = itemList[position].address
        holder.distance.text = "Distance : ${itemList[position].distance}"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}