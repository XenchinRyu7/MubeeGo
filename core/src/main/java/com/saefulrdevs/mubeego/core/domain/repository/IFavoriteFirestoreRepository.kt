package com.saefulrdevs.mubeego.core.domain.repository

import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import kotlinx.coroutines.flow.Flow

interface IFavoriteFirestoreRepository {
    suspend fun getFavoriteMovies(): List<Movie>
    suspend fun getFavoriteTvShows(): List<TvShow>
    suspend fun isMovieFavorited(movieId: Int): Boolean
    suspend fun isTvShowFavorited(tvShowId: Int): Boolean
    fun observeFavoriteMovieIds(): Flow<List<Int>>
    fun observeFavoriteTvShowIds(): Flow<List<Int>>
}
