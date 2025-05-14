package com.saefulrdevs.mubeego.ui.seemore

import android.content.Intent
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
import com.saefulrdevs.mubeego.ui.moviedetail.MovieDetailActivity
import com.saefulrdevs.mubeego.ui.tvshowdetail.TvShowDetailActivity

/**
 * Universal adapter untuk SeeMoreFragment yang dapat menangani berbagai jenis model data
 */
class SeeMoreAdapter : RecyclerView.Adapter<SeeMoreAdapter.SeeMoreViewHolder>() {
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
        holder.bind(items[position])
    }

    class SeeMoreViewHolder(private val binding: ItemHorizontalCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: Any) {
            with(binding) {
                when(item) {
                    is Movie -> {
                        tvItemTitle.text = item.title
                        tvItemDate.text = Utils.changeStringToDateFormat(item.releaseDate)
                        
                        // Jika ada TextView untuk rating
                        binding.tvItemRating.text = String.format("%.1f/10 IMDb", item.voteAverage)
                        
                        itemView.setOnClickListener {
                            val intent = Intent(itemView.context, MovieDetailActivity::class.java)
                            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, item.movieId)
                            itemView.context.startActivity(intent)
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
                        
                        // Jika ada TextView untuk rating
                        if (binding.tvItemRating != null) {
                            binding.tvItemRating.text = String.format("%.1f/10 IMDb", item.voteAverage)
                        }
                        
                        itemView.setOnClickListener {
                            val intent = Intent(itemView.context, TvShowDetailActivity::class.java)
                            intent.putExtra(TvShowDetailActivity.EXTRA_TV_SHOW, item.tvShowId)
                            itemView.context.startActivity(intent)
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
                        
                        // Jika ada TextView untuk rating
                        if (binding.tvItemRating != null) {
                            binding.tvItemRating.text = String.format("%.1f/10 IMDb", item.voteAverage) 
                        }
                        
                        itemView.setOnClickListener {
                            if (item.mediaType == "tv") {
                                val intent = Intent(itemView.context, TvShowDetailActivity::class.java)
                                intent.putExtra(TvShowDetailActivity.EXTRA_TV_SHOW, item.id)
                                itemView.context.startActivity(intent)
                            } else if (item.mediaType == "movie") {
                                val intent = Intent(itemView.context, MovieDetailActivity::class.java)
                                intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, item.id)
                                itemView.context.startActivity(intent)
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
}
