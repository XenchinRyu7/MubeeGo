package com.saefulrdevs.mubeego.core.data.source.local.room

import androidx.room.*
import com.saefulrdevs.mubeego.core.data.source.local.entity.MovieEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.SeasonEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowWithSeasonEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TmdbDao {

    //Movie
    @Query("SELECT * FROM movies ORDER BY voteCount DESC")
    fun getDiscoverMovie(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    fun getMovieById(movieId: String): Flow<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: List<MovieEntity>)

    @Query("DELETE FROM movies")
    fun clearMovies()

    //Tv Show
    @Query("SELECT * FROM tvShows ORDER BY voteCount DESC")
    fun getDiscoverTvShow(): Flow<List<TvShowEntity>>

    @Query("SELECT * FROM tvShows WHERE tvShowId = :tvShowId")
    fun getTvShowById(tvShowId: String): Flow<TvShowEntity>

    @Transaction
    @Query("SELECT * FROM tvShows WHERE tvShowId = :tvShowId")
    fun getSeasonByTvShowId(tvShowId: String): Flow<TvShowWithSeasonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShow(tvShow: List<TvShowEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSeason(season: List<SeasonEntity>)

    @Update
    fun updateTvShow(tvShow: TvShowEntity)

    // Genre
    @Query("SELECT * FROM genres")
    fun getGenres(): Flow<List<GenreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGenres(genres: List<GenreEntity>)
}