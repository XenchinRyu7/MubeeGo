package com.saefulrdevs.mubeego.ui.tvshowdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.Season
import com.saefulrdevs.mubeego.core.util.Utils.changeStringDateToYear
import com.saefulrdevs.mubeego.core.util.Utils.changeStringToDateFormat
import com.saefulrdevs.mubeego.databinding.ItemSeasonsBinding

class SeasonsAdapter : ListAdapter<Season, SeasonsAdapter.SeasonViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val itemsSeasonDetailBinding =
            ItemSeasonsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeasonViewHolder(itemsSeasonDetailBinding)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = getItem(position)
        holder.bind(season)
    }

    class SeasonViewHolder(private val binding: ItemSeasonsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(season: Season) {
            with(binding) {
                "Season ${season.seasonNumber}".also { tvItemTitle.text = it }
                val year = changeStringDateToYear(season.airDate)
                tvItemYear.text = if (year == -1) "- | ${season.episodeCount} Eps." else "$year | ${season.episodeCount} Eps."
                "Season ${season.seasonNumber} premiered on ${
                    changeStringToDateFormat(
                        season.airDate
                    )
                }.".also { tvItemPremiere.text = it }
                season.overview.also { tvItemDescription.text = it }

                Glide.with(itemView.context)
                    .load(season.posterPath)
                    .transform(RoundedCorners(16))
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.placholder)
                    )
                    .into(imgPoster)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Season>() {
            override fun areItemsTheSame(oldItem: Season, newItem: Season): Boolean {
                return oldItem.seasonId == newItem.seasonId
            }

            override fun areContentsTheSame(oldItem: Season, newItem: Season): Boolean {
                return oldItem == newItem
            }
        }
    }
}