package com.gp.sph.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gp.sph.R
import kotlinx.android.synthetic.main.item_decreasing.view.*
import kotlinx.android.synthetic.main.item_error.view.*
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
        fun retryPage()
    }

    private val data: MutableList<NodeSet> = mutableListOf()

    lateinit var adapterListener: AdapterListener
    var lastItemQuarter: Record? = null

    fun addData(records: MutableList<Pair<String, MutableList<MobileDataUsage.Result.Record>>>, reset: Boolean) {
        if (reset) {
            data.clear()
            notifyDataSetChanged()
        }

        val oldSize = data.size
        if (oldSize > 0) {
            data.removeAt(oldSize - 1)
            checkForPossibleDuplicates(records)
        }
        data.addAll(records)
        val newSize = data.size
        notifyItemRangeChanged(oldSize, newSize)

        setLastQuarterItem()
    }

    private fun setLastQuarterItem() {
        val recs = data.last().second
        lastItemQuarter = recs?.last()!!
    }

    private fun checkForPossibleDuplicates(newData: MutableList<Pair<String, MutableList<MobileDataUsage.Result.Record>>>) {
        val oldLastData = data.last()
        val newFirstData = newData.first()

        if (oldLastData.first == newFirstData.first) {
            val tmp = oldLastData.second
            tmp!!.addAll(newFirstData.second)
            data[data.lastIndex] = Pair(oldLastData.first, tmp)
            newData.removeAt(0)
        }
    }

    fun addRefresh() {
        val lastIndex = data.lastIndex
        data.removeAt(lastIndex)
        data.add(Pair(REFRESH, null))
        notifyItemChanged(lastIndex)
    }

    fun maxEndReach() {
        data.add(Pair(END_REACH, null))
        notifyItemInserted(data.size - 1)
    }

    fun addError() {
        // remove refresh item
        data.removeAt(data.size - 1)
        notifyItemRemoved(data.size - 1)

        data.add(Pair(ERROR, null))
        notifyItemInserted(data.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            REFRESH -> {
                val view = inflater.inflate(R.layout.item_page_next, parent, false)
                DefaultViewHolder(view)
            }
            END_REACH -> {
                val view = inflater.inflate(R.layout.item_max_reached, parent, false)
                DefaultViewHolder(view)
            }

            ERROR -> {
                val view = inflater.inflate(R.layout.item_error, parent, false)
                ErrorViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_mobile_data, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            REFRESH -> adapterListener.onEndReach()
            SUCCESS -> (holder as ItemViewHolder).bind(data[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pair = data[position]
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

                itemView.llDetails.removeAllViews()
                val inflater = LayoutInflater.from(itemView.context)
                for (d: String in details) {
                    val view = inflater.inflate(R.layout.item_decreasing, itemView.llDetails, false)
                    view.tvDecreasingQuarter.text = d
                    itemView.llDetails.addView(view as View)
                }
            } else {
                itemView.ivDataStat.setOnClickListener(null)
                itemView.ivDataStat.setImageResource(R.drawable.vec_data)
            }

            itemView.llDetails.visibility = if (firstItem.showDetails) View.VISIBLE else View.GONE
        }
    }

    inner class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.btnError.setOnClickListener {
                adapterListener.retryPage()
            }
        }
    }
}