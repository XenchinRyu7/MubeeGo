package com.saefulrdevs.mubeego.ui.main.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentFavoriteBinding
import com.saefulrdevs.mubeego.ui.main.favorite.FavoriteMoviesAdapter
import com.saefulrdevs.mubeego.ui.main.favorite.FavoriteMoviesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteMoviesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteAdapter = FavoriteMoviesAdapter()
        viewModel.getMovieFav().observe(viewLifecycleOwner) { movies ->
            binding.progressCircular.visibility = View.GONE
            favoriteAdapter.submitList(movies)
        }
        with(binding.rvFavorite) {
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