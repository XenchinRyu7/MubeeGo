package com.saefulrdevs.mubeego.ui.main.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.databinding.FragmentSearchBinding
import com.saefulrdevs.mubeego.ui.common.PopularAdapter
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import com.saefulrdevs.mubeego.ui.main.home.HomeViewModel
import com.saefulrdevs.mubeego.ui.search.UpcomingMoviesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private var startDate: String? = null
    private var endDate: String? = null
    private var isShowingUpcoming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupAutoComplete()
        val popularAdapter = PopularAdapter { id, type ->
            val bundle = Bundle()
            if (type == "movie") {
                bundle.putInt(MovieDetailFragment.EXTRA_MOVIE, id)
                requireParentFragment().findNavController().navigate(R.id.navigation_detail_movie, bundle)
            } else if (type == "tv") {
                bundle.putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, id)
                requireParentFragment().findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
            }
        }
        popularAdapter.submitList(emptyList())
        with(binding.rvTrending) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = popularAdapter
        }
        binding.btnStartDate.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext())
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                val m = (month + 1).toString().padStart(2, '0')
                val d = dayOfMonth.toString().padStart(2, '0')
                startDate = "$year-$m-$d"
                binding.btnStartDate.text = startDate
            }
            datePicker.show()
        }
        binding.btnEndDate.setOnClickListener {
            val datePicker = DatePickerDialog(requireContext())
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                val m = (month + 1).toString().padStart(2, '0')
                val d = dayOfMonth.toString().padStart(2, '0')
                endDate = "$year-$m-$d"
                binding.btnEndDate.text = endDate
            }
            datePicker.show()
        }
        binding.btnSearchUpcoming.setOnClickListener {
            val min = startDate
            val max = endDate
            if (min.isNullOrEmpty() || max.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select first and last date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.rvTrending.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.rvTrending.adapter = popularAdapter
            binding.progressCircular.visibility = View.VISIBLE
            isShowingUpcoming = true
            homeViewModel.getUpcomingMoviesByDate(min, max).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        val movies = result.data ?: emptyList()
                        movies.forEachIndexed { idx, m ->
                            Log.d("SearchFragment", "item[$idx]: $m")
                        }
                        val searchItems = movies.map { movie ->
                            val mediaType = try { movie.javaClass.getDeclaredField("mediaType").let { f -> f.isAccessible = true; f.get(movie) as? String } } catch (_: Exception) { null } ?: "movie"
                            SearchItem(
                                id = movie.movieId,
                                name = movie.title,
                                overview = movie.overview,
                                posterPath = movie.posterPath,
                                releaseOrAirDate = movie.releaseDate,
                                mediaType = mediaType
                            )
                        }
                        popularAdapter.submitList(searchItems)
                        binding.rvTrending.postDelayed({
                            binding.rvTrending.adapter?.itemCount ?: -1
                        }, 500)
                    }
                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to load upcoming movies", Toast.LENGTH_SHORT).show()
                    }
                    else -> if (result is Resource.Loading) binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }
        binding.progressCircular.visibility = View.GONE

        return binding.root
    }

    private fun setupAutoComplete() {
        val autoComplete = binding.autoCompleteSearch
        val btnBack = binding.btnBack
        val btnSearch = binding.btnSearch
        val popularAdapter = PopularAdapter {
            id, type ->
            val bundle = Bundle()
            if (type == "movie") {
                bundle.putInt(MovieDetailFragment.EXTRA_MOVIE, id)
                requireParentFragment().findNavController().navigate(R.id.navigation_detail_movie, bundle)
            } else if (type == "tv") {
                bundle.putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, id)
                requireParentFragment().findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
            }
        }
        btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        btnSearch.setOnClickListener {
            val query = autoComplete.text.toString()
            if (query.isNotEmpty()) {
                binding.progressCircular.visibility = View.VISIBLE
                isShowingUpcoming = false
                (binding.rvTrending.adapter as? UpcomingMoviesAdapter)?.submitList(emptyList())
                binding.rvTrending.adapter = popularAdapter
                homeViewModel.getSearchResult(query).observe(viewLifecycleOwner) { items ->
                    when (items) {
                        is Resource.Success -> {
                            binding.progressCircular.visibility = View.GONE
                            val results = items.data ?: emptyList()
                            popularAdapter.submitList(results)
                        }
                        is Resource.Error -> {
                            binding.progressCircular.visibility = View.GONE
                            popularAdapter.submitList(emptyList())
                            Toast.makeText(requireContext(), getString(R.string.error_while_getting_data), Toast.LENGTH_SHORT).show()
                        }
                        else -> if (items is Resource.Loading) binding.progressCircular.visibility = View.VISIBLE
                    }
                }
            }
        }
        autoComplete.threshold = 1
        autoComplete.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = autoComplete.text.toString()
                if (query.isNotEmpty()) {
                    binding.progressCircular.visibility = View.VISIBLE
                    isShowingUpcoming = false
                    (binding.rvTrending.adapter as? UpcomingMoviesAdapter)?.submitList(emptyList())
                    binding.rvTrending.adapter = popularAdapter
                    homeViewModel.getSearchResult(query).observe(viewLifecycleOwner) { items ->
                        when (items) {
                            is Resource.Success -> {
                                binding.progressCircular.visibility = View.GONE
                                val results = items.data ?: emptyList()
                                popularAdapter.submitList(results)
                            }
                            is Resource.Error -> {
                                binding.progressCircular.visibility = View.GONE
                                popularAdapter.submitList(emptyList())
                                Toast.makeText(requireContext(), getString(R.string.error_while_getting_data), Toast.LENGTH_SHORT).show()
                            }
                            else -> if (items is Resource.Loading) binding.progressCircular.visibility = View.VISIBLE
                        }
                    }
                }
                true
            } else {
                false
            }
        }
        autoComplete.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    isShowingUpcoming = false
                    (binding.rvTrending.adapter as? UpcomingMoviesAdapter)?.submitList(emptyList())
                    binding.rvTrending.adapter = popularAdapter
                    homeViewModel.getSearchResult(s.toString()).observe(viewLifecycleOwner) { items ->
                        when (items) {
                            is Resource.Success -> {
                                val suggestions = items.data?.map { it.name } ?: emptyList()
                                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
                                autoComplete.setAdapter(adapter)
                                autoComplete.showDropDown()
                                popularAdapter.submitList(items.data ?: emptyList())
                            }
                            is Resource.Error -> {
                                popularAdapter.submitList(emptyList())
                            }
                            else -> {}
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}