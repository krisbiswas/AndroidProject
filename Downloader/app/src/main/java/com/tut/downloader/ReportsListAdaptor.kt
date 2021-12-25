package com.tut.downloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.MalformedURLException
import java.net.URL
import kotlin.collections.ArrayList
import kotlin.math.min

class ReportsListAdapter(private val items: ArrayList<String>, private val listener: DownloadImageViewClicked) : RecyclerView.Adapter<ReportsViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        val viewHolder = ReportsViewHolder(view)

        val downloadImageView : ImageView = view.findViewById(R.id.downloadImageView)
        downloadImageView.setOnClickListener{
            listener.onButtonClicked(items[viewHolder.adapterPosition],getFileNameFromURL(items[viewHolder.adapterPosition]))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        val currentItem = getFileNameFromURL(items[position])
        val lastDotPos = currentItem.lastIndexOf('.')
        holder.reportName.text = currentItem.substring(0, lastDotPos)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    private fun getFileNameFromURL(url: String?): String {
        if (url == null) {
            return ""
        }
        try {
            val resource = URL(url)
            val host: String = resource.host
            if (host.isNotEmpty() && url.endsWith(host)) {
                // handle ...example.com
                return ""
            }
        } catch (e: MalformedURLException) {
            return ""
        }
        val startIndex = url.lastIndexOf('/') + 1
        val length = url.length

        // find end index for ?
        var lastQMPos = url.lastIndexOf('?')
        if (lastQMPos == -1) {
            lastQMPos = length
        }

        // find end index for #
        var lastHashPos = url.lastIndexOf('#')
        if (lastHashPos == -1) {
            lastHashPos = length
        }

        // calculate the end index
        val endIndex = min(lastQMPos, lastHashPos)
        return url.substring(startIndex, endIndex)
    }
}

interface DownloadImageViewClicked {
    fun onButtonClicked(url: String, fileName: String)
}

class ReportsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val reportName : TextView = itemView.findViewById(R.id.reportName)
    val downloadImageView : ImageView = itemView.findViewById(R.id.downloadImageView)

}