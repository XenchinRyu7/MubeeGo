package com.saefulrdevs.mubeego.ui.main.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.databinding.ItemHorizontalCardBinding

class FavoriteMixedAdapter(
    private val onItemClick: (SearchItem) -> Unit
) : ListAdapter<SearchItem, FavoriteMixedAdapter.MixedViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MixedViewHolder {
        val binding = ItemHorizontalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MixedViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MixedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MixedViewHolder(
        private val binding: ItemHorizontalCardBinding,
        private val onItemClick: (SearchItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem) {
            with(binding) {
                tvItemTitle.text = item.name
                tvItemDate.text = item.releaseOrAirDate
                itemView.setOnClickListener { onItemClick(item) }
                Glide.with(itemView.context)
                    .load(item.posterPath)
                    .apply(RequestOptions.placeholderOf(R.drawable.placholder).error(R.drawable.placholder))
                    .into(imgPoster)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SearchItem>() {
            override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean = oldItem.id == newItem.id && oldItem.mediaType == newItem.mediaType
            override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean = oldItem == newItem
        }
    }
}
