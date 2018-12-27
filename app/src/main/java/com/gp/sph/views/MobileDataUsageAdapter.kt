package com.gp.sph.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gp.sph.R
import kotlinx.android.synthetic.main.item_mobile_data.view.*
import testservice.gp.com.api.model.MobileDataUsage

/**
 * Created by gilbert on 12/25/18.
 */

typealias Record = MobileDataUsage.Result.Record

typealias NodeSet = Pair<Any?, MutableList<Record>?>

class MobileDataUsageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SUCCESS = 0
        const val ERROR = 1
        const val REFRESH = 2
        const val END_REACH = 3
    }

    interface AdapterListener {
        fun onEndReach()
    }

    lateinit var adapterListener: AdapterListener
    private var data: MutableList<NodeSet> = mutableListOf()
    var lastItemQuarter: Record? = null

    fun addData(records: MutableList<Pair<String, MutableList<MobileDataUsage.Result.Record>>>, reset: Boolean) {
        if (data == null) {
            data = mutableListOf()
        }

        if (reset) {
            data.clear()
            notifyDataSetChanged()
        }

        if (records != null) {
            val oldSize = data!!.size
            if (oldSize > 0) {
                data!!.removeAt(oldSize - 1)
                checkForPossibleDuplicates(records)
            }
            data!!.addAll(records)
            val newSize = data!!.size
            notifyItemRangeChanged(oldSize, newSize)
        }

        setLastQuarterItem()
    }

    private fun setLastQuarterItem() {
        val recs = data!!.last().second
        lastItemQuarter = recs?.last()!!
    }

    private fun checkForPossibleDuplicates(newData: MutableList<Pair<String, MutableList<MobileDataUsage.Result.Record>>>) {
        val oldLastData = data.last()
        val newFirstData = newData.first()

        if (oldLastData.first == newFirstData.first) {
            var tmp = oldLastData.second
            tmp!!.addAll(newFirstData.second)
            data[data.lastIndex] = Pair(oldLastData.first, tmp)
            newData.removeAt(0)
        }
    }

    fun addRefresh() {
        data!!.add(Pair(REFRESH, null))
        notifyItemInserted(data!!.size - 1)
    }

    fun maxEndReach() {
        data!!.add(Pair(END_REACH, null))
        notifyItemInserted(data!!.size - 1)
    }

    fun addError() {
        // remove refresh item
        data!!.removeAt(data!!.size - 1)
        notifyItemRemoved(data!!.size - 1)

        data!!.add(Pair(ERROR, null))
        notifyItemInserted(data!!.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            REFRESH -> {
                val view = inflater.inflate(R.layout.item_page_next, parent, false)
                RefreshViewHolder(view)
            }
            END_REACH -> {
                val view = inflater.inflate(R.layout.item_max_reached, parent, false)
                RefreshViewHolder(view)
            }

            ERROR -> {
                val view = inflater.inflate(R.layout.item_error, parent, false)
                RefreshViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_mobile_data, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = data?.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            SUCCESS -> (holder as ItemViewHolder).bind(data[position])
            REFRESH -> adapterListener?.onEndReach()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pair = data!![position]
        return when (pair.first) {
            is String -> SUCCESS
            else -> {
                pair.first as Int
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(records: NodeSet) {
            val firstItem = records.second!![0]
            val details = mutableListOf<String>()

            itemView.tvYear.text = records.first as String
            var totalVolume = 0.0
            var hasDecreasing = false
            for (rec: Record in records.second!!) {
                if (rec.isDecreasing) {
                    hasDecreasing = true
                    val detail = "${rec.quarter}"
                    details.add(detail)
                }
                totalVolume += rec.dataVolume
            }
            itemView.tvVolume.text = String.format("%f", totalVolume)

            if (hasDecreasing) {
                itemView.ivDataStat.setOnClickListener {
                    firstItem.showDetails = !firstItem.showDetails
                    notifyItemChanged(data.indexOf(records))
                }
                itemView.ivDataStat.setImageResource(R.drawable.vec_data_down)

                itemView.chipGroup.removeAllViews()
                val inflater = LayoutInflater.from(itemView.context)
                for (d: String in details) {
                    val chip = inflater.inflate(R.layout.item_decreasing, null, false) as TextView
                    chip.text = d
                    itemView.chipGroup.addView(chip as View)
                }
            } else {
                itemView.ivDataStat.setOnClickListener(null)
                itemView.ivDataStat.setImageResource(R.drawable.vec_data)
            }

            itemView.chipGroup.visibility = if (firstItem.showDetails) View.VISIBLE else View.GONE
        }
    }

    inner class RefreshViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}