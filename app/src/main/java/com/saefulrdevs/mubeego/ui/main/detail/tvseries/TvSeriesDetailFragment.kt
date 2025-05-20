package com.saefulrdevs.mubeego.ui.main.detail.tvseries

import android.R.attr.visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.core.util.Utils.changeStringToDateFormat
import com.saefulrdevs.mubeego.databinding.FragmentTvSeriesDetailBinding
import com.saefulrdevs.mubeego.ui.tvshowdetail.SeasonsAdapter
import com.saefulrdevs.mubeego.ui.tvshowdetail.TvSeriesDetailViewModel
import com.saefulrdevs.mubeego.ui.tvshowdetail.TvShowDetailActivity
import com.saefulrdevs.mubeego.ui.tvshows.TvSeriesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvSeriesDetailFragment : Fragment() {

    private var _binding : FragmentTvSeriesDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var seasonAdapter: SeasonsAdapter

    private val tvSeriesDetailViewModel: TvSeriesDetailViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTvSeriesDetailBinding.inflate(inflater, container, false)

        seasonAdapter = SeasonsAdapter()

        with(binding.rvSeasons) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = seasonAdapter
        }

        val showId = arguments?.getInt(EXTRA_TV_SHOW) ?: 0
        if (showId != 0) {
            getTvShow(showId)
        }

        binding.fabFavorite.setOnClickListener {
            val newState = tvSeriesDetailViewModel.setFavorite()
            if (newState) {
                Toast.makeText(requireContext(), R.string.addedToFavorite, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), R.string.removedFromFavorite, Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun getTvShow(showId: Int) {
        val tvShow = tvSeriesDetailViewModel.getTvShowDetail(showId)
        tvShow.observe(viewLifecycleOwner) { show ->
            when (show) {
                is Resource.Loading -> binding.progressBar.visibility =
                    View.VISIBLE

                is Resource.Success -> {
                    Log.i("result", show.data.toString())
                    binding.progressBar.visibility = View.GONE
                    show.data?.let {
                        tvSeriesDetailViewModel.setSelectedTvShow(it)
                        showDetailTvShow(it)
                        getSeasons(it)
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_while_getting_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getSeasons(tvShow: TvShow) {
        val tvShowWithSeason = tvSeriesDetailViewModel.getTvShowSeasons(tvShow.tvShowId)
        tvShowWithSeason.observe(viewLifecycleOwner) { seasons ->
            when (seasons) {
                is Resource.Loading -> binding.progressBar.visibility =
                    View.VISIBLE

                is Resource.Success -> {
                    Log.i("result", seasons.data.toString())
                    binding.progressBar.visibility = View.GONE
                    seasons.data?.seasons?.let {
                        seasonAdapter.submitList(it)
                    }
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_while_getting_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showDetailTvShow(showDetails: TvShow) {
        with(binding) {
            setFabIcon(showDetails.favorited)
            tvShowPoster.alpha = 0.75F
            tvShowTitle.text = showDetails.name
            tvShowSinopsis.text = showDetails.overview
            tvFirstAirDate.text =
                changeStringToDateFormat(showDetails.firstAirDate)
           tvShowRating.text =
               showDetails.voteAverage.toString()
            tvLength.text =
                showDetails.runtime.let { Utils.changeMinuteToDurationFormat(it) }
        }

        // Glide.with(this)
        //     .load(showDetails.posterPath)
        //     .transform(RoundedCorners(16))
        //     .apply(
        //         RequestOptions.placeholderOf(R.drawable.ic_loading)
        //             .error(R.drawable.placholder)
        //     )
        //     .into(binding.tvShowPoster)

       Glide.with(this)
           .load(showDetails.backdropPath)
           .apply(
               RequestOptions.placeholderOf(R.drawable.ic_loading)
                   .error(R.drawable.placholder)
           )
           .into(binding.tvShowPoster)

    }

    private fun setFabIcon(isFavorited: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorited) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_TV_SHOW = "extra_tv_show"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}