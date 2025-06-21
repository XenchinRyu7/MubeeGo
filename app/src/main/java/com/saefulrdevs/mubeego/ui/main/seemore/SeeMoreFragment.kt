package com.saefulrdevs.mubeego.ui.main.seemore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentSeeMoreBinding
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import com.saefulrdevs.mubeego.ui.main.home.HomeViewModel
import com.saefulrdevs.mubeego.ui.main.movies.MoviesViewModel
import com.saefulrdevs.mubeego.ui.main.tvshows.TvSeriesViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SeeMoreFragment : Fragment() {

    private var _binding: FragmentSeeMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var seeMoreAdapter: SeeMoreAdapter
    private val homeViewModel: HomeViewModel by activityViewModel()
    private val moviesViewModel: MoviesViewModel by activityViewModel()
    private val tvSeriesViewModel: TvSeriesViewModel by activityViewModel()

    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val TYPE_NOW_SHOWING = "type_now_showing"
        const val TYPE_POPULAR = "type_popular"
        const val TYPE_MOVIE = "type_movie"
        const val TYPE_TV_SERIES = "type_tv_series"
    }

    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(EXTRA_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeeMoreBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = when (type) {
                TYPE_NOW_SHOWING -> "Now Showing"
                TYPE_POPULAR -> "Popular"
                TYPE_MOVIE -> "Movies"
                TYPE_TV_SERIES -> "TV Series"
                else -> "See More"
            }
            setDisplayHomeAsUpEnabled(true)
        }
        setHasOptionsMenu(true)

        seeMoreAdapter = SeeMoreAdapter(object : SeeMoreAdapter.OnItemClickListener {
            override fun onMovieClicked(movieId: Int) {
                val bundle = Bundle().apply {
                    putInt(MovieDetailFragment.EXTRA_MOVIE, movieId)
                }
                findNavController().navigate(R.id.navigation_detail_movie, bundle)
            }
            override fun onTvShowClicked(tvShowId: Int) {
                val bundle = Bundle().apply {
                    putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, tvShowId)
                }
                findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
            }
        })
        binding.rvSeeMore.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = seeMoreAdapter
            setHasFixedSize(true)
        }

        loadDataBasedOnType()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.menu_search)
        searchItem?.isVisible = false
    }

    private fun loadDataBasedOnType() {
        when (type) {
            TYPE_NOW_SHOWING -> {
                homeViewModel.getNowPlaying().observe(viewLifecycleOwner, Observer { resource ->
                    resource.data?.let { movies ->
                        seeMoreAdapter.submitMovieList(movies)
                        seeMoreAdapter.notifyDataSetChanged()
                    }
                })
            }
            TYPE_POPULAR -> {
                homeViewModel.popular.observe(viewLifecycleOwner, Observer { popular ->
                    if (popular != null) {
                        seeMoreAdapter.submitSearchItemList(popular)
                        seeMoreAdapter.notifyDataSetChanged()
                    }
                })
                homeViewModel.fetchPopular()
            }
            TYPE_MOVIE -> {
                moviesViewModel.getDiscoverMovies()
                    .observe(viewLifecycleOwner, Observer { resource ->
                        resource.data?.let { movies ->
                            seeMoreAdapter.submitMovieList(movies)
                            seeMoreAdapter.notifyDataSetChanged()
                        }
                    })
                moviesViewModel.clearMovies()
            }
            TYPE_TV_SERIES -> {
                tvSeriesViewModel.getDiscoverTvShow()
                    .observe(viewLifecycleOwner, Observer { resource ->
                        resource.data?.let { tvShows ->
                            seeMoreAdapter.submitTvShowList(tvShows)
                            seeMoreAdapter.notifyDataSetChanged()
                        }
                    })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
