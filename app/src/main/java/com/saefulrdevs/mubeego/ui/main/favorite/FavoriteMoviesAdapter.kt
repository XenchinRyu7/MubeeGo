package com.saefulrdevs.mubeego.ui.main.favorite

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.ItemMoviesTvshowsBinding
import com.saefulrdevs.mubeego.ui.moviedetail.MovieDetailActivity

class FavoriteMoviesAdapter: ListAdapter<Movie, FavoriteMoviesAdapter.MoviesViewHolder>(
    DIFF_CALLBACK
)  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val itemsSeasonDetailBinding = ItemMoviesTvshowsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(itemsSeasonDetailBinding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }

    class MoviesViewHolder(private val binding: ItemMoviesTvshowsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            with(binding) {
                tvItemTitle.text = movie.title
                tvItemDate.text = Utils.changeStringToDateFormat(movie.releaseDate)
//                tvItemRating.rating = movie.voteAverage.toFloat() / 2
//                tvItemSynopsis.text = movie.overview
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, MovieDetailActivity::class.java)
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie.movieId)
                    itemView.context.startActivity(intent)
                }
                Glide.with(itemView.context)
                    .load(movie.posterPath)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.placholder)
                            .error(R.drawable.placholder))
                    .into(imgPoster)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.movieId == newItem.movieId
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}
