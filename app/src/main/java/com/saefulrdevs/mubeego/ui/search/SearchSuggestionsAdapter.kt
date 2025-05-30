package com.saefulrdevs.mubeego.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.ItemSuggestionsBinding
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import androidx.navigation.findNavController

class SearchSuggestionsAdapter(inflater: LayoutInflater?) :
    SuggestionsAdapter<SearchItem, SearchSuggestionsAdapter.SuggestionHolder>(inflater) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val itemSearchBinding =
            ItemSuggestionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionHolder(itemSearchBinding)
    }

    override fun onBindSuggestionHolder(
        suggestion: SearchItem,
        holder: SuggestionHolder,
        position: Int
    ) {
        holder.bind(suggestion)
    }

    override fun getSingleViewHeight(): Int {
        return 140
    }

    class SuggestionHolder(private val binding: ItemSuggestionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(suggestion: SearchItem) {
            with(binding) {
                tvItemTitle.text = suggestion.name
                tvItemDate.text = Utils.changeStringToDateFormat(suggestion.releaseOrAirDate)
                tvItemSynopsis.text = suggestion.overview
                Glide.with(itemView.context)
                    .load(suggestion.posterPath)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.placholder)
                            .error(R.drawable.placholder)
                    )
                    .into(imgPoster)

                itemView.setOnClickListener {
                    val navController = itemView.findNavController()
                    if (suggestion.mediaType == "tv") {
                        val bundle = Bundle().apply {
                            putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, suggestion.id)
                        }
                        navController.navigate(R.id.navigation_detail_tv_series, bundle)
                    } else if (suggestion.mediaType == "movie") {
                        val bundle = Bundle().apply {
                            putInt(MovieDetailFragment.EXTRA_MOVIE, suggestion.id)
                        }
                        navController.navigate(R.id.navigation_detail_movie, bundle)
                    }
                }
            }
        }
    }
}