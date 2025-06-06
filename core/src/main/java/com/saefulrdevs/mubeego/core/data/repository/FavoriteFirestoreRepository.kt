package com.saefulrdevs.mubeego.core.data.repository

import com.saefulrdevs.mubeego.core.data.source.remote.firestore.FavoriteFirestoreDataSource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.repository.IFavoriteFirestoreRepository
import kotlinx.coroutines.flow.Flow

class FavoriteFirestoreRepository(
    private val remote: FavoriteFirestoreDataSource
) : IFavoriteFirestoreRepository {
    override suspend fun getFavoriteMovies(): List<Movie> = remote.getFavoriteMovies()
    override suspend fun getFavoriteTvShows(): List<TvShow> = remote.getFavoriteTvShows()
    override suspend fun isMovieFavorited(movieId: Int): Boolean = remote.isMovieFavorited(movieId)
    override suspend fun isTvShowFavorited(tvShowId: Int): Boolean = remote.isTvShowFavorited(tvShowId)
    override fun observeFavoriteMovieIds(): Flow<List<Int>> = remote.observeFavoriteMovieIds()
    override fun observeFavoriteTvShowIds(): Flow<List<Int>> = remote.observeFavoriteTvShowIds()
}
