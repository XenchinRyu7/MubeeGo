package com.saefulrdevs.mubeego.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentHomeBinding
import com.saefulrdevs.mubeego.ui.main.movies.MoviesAdapter
import com.saefulrdevs.mubeego.ui.main.movies.MoviesViewModel
import com.saefulrdevs.mubeego.ui.common.PopularAdapter
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.seemore.SeeMoreFragment
import com.saefulrdevs.mubeego.ui.main.tvshows.TvShowsAdapter
import com.saefulrdevs.mubeego.ui.main.tvshows.TvSeriesViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val moviesViewModel: MoviesViewModel by activityViewModel()
    private val tvSeriesViewModel: TvSeriesViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by activityViewModel()
    private lateinit var nowShowingAdapter: MoviesAdapter
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var tvSeriesAdapter: TvShowsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nowShowingAdapter = MoviesAdapter { movieId ->
            val bundle = Bundle().apply {
                putInt(MovieDetailFragment.EXTRA_MOVIE, movieId)
            }
            findNavController().navigate(R.id.navigation_detail_movie, bundle)
        }
        nowShowingAdapter.submitList(emptyList())
        popularAdapter = PopularAdapter { id, type ->
            val bundle = Bundle()
            if (type == "movie") {
                bundle.putInt(MovieDetailFragment.EXTRA_MOVIE, id)
                findNavController().navigate(R.id.navigation_detail_movie, bundle)
            } else if (type == "tv") {
                bundle.putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, id)
                findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
            }
        }
        popularAdapter.submitList(emptyList())
        moviesAdapter = MoviesAdapter { movieId ->
            val bundle = Bundle().apply {
                putInt(MovieDetailFragment.EXTRA_MOVIE, movieId)
            }
            findNavController().navigate(R.id.navigation_detail_movie, bundle)
        }
        moviesAdapter.submitList(emptyList())
        tvSeriesAdapter = TvShowsAdapter { showId ->
            val bundle = Bundle().apply {
                putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, showId)
            }
            findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
        }
        tvSeriesAdapter.submitList(emptyList())

        binding.rvNowShowing.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = nowShowingAdapter
            setHasFixedSize(true)
        }

        binding.rvPopular.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = popularAdapter
            setHasFixedSize(true)
        }

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = moviesAdapter
            setHasFixedSize(true)
        }

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

        homeViewModel.popular.observe(viewLifecycleOwner) { popular ->
            if (popular != null) {
                popularAdapter.submitList(popular.take(5))
            }
        }
        homeViewModel.fetchPopular()

        moviesViewModel.getDiscoverMovies().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { movies ->
                moviesAdapter.submitList(movies.drop(5).take(5))
            }
        }

        tvSeriesViewModel.getDiscoverTvShow().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { tvShows ->
                tvSeriesAdapter.submitList(tvShows.take(5))
            }
        }

        binding.btnSeeMoreNowShowing.setOnClickListener {
            val bundle = Bundle().apply {
                putString(SeeMoreFragment.EXTRA_TYPE, SeeMoreFragment.TYPE_NOW_SHOWING)
            }
            findNavController().navigate(R.id.action_navigation_home_to_seeMoreFragment, bundle)
        }
        binding.btnSeeMorePopular.setOnClickListener {
            val bundle = Bundle().apply {
                putString(SeeMoreFragment.EXTRA_TYPE, SeeMoreFragment.TYPE_POPULAR)
            }
            findNavController().navigate(R.id.action_navigation_home_to_seeMoreFragment, bundle)
        }
        binding.btnSeeMoreMovie.setOnClickListener {
            val bundle = Bundle().apply {
                putString(SeeMoreFragment.EXTRA_TYPE, SeeMoreFragment.TYPE_MOVIE)
            }
            findNavController().navigate(R.id.action_navigation_home_to_seeMoreFragment, bundle)
        }
        binding.btnSeeMoreTv.setOnClickListener {
            val bundle = Bundle().apply {
                putString(SeeMoreFragment.EXTRA_TYPE, SeeMoreFragment.TYPE_TV_SERIES)
            }
            findNavController().navigate(R.id.action_navigation_home_to_seeMoreFragment, bundle)
        }

        if (savedInstanceState != null) {
            val scrollY = savedInstanceState.getInt("scroll_y", 0)
            binding.scrollView.post {
                binding.scrollView.scrollTo(0, scrollY)
            }
            homeViewModel.scrollPositionY = scrollY
        } else {
            binding.scrollView.post {
                binding.scrollView.scrollTo(0, homeViewModel.scrollPositionY)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        homeViewModel.scrollPositionY = _binding?.scrollView?.scrollY ?: 0
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("scroll_y", homeViewModel.scrollPositionY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvNowShowing.adapter = null
        binding.rvPopular.adapter = null
        binding.rvMovies.adapter = null
        binding.rvTvSeries.adapter = null
        _binding = null
    }
}