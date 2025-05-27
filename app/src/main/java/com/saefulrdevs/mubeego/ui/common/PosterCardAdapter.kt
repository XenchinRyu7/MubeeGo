package com.saefulrdevs.mubeego.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.databinding.ItemVerticalCardBinding

class PosterCardAdapter : RecyclerView.Adapter<PosterCardAdapter.PosterViewHolder>() {
    private val items = mutableListOf<Movie>()

    fun submitList(list: List<Movie>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val binding =
            ItemVerticalCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PosterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PosterViewHolder(private val binding: ItemVerticalCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            // Load image with Glide/Picasso/Coil if needed
            binding.tvItemTitle.text = movie.title
//            binding.tvItemRating.text = movie.rating
            // Set poster image, etc.
        }
    }
}
