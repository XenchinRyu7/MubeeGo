package com.saefulrdevs.mubeego.ui.main.playlist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.databinding.FragmentPlaylistDetailBinding
import com.saefulrdevs.mubeego.ui.common.PopularAdapter
import kotlinx.coroutines.flow.collectLatest
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDetailFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var adapter: PopularAdapter
    private var playlistId: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistId = arguments?.getString("playlistId")
        userId = arguments?.getString("userId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PopularAdapter { id, type ->
            if (type == "movie") {
                val bundle = Bundle().apply {
                    putInt(MovieDetailFragment.EXTRA_MOVIE, id)
                }
                findNavController().navigate(R.id.navigation_detail_movie, bundle)
            } else if (type == "tv") {
                val bundle = Bundle().apply {
                    putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, id)
                }
                findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
            }
        }
        binding.rvPlaylistItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlaylistItems.adapter = adapter

        if (playlistId != null && userId != null) {
            viewModel.getPlaylistDetails(userId!!, playlistId!!)
        }

        lifecycleScope.launch {
            viewModel.playlistDetail.collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvError.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.tvError.text = result.message
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                        result.data?.let { playlist ->
                            binding.tvPlaylistName.text = playlist.name
                            binding.tvOwnerName.text = playlist.ownerName
                            binding.tvPlaylistCreatedAt.text = SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(playlist.createdAt.toDate())
                            binding.tvPlaylistRating.text = "Rating: ${playlist.rating}"
                            binding.tvPlaylistDescription.text = playlist.notes
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.playlistSearchItems.collectLatest { result ->
                Log.d("PlaylistDetailFragment", "playlistSearchItems state: $result")
                result.data?.forEach { Log.d("PlaylistDetailFragment", "SearchItem: id=${it.id}, type=${it.mediaType}") }
                when (result) {
                    is Resource.Loading -> {
                        Log.d("PlaylistDetailFragment", "Loading playlist items...")
                    }
                    is Resource.Error -> {
                        Log.e("PlaylistDetailFragment", "Error: ${result.message}")
                    }
                    is Resource.Success -> {
                        Log.d("PlaylistDetailFragment", "Submitting list to adapter: ${result.data}")
                        adapter.submitList(result.data ?: emptyList())
                    }
                }
            }
        }

        binding.btnShare.setOnClickListener {
            val playlistResource = viewModel.playlistDetail.value
            if (playlistResource is Resource.Success && playlistResource.data != null) {
                val playlist = playlistResource.data
                Log.d("PlaylistDetailFragment", "Share clicked, playlist.isPublic=${playlist?.isPublic}, id=${playlist?.id}")
                if (playlist?.isPublic != true) {
                    android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Bagikan Playlist")
                        .setMessage("Playlist harus public untuk bisa dibagikan.")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    val url = "https://mubee-go-playlist.vercel.app/playlist/${playlist.ownerId}/${playlist.id}"
                    val dialogView = layoutInflater.inflate(R.layout.dialog_share_playlist, null)
                    val tvUrl = dialogView.findViewById<TextView>(R.id.tvShareUrl)
                    val btnCopy = dialogView.findViewById<Button>(R.id.btnCopyUrl)
                    val btnShare = dialogView.findViewById<Button>(R.id.btnShareIntent)
                    tvUrl.text = url
                    val dialog = android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Bagikan Playlist")
                        .setView(dialogView)
                        .setNegativeButton("Tutup", null)
                        .create()
                    btnCopy.setOnClickListener {
                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Playlist URL", url)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(requireContext(), "URL disalin", Toast.LENGTH_SHORT).show()
                    }
                    btnShare.setOnClickListener {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }
                        startActivity(Intent.createChooser(intent, "Bagikan ke..."))
                    }
                    dialog.show()
                }
            } else {
                Toast.makeText(requireContext(), "Playlist tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEdit.setOnClickListener {
            val playlistResource = viewModel.playlistDetail.value
            val playlist = (playlistResource as? Resource.Success)?.data
            if (playlist != null) {
                val dialogView = layoutInflater.inflate(R.layout.dialog_edit_playlist, null)
                val etName = dialogView.findViewById<EditText>(R.id.etPlaylistName)
                val etNotes = dialogView.findViewById<EditText>(R.id.etPlaylistNotes)
                etName.setText(playlist.name)
                etNotes.setText(playlist.notes)
                val dialog = android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Edit Playlist")
                    .setView(dialogView)
                    .setPositiveButton("Simpan", null)
                    .setNegativeButton("Batal", null)
                    .create()
                dialog.setOnShowListener {
                    val btnSave = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    btnSave.setOnClickListener {
                        val newName = etName.text.toString().trim()
                        val newNotes = etNotes.text.toString().trim()
                        if (newName.isEmpty()) {
                            etName.error = "Nama playlist tidak boleh kosong"
                            return@setOnClickListener
                        }
                        viewModel.updatePlaylistData(playlist.ownerId, playlist.id, newName, newNotes) { result ->
                            if (result is Resource.Success) {
                                Toast.makeText(requireContext(), "Playlist berhasil diupdate", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            } else if (result is Resource.Error) {
                                Toast.makeText(requireContext(), result.message ?: "Gagal update playlist", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                dialog.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
