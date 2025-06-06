package com.saefulrdevs.mubeego.ui.main.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.FragmentFavoriteBinding
import androidx.navigation.fragment.findNavController
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailFragment
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mixedAdapter = FavoriteMixedAdapter { item ->
            when (item.mediaType) {
                "movie" -> {
                    val bundle = Bundle().apply {
                        putInt(MovieDetailFragment.EXTRA_MOVIE, item.id)
                    }
                    findNavController().navigate(R.id.navigation_detail_movie, bundle)
                }
                "tv" -> {
                    val bundle = Bundle().apply {
                        putInt(TvSeriesDetailFragment.EXTRA_TV_SHOW, item.id)
                    }
                    findNavController().navigate(R.id.navigation_detail_tv_series, bundle)
                }
            }
        }
        with(binding.rvFavorite) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mixedAdapter
        }
        viewModel.favoriteList.observe(viewLifecycleOwner) { items ->
            mixedAdapter.submitList(null) // force clear to ensure diff util triggers
            mixedAdapter.submitList(items ?: emptyList())
            binding.progressCircular.visibility = if (items == null) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}