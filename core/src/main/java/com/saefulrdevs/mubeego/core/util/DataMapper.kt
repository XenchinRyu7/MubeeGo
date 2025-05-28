package com.saefulrdevs.mubeego.core.util

import com.saefulrdevs.mubeego.core.data.source.local.entity.MovieEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.SeasonEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowWithSeasonEntity
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemMovie
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemTvShow
import com.saefulrdevs.mubeego.core.data.source.remote.response.SearchResultsItem
import com.saefulrdevs.mubeego.core.data.source.remote.response.SeasonsItem
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.VideoResults
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.model.Season
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.model.TvShowWithSeason
import org.json.JSONArray

object DataMapper {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

    private fun String.toImageUrl() : String {
        return "$IMAGE_BASE_URL$this"
    }

    // DEBUG: log genreIds saat mapping ResultsItemMovie ke MovieEntity
    fun ResultsItemMovie.toEntity(): MovieEntity {
        android.util.Log.d("DataMapper", "toEntity genreIds: $genreIds for movieId: $id")
        return MovieEntity(
            movieId = id,
            title = title,
            overview = overview,
            posterPath = posterPath.toImageUrl(),
            backdropPath = backdropPath.toImageUrl(),
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            runtime = 0,
            genres = JSONArray(genreIds).toString(), // <-- Simpan genreIds sebagai JSON array
            youtubeTrailerId = "",
            favorited = false,
            originalLanguage = originalLanguage
        )
    }

    fun MovieEntity.toDomain() : Movie {
        return Movie(
            movieId = movieId,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteCount = voteCount,
            voteAverage = voteAverage,
            runtime = runtime,
            genres = genres,
            youtubeTrailerId = youtubeTrailerId,
            favorited = favorited,
            originalLanguage = originalLanguage
        )
    }

    fun Movie.toEntity() : MovieEntity {
        return MovieEntity(
            movieId = movieId,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteCount = voteCount,
            voteAverage = voteAverage,
            runtime = runtime,
            genres = genres,
            youtubeTrailerId = youtubeTrailerId,
            favorited = favorited,
            originalLanguage = originalLanguage
        )
    }

    fun MovieDetailResponse.toEntity() : MovieEntity {
        val listOfGenreId = ArrayList<Int>()
        if (genres != null) {
            for (genre in (genres)) {
                if (genre != null && genre.id != null) {
                    listOfGenreId.add(genre.id)
                }
            }
        }

        return MovieEntity(
            movieId = id,
            title = title ?: "",
            overview = overview ?: "",
            posterPath = posterPath?.toImageUrl() ?: "",
            backdropPath = backdropPath?.toImageUrl() ?: "",
            releaseDate = releaseDate ?: "",
            voteAverage = voteAverage ?: 0.0,
            voteCount = voteCount ?: 0,
            runtime = runtime ?: 0,
            genres = JSONArray(listOfGenreId).toString(), // <-- Simpan genre id
            youtubeTrailerId = videos?.getYoutubeTrailerId() ?: "",
            favorited = false,
            originalLanguage = originalLanguage ?: ""
        )
    }

    fun TvShowEntity.toDomain() : TvShow {
        return TvShow(
            tvShowId = tvShowId,
            name= name,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            voteCount = voteCount,
            voteAverage = voteAverage,
            firstAirDate = firstAirDate,
            genres = genres,
            runtime = runtime,
            youtubeTrailerId = youtubeTrailerId,
            favorited = favorited
        )
    }

    fun ResultsItemTvShow.toEntity() : TvShowEntity {
        return TvShowEntity(
            tvShowId = id,
            name= name,
            overview = overview,
            posterPath = posterPath.toImageUrl(),
            backdropPath = backdropPath.toImageUrl(),
            voteCount = voteCount,
            voteAverage = voteAverage,
            firstAirDate = firstAirDate
        )
    }

    fun TvShowDetailResponse.toEntity() : TvShowEntity {
        val listOfGenre = ArrayList<String>()
        if (genres != null) {
            for (genre in (genres)) {
                if (genre != null) {
                    genre.name?.let { listOfGenre.add(it) }
                }
            }
        }

        return TvShowEntity(
            tvShowId = id,
            name= name ?: "",
            overview = overview ?: "",
            posterPath = posterPath?.toImageUrl() ?: "",
            backdropPath = backdropPath?.toImageUrl() ?: "",
            voteCount = voteCount ?: 0,
            voteAverage = voteAverage ?: 0.0,
            firstAirDate = firstAirDate ?: "",
            genres = JSONArray(listOfGenre).toString(),
            runtime = (if (!episodeRunTime.isNullOrEmpty()) episodeRunTime[0] else 0) ?: 0,
            youtubeTrailerId = videos?.getYoutubeTrailerId() ?: ""
        )
    }

    fun TvShow.toEntity() : TvShowEntity {
        return TvShowEntity(
            tvShowId = tvShowId,
            name= name,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            voteCount = voteCount,
            voteAverage = voteAverage,
            firstAirDate = firstAirDate,
            genres = genres,
            runtime = runtime,
            youtubeTrailerId = youtubeTrailerId,
            favorited = favorited
        )
    }

    fun TvShowDetailResponse.getSeasonEntity() : List<SeasonEntity>? {
        return seasons?.map {
            it.toEntity(id)
        }
    }

    fun SeasonsItem.toEntity(tvShowId: Int) : SeasonEntity {
        return SeasonEntity(
            seasonId = id,
            tvShowId = tvShowId,
            name = name ?: "",
            overview = overview ?: "",
            airDate = airDate ?: "",
            seasonNumber = seasonNumber ?: 0,
            episodeCount = episodeCount ?: 0,
            posterPath = posterPath?.toImageUrl() ?: ""
        )
    }

    fun SeasonEntity.toDomain() : Season {
        return Season(
            seasonId = seasonId,
            tvShowId = tvShowId,
            name = name,
            overview = overview,
            airDate = airDate,
            seasonNumber = seasonNumber,
            episodeCount = episodeCount,
            posterPath = posterPath
        )
    }


    fun TvShowWithSeasonEntity.toDomain() : TvShowWithSeason {
        return TvShowWithSeason(
            tvShow = tvShow.toDomain(),
            seasons = seasons.map {
                it.toDomain()
            }
        )
    }

    fun SearchResultsItem.toDomain() : SearchItem {
        return SearchItem(
            id = id,
            name = (if (mediaType == "tv") name else title)  ?: "",
            posterPath = posterPath?.toImageUrl() ?: "",
            backdropPath = backdropPath?.toImageUrl() ?: "",
            mediaType = mediaType,
            overview = overview ?: "",
            voteCount = voteCount ?: 0,
            voteAverage = voteAverage ?: 0.0,
            releaseOrAirDate = (if (mediaType == "tv") firstAirDate else releaseDate)  ?: ""
        )
    }

    fun SearchResultsItem.toTvShowEntity() : TvShowEntity {
        return TvShowEntity(
            tvShowId = id,
            name = name ?: "",
            posterPath = posterPath?.toImageUrl() ?: "",
            backdropPath = backdropPath?.toImageUrl() ?: "",
            overview = overview ?: "",
            voteCount = voteCount ?: 0,
            voteAverage = voteAverage ?: 0.0,
            firstAirDate = firstAirDate ?: ""
        )
    }

    fun SearchResultsItem.toMovieEntity() : MovieEntity {
        return MovieEntity(
            movieId = id,
            title = title ?: "",
            posterPath = posterPath?.toImageUrl() ?: "",
            backdropPath = backdropPath?.toImageUrl() ?: "",
            overview = overview ?: "",
            voteCount = voteCount ?: 0,
            voteAverage = voteAverage ?: 0.0,
            releaseDate = releaseDate ?: ""
        )
    }

    fun movieToSearchItem(movie: Movie): SearchItem {
        return SearchItem(
            id = movie.movieId,
            name = movie.title,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            mediaType = "movie",
            overview = movie.overview,
            voteCount = movie.voteCount,
            voteAverage = movie.voteAverage,
            releaseOrAirDate = movie.releaseDate
        )
    }

    fun tvShowToSearchItem(tvShow: TvShow): SearchItem {
        return SearchItem(
            id = tvShow.tvShowId,
            name = tvShow.name,
            posterPath = tvShow.posterPath,
            backdropPath = tvShow.backdropPath,
            mediaType = "tv",
            overview = tvShow.overview,
            voteCount = tvShow.voteCount,
            voteAverage = tvShow.voteAverage,
            releaseOrAirDate = tvShow.firstAirDate
        )
    }

    private fun VideoResults.getYoutubeTrailerId(): String? {
        val ytTrailer = this.results?.filter { it.site == "YouTube" && it.type == "Trailer" }
        if (!ytTrailer.isNullOrEmpty()) {
            return ytTrailer[0].key
        }
        val ytVideo = this.results?.filter { it.site == "YouTube" }
        if (!ytVideo.isNullOrEmpty()) {
            return ytVideo[0].key
        }
        return null
    }
}