package com.saefulrdevs.mubeego.ui.main.playlist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.Timestamp
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.model.Playlist

class AddPlaylistDialog(
    private val userId: String,
    private val userName: String,
    private val onCreatePlaylist: (Playlist) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_add_playlist, null)

        val etPlaylistName = view.findViewById<EditText>(R.id.etPlaylistName)
        val cbPublic = view.findViewById<CheckBox>(R.id.cbPublic)

        return AlertDialog.Builder(requireContext())
            .setTitle("Create New Playlist")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                val name = etPlaylistName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val playlist = Playlist(
                        id = "", // Will be set by Firestore
                        name = name,
                        ownerId = userId,
                        ownerName = userName,
                        isPublic = cbPublic.isChecked,
                        createdAt = Timestamp.now(),
                        updatedAt = Timestamp.now()
                    )
                    onCreatePlaylist(playlist)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    companion object {
        const val TAG = "AddPlaylistDialog"
    }
}
