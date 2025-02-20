package com.saefulrdevs.mubeego.ui.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.FragmentMoviesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment() {

    private val moviesViewModel: MoviesViewModel by viewModel()

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            val movieAdapter = MoviesAdapter()

            moviesViewModel.getDiscoverMovies().observe(viewLifecycleOwner) { movies ->
                if (movies != null) {
                    when (movies) {
                        is Resource.Loading -> binding.progressCircular.visibility = View.VISIBLE
                        is Resource.Success -> {
                            binding.progressCircular.visibility = View.GONE
                            movieAdapter.submitList(movies.data)
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

            with(binding.rvMovies) {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = movieAdapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}