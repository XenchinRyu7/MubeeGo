package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import kotlinx.coroutines.flow.Flow

interface FavoriteFirestoreUseCase {
    suspend fun getFavoriteMovies(): List<Movie>
    suspend fun getFavoriteTvShows(): List<TvShow>
    suspend fun isMovieFavorited(movieId: Int): Boolean
    suspend fun isTvShowFavorited(tvShowId: Int): Boolean
    suspend fun getMovieDetailRemote(movieId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse?
    suspend fun getTvShowDetailRemote(tvShowId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse?
    fun observeFavoriteMovieIds(): Flow<List<Int>>
    fun observeFavoriteTvShowIds(): Flow<List<Int>>
}
