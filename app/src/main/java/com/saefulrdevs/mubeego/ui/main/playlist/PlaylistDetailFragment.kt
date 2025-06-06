package com.saefulrdevs.mubeego.ui.main.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment

class PlaylistDetailFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by activityViewModel()

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
        } else {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
