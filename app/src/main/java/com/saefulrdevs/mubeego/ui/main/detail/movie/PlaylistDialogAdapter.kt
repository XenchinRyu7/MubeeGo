package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.Playlist

class PlaylistDialogAdapter(
    private val onClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistDialogAdapter.PlaylistViewHolder>() {
    private val playlists = mutableListOf<Playlist>()

    fun submitList(list: List<Playlist>) {
        playlists.clear()
        playlists.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_dialog, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvPlaylistName)
        private val ivAdd: ImageView = itemView.findViewById(R.id.ivAddToPlaylist)
        fun bind(playlist: Playlist) {
            tvName.text = playlist.name
            ivAdd.setOnClickListener { onClick(playlist) }
        }
    }
}
