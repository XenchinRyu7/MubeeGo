package com.saefulrdevs.mubeego.ui.seemore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentSeeMoreBinding
import com.saefulrdevs.mubeego.ui.main.home.HomeViewModel
import com.saefulrdevs.mubeego.ui.movies.MoviesViewModel
import com.saefulrdevs.mubeego.ui.tvshows.TvSeriesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeeMoreFragment : Fragment() {

    private var _binding: FragmentSeeMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var seeMoreAdapter: SeeMoreAdapter
    private val homeViewModel: HomeViewModel by viewModel()
    private val moviesViewModel: MoviesViewModel by viewModel()
    private val tvSeriesViewModel: TvSeriesViewModel by viewModel()

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

        // Setup adapter
        seeMoreAdapter = SeeMoreAdapter()
        binding.rvSeeMore.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = seeMoreAdapter
            setHasFixedSize(true)
        }

        // Load data based on type
        loadDataBasedOnType()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // Sembunyikan icon search jika ada
        val searchItem = menu.findItem(R.id.menu_search)
        searchItem?.isVisible = false
    }

    private fun loadDataBasedOnType() {
        when (type) {
            TYPE_NOW_SHOWING -> {
                homeViewModel.getNowPlaying().observe(viewLifecycleOwner, Observer { resource ->
                    resource.data?.let { movies ->
                        seeMoreAdapter.submitMovieList(movies)
                    }
                })
            }

            TYPE_POPULAR -> {
                homeViewModel.getPopular().observe(viewLifecycleOwner, Observer { resource ->
                    resource.data?.let { popular ->
                        seeMoreAdapter.submitSearchItemList(popular)
                    }
                })
            }

            TYPE_MOVIE -> {
                moviesViewModel.getDiscoverMovies()
                    .observe(viewLifecycleOwner, Observer { resource ->
                        resource.data?.let { movies ->
                            seeMoreAdapter.submitMovieList(movies)
                        }
                    })
            }

            TYPE_TV_SERIES -> {
                tvSeriesViewModel.getDiscoverTvShow()
                    .observe(viewLifecycleOwner, Observer { resource ->
                        resource.data?.let { tvShows ->
                            seeMoreAdapter.submitTvShowList(tvShows)
                        }
                    })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
