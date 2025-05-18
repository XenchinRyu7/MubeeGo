package com.saefulrdevs.mubeego.ui.main.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saefulrdevs.mubeego.core.domain.model.Playlist
import com.saefulrdevs.mubeego.databinding.ItemPlaylistBinding

class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
    private val onVisibilityToggle: (String, String, Boolean) -> Unit // userId, playlistId, isPublic
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPlaylistClick(getItem(position))
                }
            }

            binding.switchVisibility.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val playlist = getItem(position)
                    onVisibilityToggle(playlist.ownerId, playlist.id, isChecked)
                }
            }
        }

        fun bind(playlist: Playlist) {
            binding.apply {
                tvPlaylistName.text = playlist.name
                tvOwnerName.text = playlist.ownerName
                switchVisibility.isChecked = playlist.isPublic
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Playlist>() {
            override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
                return oldItem == newItem
            }
        }
    }
}
