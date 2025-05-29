package com.saefulrdevs.mubeego.ui.main.detail.tvseries

import android.os.Bundle
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
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import com.saefulrdevs.mubeego.core.domain.model.Season
import com.saefulrdevs.mubeego.core.util.Utils
import com.saefulrdevs.mubeego.databinding.FragmentTvSeriesDetailBinding
import com.saefulrdevs.mubeego.ui.main.detail.movie.CastAdapter
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class TvSeriesDetailFragment : Fragment() {

    private var _binding : FragmentTvSeriesDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var seasonAdapter: SeasonsAdapter
    private var castAdapter: CastAdapter? = null

    private val tvSeriesDetailViewModel: TvSeriesDetailViewModel by activityViewModel()


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
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = seasonAdapter
        }
        castAdapter = null
        binding.rvCast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        val showId = arguments?.getInt(EXTRA_TV_SHOW) ?: 0
        if (showId != 0) {
            tvSeriesDetailViewModel.tvShowDetails.observe(viewLifecycleOwner) { map ->
                val detail = map[showId]
                val credits = tvSeriesDetailViewModel.getCachedTvShowCredits(showId)
                val providers = tvSeriesDetailViewModel.getCachedTvShowProviders(showId)
                if (detail != null && credits != null && providers != null) {
                    showRemoteDetailTvShow(detail, credits, providers)
                }
            }
            tvSeriesDetailViewModel.tvShowCredits.observe(viewLifecycleOwner) { map ->
                val detail = tvSeriesDetailViewModel.getCachedTvShowDetail(showId)
                val credits = map[showId]
                val providers = tvSeriesDetailViewModel.getCachedTvShowProviders(showId)
                if (detail != null && credits != null && providers != null) {
                    showRemoteDetailTvShow(detail, credits, providers)
                }
            }
            tvSeriesDetailViewModel.tvShowProviders.observe(viewLifecycleOwner) { map ->
                val detail = tvSeriesDetailViewModel.getCachedTvShowDetail(showId)
                val credits = tvSeriesDetailViewModel.getCachedTvShowCredits(showId)
                val providers = map[showId]
                if (detail != null && credits != null && providers != null) {
                    showRemoteDetailTvShow(detail, credits, providers)
                }
            }
            if (tvSeriesDetailViewModel.getCachedTvShowDetail(showId) == null) {
                tvSeriesDetailViewModel.fetchTvShowDetail(showId)
            }
            if (tvSeriesDetailViewModel.getCachedTvShowCredits(showId) == null) {
                tvSeriesDetailViewModel.fetchTvShowCredits(showId)
            }
            if (tvSeriesDetailViewModel.getCachedTvShowProviders(showId) == null) {
                tvSeriesDetailViewModel.fetchTvShowProviders(showId)
            }
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

    private fun showRemoteDetailTvShow(
        detail: TvShowDetailResponse,
        credits: CreditsResponse?,
        providers: WatchProvidersResponse?
    ) {
        with(binding) {
            setFabIcon(false)
            tvShowPoster.alpha = 0.75F
            tvShowTitle.text = detail.name ?: "-"
            tvShowSinopsis.text = detail.overview ?: "-"
            tvFirstAirDate.text = detail.firstAirDate ?: "-"
            tvShowRating.text = detail.voteAverage?.toString() ?: "-"
            tvLength.text = detail.episodeRunTime?.firstOrNull()?.let { Utils.changeMinuteToDurationFormat(it) } ?: "-"
            val flatrate = providers?.results?.get("ID")?.flatrate
            if (!flatrate.isNullOrEmpty()) {
                val names = flatrate.joinToString(", ") { p -> p.providerName ?: "" }
                tvPlatform.text = "Platform \n$names"
            }
            val castList = credits?.cast?.sortedBy { it.order ?: Int.MAX_VALUE }?.take(10) ?: emptyList()
            castAdapter = CastAdapter(castList)
            rvCast.adapter = castAdapter
            btnSeeMoreCast.visibility = if ((credits?.cast?.size ?: 0) > 10) View.VISIBLE else View.GONE
            btnSeeMoreCast.setOnClickListener {
                Toast.makeText(requireContext(), "See more cast not implemented", Toast.LENGTH_SHORT).show()
            }
            val remoteSeasons = detail.seasons?.map {
                Season(
                    seasonId = it.id,
                    tvShowId = detail.id,
                    name = it.name ?: "",
                    overview = it.overview ?: "",
                    airDate = it.airDate ?: "",
                    seasonNumber = it.seasonNumber ?: 0,
                    episodeCount = it.episodeCount ?: 0,
                    posterPath = it.posterPath?.let { p -> if (p.startsWith("http")) p else "https://image.tmdb.org/t/p/w185$p" } ?: ""
                )
            } ?: emptyList()
            android.util.Log.d("TvSeriesDetailFragment", "remoteSeasons: $remoteSeasons")
            seasonAdapter.submitList(remoteSeasons)
        }
        Glide.with(this)
            .load(detail.backdropPath?.let { if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it" } ?: "")
            .transform(RoundedCorners(16))
            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.placholder))
            .into(binding.tvShowPoster)
    }

    private fun setFabIcon(isFavorited: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorited) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    @Deprecated("Deprecated in Java")
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