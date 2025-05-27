package com.saefulrdevs.mubeego.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import com.saefulrdevs.mubeego.core.util.DataMapper

class FavoriteMoviesViewModel(
    private val tmdbUseCase: TmdbUseCase
) : ViewModel() {
    fun getMovieFav(): LiveData<List<Movie>> =
        tmdbUseCase.getFavoriteMovie().asLiveData()

    fun getTvShowFav(): LiveData<List<TvShow>> =
        tmdbUseCase.getFavoriteTvShow().asLiveData()

    fun getFavoriteMixed(): LiveData<List<SearchItem>> {
        val result = MediatorLiveData<List<SearchItem>>()
        val movieLive = getMovieFav()
        val tvLive = getTvShowFav()
        result.addSource(movieLive) { movies ->
            val tvs = tvLive.value ?: emptyList()
            val movieItems = movies.map { DataMapper.movieToSearchItem(it) }
            val tvItems = tvs.map { DataMapper.tvShowToSearchItem(it) }
            result.value = (movieItems + tvItems).sortedByDescending { it.voteAverage }
        }
        result.addSource(tvLive) { tvs ->
            val movies = movieLive.value ?: emptyList()
            val movieItems = movies.map { DataMapper.movieToSearchItem(it) }
            val tvItems = tvs.map { DataMapper.tvShowToSearchItem(it) }
            result.value = (movieItems + tvItems).sortedByDescending { it.voteAverage }
        }
        return result
    }
}
