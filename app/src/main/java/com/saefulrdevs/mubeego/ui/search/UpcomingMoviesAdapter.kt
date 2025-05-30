package com.saefulrdevs.mubeego.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.databinding.ItemVerticalCardBinding
import androidx.navigation.findNavController
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment

class UpcomingMoviesAdapter : ListAdapter<Movie, UpcomingMoviesAdapter.MovieViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemVerticalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MovieViewHolder(private val binding: ItemVerticalCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            with(binding) {
                tvItemTitle.text = movie.title
                tvItemRating.text = movie.releaseDate
                tvItemRating.text = "${movie.voteAverage}/10 IMDb"
                Glide.with(itemView.context)
                    .load(if (movie.posterPath.isNotEmpty()) movie.posterPath else R.drawable.placholder)
                    .apply(RequestOptions.placeholderOf(R.drawable.placholder).error(R.drawable.placholder))
                    .into(imgPoster)
                itemView.setOnClickListener {
                    val navController = itemView.findNavController()
                    val bundle = android.os.Bundle().apply {
                        putInt(MovieDetailFragment.EXTRA_MOVIE, movie.movieId)
                    }
                    navController.navigate(R.id.navigation_detail_movie, bundle)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.movieId == newItem.movieId
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
        }
    }
}
