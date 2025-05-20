package com.saefulrdevs.mubeego.ui.tvshows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.FragmentTvShowsBinding
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvShowsFragment : Fragment() {

    private val tvSeriesViewModel: TvSeriesViewModel by viewModel()

    private var _binding: FragmentTvShowsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvShowsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvShowAdapter = TvShowsAdapter { showId ->
            val bundle = Bundle().apply {
                putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, showId)
            }
            findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
        }

        tvSeriesViewModel.getDiscoverTvShow().observe(viewLifecycleOwner) { tvShows ->
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

        with(binding.rvTvshows) {
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