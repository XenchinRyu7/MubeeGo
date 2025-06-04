package com.saefulrdevs.mubeego.ui.main.playlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.saefulrdevs.mubeego.databinding.FragmentPlaylistBinding
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    private var isUpdatingVisibility = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = userPreferencesUseCase.getUser()
        user?.let {

        }
        setupRecyclerView()
        setupFab()
        observeUserPlaylists()
    }

    private fun setupRecyclerView() {
        val user = userPreferencesUseCase.getUser()

        playlistAdapter = PlaylistAdapter(
            onPlaylistClick = { playlist ->
                val bundle = Bundle().apply {
                    putString("playlistId", playlist.id)
                    putString("userId", user?.uid)
                }
                parentFragment?.findNavController()?.navigate(R.id.navigation_playlist_detail, bundle)
            },
            onVisibilityToggle = { userId, playlistId, isPublic ->
                // Optimistic update
                val currentList = playlistAdapter.currentList.toMutableList()
                val idx = currentList.indexOfFirst { it.id == playlistId }
                if (idx != -1) {
                    val playlist = currentList[idx]
                    currentList[idx] = playlist.copy(isPublic = isPublic)
                    playlistAdapter.submitList(currentList.toList())
                }
                isUpdatingVisibility = true
                viewModel.updatePlaylistVisibility(userId, playlistId, isPublic)
            }
        )

        binding.rvPlaylist.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddPlaylist.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                showAddPlaylistDialog(currentUser.uid, currentUser.displayName ?: "Anonymous")
            } else {
                Snackbar.make(binding.root, "Please sign in to create a playlist", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showAddPlaylistDialog(userId: String, userName: String) {
        AddPlaylistDialog(
            userId = userId,
            userName = userName,
            onCreatePlaylist = { playlist ->
                viewModel.createPlaylist(playlist)
            }
        ).show(childFragmentManager, AddPlaylistDialog.TAG)
    }

    private fun observeUserPlaylists() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getUserPlaylists(currentUser.uid)
                viewModel.userPlaylists.collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            showLoading(false)
                            if (result.data.isNullOrEmpty()) {
                                showEmptyState(true)
                            } else {
                                showEmptyState(false)
                                result.data?.forEach {
                                    Log.d("PlaylistFragment", "API: ${it.name} isPublic=${it.isPublic}")
                                }
                                if (isUpdatingVisibility) {
                                    val localList = playlistAdapter.currentList
                                    val backendList = result.data ?: emptyList()
                                    val allMatch = localList.size == backendList.size &&
                                        localList.zip(backendList).all { (local: com.saefulrdevs.mubeego.core.domain.model.Playlist, backend: com.saefulrdevs.mubeego.core.domain.model.Playlist) ->
                                            local.id == backend.id && local.isPublic == backend.isPublic
                                        }
                                    if (allMatch) {
                                        isUpdatingVisibility = false
                                        playlistAdapter.submitList(backendList)
                                    }
                                    // else: tunggu sampai backend sync
                                } else {
                                    playlistAdapter.submitList(result.data)
                                }
                            }
                        }
                        is Resource.Loading -> {
                            showLoading(true)
                            showEmptyState(false)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            showError(result.message ?: "Unknown error occurred")
                        }
                    }
                }
            }
        } else {
            showEmptyState(true)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(show: Boolean) {
        binding.emptyState.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvPlaylist.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaylist.adapter = null
        _binding = null
    }
}
