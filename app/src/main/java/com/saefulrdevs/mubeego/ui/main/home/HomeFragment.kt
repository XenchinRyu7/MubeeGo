package com.saefulrdevs.mubeego.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentHomeBinding
import com.saefulrdevs.mubeego.ui.common.PosterCardAdapter
import com.saefulrdevs.mubeego.ui.movies.MoviesAdapter
import com.saefulrdevs.mubeego.ui.movies.MoviesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val moviesViewModel: MoviesViewModel by viewModel()
    private lateinit var nowShowingAdapter: PosterCardAdapter
    private lateinit var tvSeriesAdapter: PosterCardAdapter
    private lateinit var popularAdapter: MoviesAdapter

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

        nowShowingAdapter = PosterCardAdapter()
        tvSeriesAdapter = PosterCardAdapter()
        popularAdapter = MoviesAdapter()

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

        // Setup TV Series RecyclerView (Horizontal)
        binding.rvTvSeries.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = tvSeriesAdapter
            setHasFixedSize(true)
        }

        // Observe movies and split for all sections
        moviesViewModel.getDiscoverMovies().observe(viewLifecycleOwner) { resource ->
            resource.data?.let { movies ->
                nowShowingAdapter.submitList(movies.take(5))
                popularAdapter.submitList(movies.drop(5).take(5))
                tvSeriesAdapter.submitList(movies.takeLast(5)) // Replace with real TV series data if available
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