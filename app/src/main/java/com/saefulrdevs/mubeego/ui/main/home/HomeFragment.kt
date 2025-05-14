package com.saefulrdevs.mubeego.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.databinding.FragmentHomeBinding
import com.saefulrdevs.mubeego.ui.common.PosterCardAdapter
import com.saefulrdevs.mubeego.ui.movies.MoviesAdapter
import com.saefulrdevs.mubeego.ui.movies.MoviesViewModel
import com.saefulrdevs.mubeego.ui.common.PopularAdapter
import com.saefulrdevs.mubeego.ui.tvshows.TvShowsAdapter
import com.saefulrdevs.mubeego.ui.tvshows.TvShowsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val moviesViewModel: MoviesViewModel by viewModel()
    private val tvShowsViewModel: TvShowsViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var nowShowingAdapter: MoviesAdapter
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var tvSeriesAdapter: TvShowsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nowShowingAdapter = MoviesAdapter()
        popularAdapter = PopularAdapter()
        moviesAdapter = MoviesAdapter()
        tvSeriesAdapter = TvShowsAdapter()

        // Setup Now Showing RecyclerView (Horizontal)
        binding.rvNowShowing.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = nowShowingAdapter
            setHasFixedSize(true)
        }

        // Setup Popular RecyclerView (Vertical)
        binding.rvPopular.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popularAdapter
            setHasFixedSize(true)
        }

        // Setup Movies RecyclerView (Horizontal)
        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = moviesAdapter
            setHasFixedSize(true)
        }

        // Setup TV Series RecyclerView (Horizontal)
        binding.rvTvSeries.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tvSeriesAdapter
            setHasFixedSize(true)
        }

        homeViewModel.getNowPlaying().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { nowPlaying ->
                nowShowingAdapter.submitList(nowPlaying.take(5))
            }
        }

        homeViewModel.getPopular().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { popular ->
                popularAdapter.submitList(popular.take(5))
            }
        }

        // Observe movies and split for all sections
        moviesViewModel.getDiscoverMovies().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { movies ->
                moviesAdapter.submitList(movies.drop(5).take(5))
            }
        }

        tvShowsViewModel.getDiscoverTvShow().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { tvShows ->
                tvSeriesAdapter.submitList(tvShows.take(5))
            }
        }

        // See more click listeners (implement navigation as needed)
        binding.btnSeeMoreNowShowing.setOnClickListener {
            // TODO: Navigate to full now showing list
        }
        binding.btnSeeMorePopular.setOnClickListener {
            // TODO: Navigate to full popular list
        }
        binding.btnSeeMoreTv.setOnClickListener {
            // TODO: Navigate to full TV series list
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}