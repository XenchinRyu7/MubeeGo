package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.repository.IFavoriteFirestoreRepository
import kotlinx.coroutines.flow.Flow

class FavoriteFirestoreInteractor(
    private val repo: IFavoriteFirestoreRepository,
    private val tmdbUseCase: TmdbUseCase
) : FavoriteFirestoreUseCase {
    override suspend fun getFavoriteMovies(): List<Movie> = repo.getFavoriteMovies()
    override suspend fun getFavoriteTvShows(): List<TvShow> = repo.getFavoriteTvShows()
    override suspend fun isMovieFavorited(movieId: Int): Boolean = repo.isMovieFavorited(movieId)
    override suspend fun isTvShowFavorited(tvShowId: Int): Boolean = repo.isTvShowFavorited(tvShowId)
    override suspend fun getMovieDetailRemote(movieId: String): MovieDetailResponse? = tmdbUseCase.getMovieDetailRemote(movieId)
    override suspend fun getTvShowDetailRemote(tvShowId: String): TvShowDetailResponse? = tmdbUseCase.getTvShowDetailRemote(tvShowId)
    override fun observeFavoriteMovieIds(): Flow<List<Int>> = repo.observeFavoriteMovieIds()
    override fun observeFavoriteTvShowIds(): Flow<List<Int>> = repo.observeFavoriteTvShowIds()
}
