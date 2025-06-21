package com.saefulrdevs.mubeego.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.HonorableItem

class HonorableAdapter : ListAdapter<HonorableItem, HonorableAdapter.HonorableViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HonorableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_honorable_card, parent, false)
        return HonorableViewHolder(view)
    }

    override fun onBindViewHolder(holder: HonorableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HonorableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvReleaseDate: TextView = itemView.findViewById(R.id.tvReleaseDate)
        private val tvOverview: TextView = itemView.findViewById(R.id.tvOverview)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)

        fun bind(item: HonorableItem) {
            tvTitle.text = item.title
            tvReleaseDate.text = item.releaseDate
            tvOverview.text = item.overview
            tvType.text = item.type
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HonorableItem>() {
            override fun areItemsTheSame(oldItem: HonorableItem, newItem: HonorableItem): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: HonorableItem, newItem: HonorableItem): Boolean = oldItem == newItem
        }
    }
}
