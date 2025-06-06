package com.saefulrdevs.mubeego.core.data.source.remote

import com.saefulrdevs.mubeego.core.BuildConfig
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiResponse
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiService
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.util.EspressoIdlingResource
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemMovie
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemTvShow
import com.saefulrdevs.mubeego.core.data.source.remote.response.SearchResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class RemoteDataSource(private val apiService: ApiService) {

    fun getNowPlayingMovies(): Flow<ApiResponse<List<ResultsItemMovie>>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getNowPlayingMovies(API_KEY)
                val results = response.results
                if (results.isNotEmpty()) {
                    emit(ApiResponse.Success(results))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
        EspressoIdlingResource.decrement()
        return f
    }

    fun getDiscoverMovie(): Flow<ApiResponse<List<ResultsItemMovie>>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getDiscoverMovie(API_KEY, LANGUAGE)
                val results = response.results
                if (results.isNotEmpty()) {
                    emit(ApiResponse.Success(results))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
        EspressoIdlingResource.decrement()
        return f
    }

    fun getDiscoverTvShow(): Flow<ApiResponse<List<ResultsItemTvShow>>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getDiscoverTvShow(API_KEY, LANGUAGE)
                val results = response.results
                if (results.isNotEmpty()) {
                    emit(ApiResponse.Success(results))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
        EspressoIdlingResource.decrement()
        return f
    }

    fun getMovie(movieId: String): Flow<ApiResponse<MovieDetailResponse>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response =
                    apiService.getMovieDetail(movieId, API_KEY, LANGUAGE, "videos")
                emit(ApiResponse.Success(response))
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }
        EspressoIdlingResource.decrement()
        return f
    }

    fun getTvShow(showId: String): Flow<ApiResponse<TvShowDetailResponse>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response =
                    apiService.getTvShowDetail(showId, API_KEY, LANGUAGE, "videos")
                emit(ApiResponse.Success(response))
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }
        EspressoIdlingResource.decrement()
        return f
    }

    suspend fun getMovieDetail(movieId: String): MovieDetailResponse? {
        return try {
            apiService.getMovieDetail(movieId, API_KEY, LANGUAGE, "videos")
        } catch (_: IOException) {
            null
        }
    }

    suspend fun getTvShowDetail(showId: String): TvShowDetailResponse? {
        return try {
            apiService.getTvShowDetail(showId, API_KEY, LANGUAGE, "videos")
        } catch (_: IOException) {
            null
        }
    }

    fun getSearchResult(title: String): Flow<ApiResponse<SearchResponse>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response =
                    apiService.getSearchResult(API_KEY, LANGUAGE, title, "1")
                emit(ApiResponse.Success(response))
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }
        EspressoIdlingResource.decrement()
        return f
    }

    fun getTrendings(): Flow<ApiResponse<SearchResponse>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getTrendings(API_KEY)
                emit(ApiResponse.Success(response))
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }
        EspressoIdlingResource.decrement()
        return f
    }

    fun getUpcomingMoviesByDate(minDate: String, maxDate: String) = flow {
        EspressoIdlingResource.increment()
        try {
            val response = apiService.getUpcomingMoviesByDate(
                API_KEY, LANGUAGE, minDate, maxDate
            )
            val results = response.results
            if (results.isNotEmpty()) {
                emit(ApiResponse.Success(results))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: IOException) {
            emit(ApiResponse.Error(e.toString()))
        }
        EspressoIdlingResource.decrement()
    }.flowOn(Dispatchers.IO)

    fun getMovieWatchProviders(movieId: String): Flow<ApiResponse<WatchProvidersResponse>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getMovieWatchProviders(movieId, API_KEY)
                emit(ApiResponse.Success(response))
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
        EspressoIdlingResource.decrement()
        return f
    }

    fun getMovieGenres(): Flow<ApiResponse<List<GenreResponse>>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getMovieGenres(API_KEY, LANGUAGE)
                val results = response.genres
                if (results.isNotEmpty()) {
                    emit(ApiResponse.Success(results))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
        EspressoIdlingResource.decrement()
        return f
    }

    suspend fun getMovieGenresOnce(): List<GenreResponse>? {
        return try {
            apiService.getMovieGenres(API_KEY, LANGUAGE).genres
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieCredits(movieId: String): CreditsResponse? {
        return try {
            apiService.getMovieCredits(movieId, API_KEY)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getMovieWatchProvidersOnce(movieId: String): WatchProvidersResponse? {
        return try {
            apiService.getMovieWatchProviders(movieId, API_KEY)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getTvShowDetailOnce(tvShowId: String): TvShowDetailResponse? {
        return try {
            apiService.getTvShowDetail(tvShowId, API_KEY, LANGUAGE)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getTvShowAggregateCredits(tvShowId: String): CreditsResponse? {
        return try {
            apiService.getTvShowAggregateCredits(tvShowId, API_KEY)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getTvShowWatchProvidersOnce(tvShowId: String): WatchProvidersResponse? {
        return try {
            apiService.getTvShowWatchProviders(tvShowId, API_KEY)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val API_KEY = BuildConfig.TMDB_API_KEY
        private const val LANGUAGE = "en-US"
    }
}