package com.saefulrdevs.mubeego.ui.main.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentPlaylistBinding
import com.saefulrdevs.mubeego.ui.tvshows.TvShowsAdapter
import com.saefulrdevs.mubeego.ui.tvshows.TvShowsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.saefulrdevs.mubeego.core.data.Resource
import android.widget.Toast

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val tvShowsViewModel: TvShowsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvShowAdapter = TvShowsAdapter()
        tvShowsViewModel.getDiscoverTvShow().observe(viewLifecycleOwner) { tvShows ->
            if (tvShows != null) {
                when (tvShows) {
                    is Resource.Loading -> binding.progressCircular.visibility = View.VISIBLE
                    is Resource.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        tvShowAdapter.submitList(tvShows.data)
                    }
                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(
                            context,
                            getString(R.string.error_while_getting_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        with(binding.rvPlaylist) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = tvShowAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}