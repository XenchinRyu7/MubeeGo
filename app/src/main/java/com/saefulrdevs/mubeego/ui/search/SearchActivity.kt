package com.saefulrdevs.mubeego.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.ActivitySearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel: SearchViewModel by viewModel()
    private var startDate: String? = null
    private var endDate: String? = null
    private lateinit var upcomingAdapter: UpcomingMoviesAdapter
    private var isShowingUpcoming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupAutoComplete()
        val trendingsAdapter = TrendingsAdapter()
        trendingsAdapter.submitList(emptyList())
        with(binding.rvTrending) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = trendingsAdapter
        }
        // Setup Upcoming Movies Adapter
        upcomingAdapter = UpcomingMoviesAdapter()
        binding.btnStartDate.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(this)
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                val m = (month + 1).toString().padStart(2, '0')
                val d = dayOfMonth.toString().padStart(2, '0')
                startDate = "$year-$m-$d"
                binding.btnStartDate.text = startDate
            }
            datePicker.show()
        }
        binding.btnEndDate.setOnClickListener {
            val datePicker = android.app.DatePickerDialog(this)
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
                Toast.makeText(this, "Please select first and last date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Clear list before loading
            upcomingAdapter.submitList(emptyList())
            binding.rvTrending.adapter = upcomingAdapter
            binding.progressCircular.visibility = View.VISIBLE
            isShowingUpcoming = true
            searchViewModel.getUpcomingMoviesByDate(min, max).observe(this) { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        val movies = result.data ?: emptyList()
                        upcomingAdapter.submitList(movies)
                    }
                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(this, "Failed to load upcoming movies", Toast.LENGTH_SHORT).show()
                    }
                    else -> if (result is Resource.Loading) binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }
        binding.progressCircular.visibility = View.GONE
    }

    private fun setupAutoComplete() {
        val autoComplete = binding.autoCompleteSearch
        val btnBack = binding.btnBack
        val btnSearch = binding.btnSearch
        val trendingsAdapter = TrendingsAdapter()
        btnBack.setOnClickListener { finish() }
        btnSearch.setOnClickListener {
            val query = autoComplete.text.toString()
            if (query.isNotEmpty()) {
                binding.progressCircular.visibility = View.VISIBLE
                isShowingUpcoming = false
                // Clear and set trending adapter
                (binding.rvTrending.adapter as? UpcomingMoviesAdapter)?.submitList(emptyList())
                binding.rvTrending.adapter = trendingsAdapter
                searchViewModel.getSearchResult(query).observe(this@SearchActivity) { items ->
                    when (items) {
                        is Resource.Success -> {
                            binding.progressCircular.visibility = View.GONE
                            val results = items.data ?: emptyList()
                            trendingsAdapter.submitList(results)
                        }
                        is Resource.Error -> {
                            binding.progressCircular.visibility = View.GONE
                            trendingsAdapter.submitList(emptyList())
                            Toast.makeText(this@SearchActivity, getString(R.string.error_while_getting_data), Toast.LENGTH_SHORT).show()
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
                    binding.rvTrending.adapter = trendingsAdapter
                    searchViewModel.getSearchResult(query).observe(this@SearchActivity) { items ->
                        when (items) {
                            is Resource.Success -> {
                                binding.progressCircular.visibility = View.GONE
                                val results = items.data ?: emptyList()
                                trendingsAdapter.submitList(results)
                            }
                            is Resource.Error -> {
                                binding.progressCircular.visibility = View.GONE
                                trendingsAdapter.submitList(emptyList())
                                Toast.makeText(this@SearchActivity, getString(R.string.error_while_getting_data), Toast.LENGTH_SHORT).show()
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
                    binding.rvTrending.adapter = trendingsAdapter
                    searchViewModel.getSearchResult(s.toString()).observe(this@SearchActivity) { items ->
                        when (items) {
                            is Resource.Success -> {
                                val suggestions = items.data?.map { it.name } ?: emptyList()
                                val adapter = ArrayAdapter(this@SearchActivity, android.R.layout.simple_dropdown_item_1line, suggestions)
                                autoComplete.setAdapter(adapter)
                                autoComplete.showDropDown()
                                trendingsAdapter.submitList(items.data ?: emptyList())
                            }
                            is Resource.Error -> {
                                trendingsAdapter.submitList(emptyList())
                            }
                            else -> {}
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}