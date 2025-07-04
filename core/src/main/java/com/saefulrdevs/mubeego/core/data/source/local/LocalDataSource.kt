package com.saefulrdevs.mubeego.core.data.source.local

import com.saefulrdevs.mubeego.core.data.source.local.entity.GenreEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.MovieEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.SeasonEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowWithSeasonEntity
import com.saefulrdevs.mubeego.core.data.source.local.room.TmdbDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource private constructor(private val tmdbDao: TmdbDao) {

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(tmdbDao: TmdbDao): LocalDataSource =
            INSTANCE ?: LocalDataSource(tmdbDao)
    }

    fun getAllMovies(): Flow<List<MovieEntity>> = tmdbDao.getDiscoverMovie()

    fun getMovieById(movieId: String): Flow<MovieEntity> =
        tmdbDao.getMovieById(movieId).also {
            android.util.Log.d("DEBUG_LOCAL", "getMovieById called: movieId=$movieId")
        }

    fun getAllTvShow(): Flow<List<TvShowEntity>> = tmdbDao.getDiscoverTvShow()

    fun getTvShowById(showId: String): Flow<TvShowEntity> =
        tmdbDao.getTvShowById(showId)

    fun getTvShowWithSeason(showId: String): Flow<TvShowWithSeasonEntity> =
        tmdbDao.getSeasonByTvShowId(showId)

    fun getGenres(): Flow<List<GenreEntity>> = tmdbDao.getGenres()

    fun insertMovies(movies: List<MovieEntity>) = tmdbDao.insertMovie(movies)

    fun insertTvShow(tvShows: List<TvShowEntity>) = tmdbDao.insertTvShow(tvShows)

    fun insertSeason(seasons: List<SeasonEntity>) = tmdbDao.insertSeason(seasons)

    fun insertGenres(genres: List<GenreEntity>) = tmdbDao.insertGenres(genres)

    fun clearMovies() = tmdbDao.clearMovies()
}