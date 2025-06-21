package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.PlaylistItem
import com.saefulrdevs.mubeego.core.domain.model.MediaType
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.ui.main.playlist.PlaylistViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AddToPlaylistDialog(
    private val itemId: Int,
    private val itemType: String = "movie",
    private val onAdded: (() -> Unit)? = null
) : DialogFragment() {
    private val playlistViewModel: PlaylistViewModel by activityViewModel()
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_playlist, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlaylistDialog)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = PlaylistDialogAdapter { playlist ->
            val user = userPreferencesUseCase.getUser()
            if (user == null) {
                Toast.makeText(requireContext(), "Please sign in first", Toast.LENGTH_SHORT).show()
                return@PlaylistDialogAdapter
            }
            val item = PlaylistItem(
                id = itemId.toLong(),
                type = itemType,
                addedAt = Timestamp.now()
            )
            playlistViewModel.addItemToPlaylist(user.uid, playlist.id, item)
            Toast.makeText(requireContext(), "Added to playlist", Toast.LENGTH_SHORT).show()
            onAdded?.invoke()
            dismiss()
        }
        recyclerView.adapter = adapter

        val user = userPreferencesUseCase.getUser()
        if (user != null) {
            playlistViewModel.getUserPlaylists(user.uid)
            lifecycleScope.launch {
                playlistViewModel.userPlaylists.collect { result ->
                    val data = result.data
                    if (data != null && true) {
                        @Suppress("UNCHECKED_CAST")
                        adapter.submitList(data)
                    }
                }
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Add to Playlist")
            .setView(view)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}
