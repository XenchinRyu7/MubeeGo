package com.saefulrdevs.mubeego.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.FragmentSearchBinding
import com.saefulrdevs.mubeego.ui.common.PopularAdapter
import com.saefulrdevs.mubeego.ui.main.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var upcomingAdapter: UpcomingMoviesAdapter
    private var isShowingUpcoming = false
    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAutoComplete()
        val popularAdapter = PopularAdapter()
        popularAdapter.submitList(emptyList())
        with(binding.rvTrending) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = popularAdapter
        }
        // Setup Upcoming Movies Adapter
        upcomingAdapter = UpcomingMoviesAdapter()
        // Tambahkan search bar ke toolbar parent secara programatik
        val activity = activity as? AppCompatActivity
        val toolbar = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar?.let {
            // Hapus judul
            activity.supportActionBar?.title = ""
            // Hapus view custom sebelumnya jika ada
            for (i in it.childCount - 1 downTo 0) {
                val v = it.getChildAt(i)
                if (v.tag == "search_bar_parent") it.removeViewAt(i)
            }
            // Tambahkan AutoCompleteTextView dan icon search ke toolbar
            val searchContainer = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                tag = "search_bar_parent"
                val params = androidx.appcompat.widget.Toolbar.LayoutParams(
                    0, androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT, 1
                )
                layoutParams = params
                setPadding(0, 0, 0, 0)
            }
            val autoComplete = android.widget.AutoCompleteTextView(requireContext()).apply {
                id = View.generateViewId()
                hint = getString(R.string.search_hint)
                setBackgroundResource(R.drawable.search_edittext_bg)
                setTextColor(resources.getColor(android.R.color.black, null))
                setHintTextColor(resources.getColor(android.R.color.darker_gray, null))
                textSize = 18f
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                imeOptions = EditorInfo.IME_ACTION_SEARCH
                maxLines = 1
            }
            val btnSearch = android.widget.ImageButton(requireContext()).apply {
                id = View.generateViewId()
                setImageResource(R.drawable.ic_baseline_search_24)
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT
                )
            }
            searchContainer.addView(autoComplete)
            searchContainer.addView(btnSearch)
            it.addView(searchContainer)

            // Logic autocomplete dan search sama seperti SearchActivity
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
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
        // Atur toolbar parent agar mirip toolbar search lama
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            // Hilangkan elevation jika ingin flat
            elevation = 0f
        }
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
        // Hapus search bar custom dari toolbar parent saat fragment hilang
        val activity = activity as? AppCompatActivity
        val toolbar = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar?.let {
            for (i in it.childCount - 1 downTo 0) {
                val v = it.getChildAt(i)
                if (v.tag == "search_bar_parent") it.removeViewAt(i)
            }
        }
        _binding = null
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // Sembunyikan icon search jika ada
        val searchItem = menu.findItem(R.id.menu_search)
        searchItem?.isVisible = false
    }

    private fun setupAutoComplete() {
        // Logic autocomplete dan search sama seperti SearchActivity
    }
}
