package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import com.saefulrdevs.mubeego.core.domain.model.Genre
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.model.TvShowWithSeason
import kotlinx.coroutines.flow.Flow

interface ITmdbRepository {
    fun getNowPlayingMovies(): Flow<Resource<List<Movie>>>
    fun getDiscoverMovies(): Flow<Resource<List<Movie>>>
    fun getDiscoverTvShow(): Flow<Resource<List<TvShow>>>
    fun getMovieDetail(movieId: String): Flow<Resource<Movie>>
    fun getTvShowDetail(showId: String): Flow<Resource<TvShow>>
    fun getTvShowWithSeason(showId: String): Flow<Resource<TvShowWithSeason>>
    fun getFavoriteMovie(): Flow<List<Movie>>
    fun getFavoriteTvShow(): Flow<List<TvShow>>
    fun setFavoriteMovie(movie: Movie, newState: Boolean)
    fun setFavoriteTvShow(tvShow: TvShow, newState: Boolean)
    fun observeFavoriteMoviesRealtime()
    fun observeFavoriteTvShowsRealtime()
    fun getSearchResult(title: String): Flow<Resource<List<SearchItem>>>
    fun getTrendings(): Flow<Resource<List<SearchItem>>>
    fun getUpcomingMoviesByDate(minDate: String, maxDate: String): Flow<Resource<List<Movie>>>
    fun getGenres(): Flow<Resource<List<Genre>>>
    fun clearMovies()

    suspend fun getMovieDetailRemote(movieId: String): MovieDetailResponse?
    suspend fun getGenresRemote(): List<GenreResponse>?
    suspend fun getMovieCreditsRemote(movieId: String): CreditsResponse?
    suspend fun getMovieWatchProvidersRemote(movieId: String): WatchProvidersResponse?
    suspend fun getTvShowDetailRemote(tvShowId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse?
    suspend fun getTvShowAggregateCreditsRemote(tvShowId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse?
    suspend fun getTvShowWatchProvidersRemote(tvShowId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse?
}