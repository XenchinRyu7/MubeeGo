package com.saefulrdevs.mubeego.favorite.main.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.favorite.databinding.FragmentFavoriteMovieBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteMoviesFragment : Fragment() {

    private var _binding: FragmentFavoriteMovieBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteMoviesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteAdapter = FavoriteMoviesAdapter()
        with(binding) {
            progressCircular.visibility = View.VISIBLE
            viewModel.getMovieFav().observe(viewLifecycleOwner) { movies ->
                progressCircular.visibility = View.GONE
                imgNotFound.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
                favoriteAdapter.submitList(movies)
            }
        }

        with(binding.rvFavoriteMovies) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = favoriteAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}