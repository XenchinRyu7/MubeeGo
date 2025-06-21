package com.saefulrdevs.mubeego.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.ItemHorizontalCardBinding

class PopularAdapter(
    private val onItemClick: (Int, String) -> Unit
) :
    ListAdapter<SearchItem, PopularAdapter.TrendingViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val itemsTrendingDetailBinding =
            ItemHorizontalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrendingViewHolder(itemsTrendingDetailBinding, onItemClick)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val trending = getItem(position)
        holder.bind(trending)
    }

    class TrendingViewHolder(private val binding: ItemHorizontalCardBinding, private val onItemClick: (Int, String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trending: SearchItem) {
            with(binding) {
                tvItemTitle.text = trending.name
                tvItemDate.text = Utils.changeStringToDateFormat(trending.releaseOrAirDate)
                itemView.setOnClickListener {
                    onItemClick(trending.id, trending.mediaType)
                }
                Glide.with(itemView.context)
                    .load(trending.posterPath)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.placholder)
                            .error(R.drawable.placholder))
                    .into(imgPoster)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchItem>() {
            override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}