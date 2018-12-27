package com.gp.sph.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gp.sph.R

/**
 * Created by gilbert on 12/27/18.
 */
class DecreasingDetailsAdapter(val details: MutableList<String>) : RecyclerView.Adapter<DecreasingDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_decreasing, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = details.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(details[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(details: String) {
            (itemView as TextView).text = details
        }
    }
}