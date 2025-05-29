package com.saefulrdevs.mubeego.ui.main.seemore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.ItemHorizontalCardBinding

class SeeMoreAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SeeMoreAdapter.SeeMoreViewHolder>() {
    private val items = ArrayList<Any>()
    
    fun submitMovieList(movies: List<Movie>) {
        items.clear()
        items.addAll(movies)
        notifyDataSetChanged()
    }
    
    fun submitTvShowList(tvShows: List<TvShow>) {
        items.clear()
        items.addAll(tvShows)
        notifyDataSetChanged()
    }
    
    fun submitSearchItemList(searchItems: List<SearchItem>) {
        items.clear()
        items.addAll(searchItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeMoreViewHolder {
        val binding =
            ItemHorizontalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeeMoreViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SeeMoreViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    class SeeMoreViewHolder(private val binding: ItemHorizontalCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Any, listener: OnItemClickListener) {
            with(binding) {
                when(item) {
                    is Movie -> {
                        tvItemTitle.text = item.title
                        tvItemDate.text = Utils.changeStringToDateFormat(item.releaseDate)
                        
                        binding.tvItemRating.text = String.format("%.1f/10 IMDb", item.voteAverage)
                        
                        itemView.setOnClickListener {
                            listener.onMovieClicked(item.movieId)
                        }
                        
                        Glide.with(itemView.context)
                            .load(item.posterPath)
                            .apply(
                                RequestOptions.placeholderOf(R.drawable.placholder)
                                    .error(R.drawable.placholder)
                            )
                            .into(imgPoster)
                    }
                    is TvShow -> {
                        tvItemTitle.text = item.name
                        tvItemDate.text = Utils.changeStringToDateFormat(item.firstAirDate)
                        
                        if (binding.tvItemRating != null) {
                            binding.tvItemRating.text = String.format("%.1f/10 IMDb", item.voteAverage)
                        }
                        
                        itemView.setOnClickListener {
                            listener.onTvShowClicked(item.tvShowId)
                        }
                        
                        Glide.with(itemView.context)
                            .load(item.posterPath)
                            .apply(
                                RequestOptions.placeholderOf(R.drawable.placholder)
                                    .error(R.drawable.placholder)
                            )
                            .into(imgPoster)
                    }
                    is SearchItem -> {
                        tvItemTitle.text = item.name
                        tvItemDate.text = Utils.changeStringToDateFormat(item.releaseOrAirDate)
                        
                        if (true) {
                            binding.tvItemRating.text = String.format("%.1f/10 IMDb", item.voteAverage) 
                        }
                        
                        itemView.setOnClickListener {
                            if (item.mediaType == "tv") {
                                listener.onTvShowClicked(item.id)
                            } else if (item.mediaType == "movie") {
                                listener.onMovieClicked(item.id)
                            }
                        }
                        
                        Glide.with(itemView.context)
                            .load(item.posterPath)
                            .apply(
                                RequestOptions.placeholderOf(R.drawable.placholder)
                                    .error(R.drawable.placholder)
                            )
                            .into(imgPoster)
                    }

                    else -> {}
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onMovieClicked(movieId: Int)
        fun onTvShowClicked(tvShowId: Int)
    }
}
