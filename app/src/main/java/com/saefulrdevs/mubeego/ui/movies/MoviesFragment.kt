package com.saefulrdevs.mubeego.ui.movies

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.databinding.FragmentMoviesBinding
import com.saefulrdevs.mubeego.ui.MainActivity
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

        val movieAdapter = MoviesAdapter()
        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = movieAdapter
        }
        Log.d("MoviesFragment", "Adapter attached to RecyclerView")

        moviesViewModel.getDiscoverMovies().observe(viewLifecycleOwner) { movies ->
            Log.d("MoviesFragment", "MoviesViewModel LiveData triggered")
            if (movies != null) {
                when (movies) {
                    is Resource.Loading -> {
                        Log.d("MoviesFragment", "Loading movies...")
                        binding.progressCircular.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressCircular.visibility = View.GONE

                        val movieList = movies.data.orEmpty()
                        Log.d("MoviesFragment", "Fetched movies: ${movieList.size} items")

                        movieList.forEach { movie ->
                            Log.d("MoviesFragment", "Movie: ${movie.movieId}, ${movie.title}")
                        }

                        Handler(Looper.getMainLooper()).post {
                            movieAdapter.submitList(movieList) {
                                movieAdapter.notifyDataSetChanged()
                                Log.d("MoviesFragment", "List submitted, RecyclerView updated.")
                                binding.rvMovies.visibility = View.VISIBLE
                            }
                        }
                    }

                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        Log.e("MoviesFragment", "Error fetching movies: ${movies.message}")
                        Toast.makeText(
                            context,
                            getString(R.string.error_while_getting_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
