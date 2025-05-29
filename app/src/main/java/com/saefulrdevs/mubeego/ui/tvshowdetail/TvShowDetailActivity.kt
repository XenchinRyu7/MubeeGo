package com.saefulrdevs.mubeego.ui.tvshowdetail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.saefulrdevs.mubeego.databinding.ActivityTvShowDetailBinding
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.SeasonsAdapter
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvShowDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTvShowDetailBinding
    private val tvSeriesDetailViewModel: TvSeriesDetailViewModel by viewModel()
    private lateinit var seasonAdapter: SeasonsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTvShowDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        seasonAdapter = SeasonsAdapter()

        with(binding.contentTvShowDetail.rvSeasons) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(
                    this@TvShowDetailActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = seasonAdapter
        }

        val extras = intent.extras
        if (extras != null) {
            val showId = extras.getInt(EXTRA_TV_SHOW)
            if (showId != 0) {
                getTvShow(showId)
            }
        }

        binding.fabFavorite.setOnClickListener {
            val newState = tvSeriesDetailViewModel.setFavorite()
            if (newState) {
                Toast.makeText(this, R.string.addedToFavorite, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.removedFromFavorite, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTvShow(showId: Int) {
        val tvShow = tvSeriesDetailViewModel.getTvShowDetail(showId)
        tvShow.observe(this) { show ->
            when (show) {
                is Resource.Loading -> binding.contentTvShowDetail.progressCircular.visibility =
                    View.VISIBLE

                is Resource.Success -> {
                    Log.i("result", show.data.toString())
                    binding.contentTvShowDetail.progressCircular.visibility = View.GONE
                    show.data?.let {
                        tvSeriesDetailViewModel.setSelectedTvShow(it)
                        showDetailTvShow(it)
                        getSeasons(it)
                    }
                }

                is Resource.Error -> {
                    binding.contentTvShowDetail.progressCircular.visibility = View.GONE
                    Toast.makeText(
                        this,
                        getString(R.string.error_while_getting_data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getSeasons(tvShow: TvShow) {
        val tvShowWithSeason = tvSeriesDetailViewModel.getTvShowSeasons(tvShow.tvShowId)
        tvShowWithSeason.observe(this) { seasons ->
            when (seasons) {
                is Resource.Loading -> binding.contentTvShowDetail.progressCircular.visibility =
                    View.VISIBLE

                is Resource.Success -> {
                    Log.i("result", seasons.data.toString())
                    binding.contentTvShowDetail.progressCircular.visibility = View.GONE
                    seasons.data?.seasons?.let {
                        seasonAdapter.submitList(it)
                    }
                }

                is Resource.Error -> {
                    binding.contentTvShowDetail.progressCircular.visibility = View.GONE
                    Toast.makeText(
                        this,
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
            toolbarLayout.title = showDetails.name
            tvShowBackdrop.alpha = 0.75F
            contentTvShowDetail.tvShowTitle.text = showDetails.name
            contentTvShowDetail.tvShowSinopsis.text = showDetails.overview
            contentTvShowDetail.tvShowReleaseDate.text =
                changeStringToDateFormat(showDetails.firstAirDate)
            contentTvShowDetail.tvShowRating.rating =
                showDetails.voteAverage.toFloat() / 2
            contentTvShowDetail.tvShowDuration.text =
                showDetails.runtime.let { Utils.changeMinuteToDurationFormat(it) }
            contentTvShowDetail.tvShowGenres.text = showDetails.genres
        }

        Glide.with(this)
            .load(showDetails.posterPath)
            .transform(RoundedCorners(16))
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.placholder)
            )
            .into(binding.contentTvShowDetail.tvShowPoster)

        Glide.with(this)
            .load(showDetails.backdropPath)
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.placholder)
            )
            .into(binding.tvShowBackdrop)

    }

    private fun setFabIcon(isFavorited: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorited) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    companion object {
        const val EXTRA_TV_SHOW = "extra_tv_show"
    }
}
