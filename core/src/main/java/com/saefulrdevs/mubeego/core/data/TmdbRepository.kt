package com.saefulrdevs.mubeego.core.data

import com.saefulrdevs.mubeego.core.data.source.local.LocalDataSource
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiResponse
import com.saefulrdevs.mubeego.core.data.source.remote.RemoteDataSource
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.model.TvShowWithSeason
import com.saefulrdevs.mubeego.core.domain.repository.ITmdbRepository
import com.saefulrdevs.mubeego.core.util.AppExecutors
import com.saefulrdevs.mubeego.core.util.DataMapper.getSeasonEntity
import com.saefulrdevs.mubeego.core.util.DataMapper.toDomain
import com.saefulrdevs.mubeego.core.util.DataMapper.toEntity
import com.saefulrdevs.mubeego.core.util.DataMapper.toMovieEntity
import com.saefulrdevs.mubeego.core.util.DataMapper.toTvShowEntity
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemMovie
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemTvShow
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TmdbRepository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : ITmdbRepository {

    companion object {
        @Volatile
        private var instance: TmdbRepository? = null

        fun getInstance(
            remoteData: RemoteDataSource,
            localData: LocalDataSource,
            appExecutors: AppExecutors
        ): TmdbRepository =
            instance ?: synchronized(this) {
                instance ?: TmdbRepository(remoteData, localData, appExecutors).apply {
                    instance = this
                }
            }
    }

    override fun getDiscoverMovies(): Flow<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, List<ResultsItemMovie>>() {
            public override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getAllMovies().map { list ->
                    list.map {
                        it.toDomain()
                    }
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean =
                data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<ResultsItemMovie>>> =
                remoteDataSource.getDiscoverMovie()

            override suspend fun saveCallResult(data: List<ResultsItemMovie>) {
                val movies = data.map {
                    it.toEntity()
                }
                appExecutors.diskIO().execute {
                    localDataSource.insertMovies(movies)
                }
            }
        }.asFlow()
    }

    override fun getMovieDetail(movieId: String): Flow<Resource<Movie>> {
        return object : NetworkBoundResource<Movie, MovieDetailResponse>() {
            public override fun loadFromDB(): Flow<Movie> =
                localDataSource.getMovieById(movieId).map {
                    it.toDomain()
                }

            override fun shouldFetch(data: Movie?): Boolean =
                data?.runtime == null

            override suspend fun createCall(): Flow<ApiResponse<MovieDetailResponse>> =
                remoteDataSource.getMovie(movieId)

            override suspend fun saveCallResult(data: MovieDetailResponse) {
                val movie = data.toEntity()
                appExecutors.diskIO().execute {
                    localDataSource.insertMovies(listOf(movie))
                }
            }
        }.asFlow()
    }

    override fun getDiscoverTvShow(): Flow<Resource<List<TvShow>>> {
        return object : NetworkBoundResource<List<TvShow>, List<ResultsItemTvShow>>() {
            public override fun loadFromDB(): Flow<List<TvShow>> {
                return localDataSource.getAllTvShow().map { list ->
                    list.map {
                        it.toDomain()
                    }
                }
            }

            override fun shouldFetch(data: List<TvShow>?): Boolean =
                data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<ResultsItemTvShow>>> =
                remoteDataSource.getDiscoverTvShow()

            override suspend fun saveCallResult(data: List<ResultsItemTvShow>) {
                val shows = data.map {
                    it.toEntity()
                }
                appExecutors.diskIO().execute {
                    localDataSource.insertTvShow(shows)
                }
            }
        }.asFlow()
    }

    override fun getTvShowDetail(showId: String): Flow<Resource<TvShow>> {
        return object : NetworkBoundResource<TvShow, TvShowDetailResponse>() {
            public override fun loadFromDB(): Flow<TvShow> =
                localDataSource.getTvShowById(showId).map {
                    it.toDomain()
                }

            override fun shouldFetch(data: TvShow?): Boolean =
                data == null || data.runtime == 0 || data.overview.isEmpty()

            override suspend fun createCall(): Flow<ApiResponse<TvShowDetailResponse>> =
                remoteDataSource.getTvShow(showId)

            override suspend fun saveCallResult(data: TvShowDetailResponse) {
                val existingShow = localDataSource.getTvShowById(data.id.toString()).firstOrNull()
                val isFavorited = existingShow?.favorited ?: false

                val show = data.toEntity().copy(favorited = isFavorited)
                val seasons = data.getSeasonEntity()

                appExecutors.diskIO().execute {
                    localDataSource.insertTvShow(arrayListOf(show))
                    seasons?.let {
                        localDataSource.insertSeason(seasons)
                    }
                }
            }
        }.asFlow()
    }

    override fun getTvShowWithSeason(showId: String): Flow<Resource<TvShowWithSeason>> {
        return object : NetworkBoundResource<TvShowWithSeason, TvShowDetailResponse>() {
            public override fun loadFromDB(): Flow<TvShowWithSeason> =
                localDataSource.getTvShowWithSeason(showId).filterNotNull().map {
                    it.toDomain()
                }

            override fun shouldFetch(data: TvShowWithSeason?): Boolean =
                data == null || data.seasons.isEmpty()

            override suspend fun createCall(): Flow<ApiResponse<TvShowDetailResponse>> =
                remoteDataSource.getTvShow(showId)

            override suspend fun saveCallResult(data: TvShowDetailResponse) {
                val show = data.toEntity()
                val seasons = data.getSeasonEntity()

                appExecutors.diskIO().execute {
                    localDataSource.insertTvShow(arrayListOf(show))
                    seasons?.let {
                        localDataSource.insertSeason(seasons)
                    }
                }
            }
        }.asFlow()
    }

    override fun getFavoriteMovie(): Flow<List<Movie>> {
        return localDataSource.getFavoriteMovie().map { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    override fun getFavoriteTvShow(): Flow<List<TvShow>> {
        return localDataSource.getFavoriteTvShow().map { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    override fun setFavoriteMovie(movie: Movie, newState: Boolean) {
        val movieEntity = movie.toEntity()
        appExecutors.diskIO().execute {
            localDataSource.setMovieFavorite(movieEntity, newState)
        }
    }

    override fun setFavoriteTvShow(tvShow: TvShow, newState: Boolean) {
        val tvShowEntity = tvShow.toEntity()
        appExecutors.diskIO().execute {
            localDataSource.setTvShowFavorite(tvShowEntity, newState)
        }
    }

    override fun getSearchResult(title: String): Flow<Resource<List<SearchItem>>> {
        return flow {
            emit(Resource.Loading())
            val call = remoteDataSource.getSearchResult(title)
            when (val apiResponse = call.first()) {
                is ApiResponse.Success -> {
                    val resource = apiResponse.data.results?.let { list ->
                        Resource.Success(list.filter {
                            it.mediaType == "tv" || it.mediaType == "movie"
                        }.map {
                            it.toDomain()
                        })
                    }
                    val shows = apiResponse.data.results?.let { list ->
                        list.filter { it.mediaType == "tv" }.map {
                            it.toTvShowEntity()
                        }
                    }
                    if (shows != null) {
                        appExecutors.diskIO().execute {
                            localDataSource.insertTvShow(shows)
                        }
                    }
                    val movie = apiResponse.data.results?.let { list ->
                        list.filter { it.mediaType == "movie" }.map {
                            it.toMovieEntity()
                        }
                    }
                    if (movie != null) {
                        appExecutors.diskIO().execute {
                            localDataSource.insertMovies(movie)
                        }
                    }
                    if (resource != null) {
                        emit(resource)
                    } else {
                        emit(Resource.Error("Not Found"))
                    }
                }

                is ApiResponse.Empty -> {
                    emit(Resource.Error("Not Found"))
                }

                is ApiResponse.Error -> {
                    emit(
                        Resource.Error(apiResponse.errorMessage)
                    )
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getTrendings(): Flow<Resource<List<SearchItem>>> {
        return flow {
            emit(Resource.Loading())
            val call = remoteDataSource.getTrendings()
            when (val apiResponse = call.first()) {
                is ApiResponse.Success -> {
                    val resource = apiResponse.data.results?.let { list ->
                        Resource.Success(list.filter {
                            it.mediaType == "tv" || it.mediaType == "movie"
                        }.map {
                            it.toDomain()
                        })
                    }
                    val shows = apiResponse.data.results?.let { list ->
                        list.filter { it.mediaType == "tv" }.map {
                            it.toTvShowEntity()
                        }
                    }
                    if (shows != null) {
                        appExecutors.diskIO().execute {
                            localDataSource.insertTvShow(shows)
                        }
                    }
                    val movie = apiResponse.data.results?.let { list ->
                        list.filter { it.mediaType == "movie" }.map {
                            it.toMovieEntity()
                        }
                    }
                    if (movie != null) {
                        appExecutors.diskIO().execute {
                            localDataSource.insertMovies(movie)
                        }
                    }
                    if (resource != null) {
                        emit(resource)
                    } else {
                        emit(Resource.Error("Not Found"))
                    }
                }

                is ApiResponse.Empty -> {
                    emit(Resource.Error("Not Found"))
                }

                is ApiResponse.Error -> {
                    emit(
                        Resource.Error(apiResponse.errorMessage)
                    )
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}