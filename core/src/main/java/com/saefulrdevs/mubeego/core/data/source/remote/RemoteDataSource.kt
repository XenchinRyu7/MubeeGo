package com.saefulrdevs.mubeego.core.data.source.remote

import android.util.Log
import com.saefulrdevs.mubeego.core.BuildConfig
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiResponse
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiService
import com.saefulrdevs.mubeego.core.util.EspressoIdlingResource
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemMovie
import com.saefulrdevs.mubeego.core.data.source.remote.response.ResultsItemTvShow
import com.saefulrdevs.mubeego.core.data.source.remote.response.SearchResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class RemoteDataSource(private val apiService: ApiService) {

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
                Log.e("RemoteDataSource", e.toString())
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
                Log.e("RemoteDataSource", e.toString())
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
                Log.e("RemoteDataSource", e.toString())
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
                Log.e("RemoteDataSource", e.toString())
            }
        }
        EspressoIdlingResource.decrement()
        return f
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
                Log.e("RemoteDataSource", e.toString())
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
                Log.e("RemoteDataSource", e.toString())
            }
        }
        EspressoIdlingResource.decrement()
        return f
    }

    fun getUpcomingMoviesByDate(minDate: String, maxDate: String) = flow {
        EspressoIdlingResource.increment()
        try {
            android.util.Log.d("API_REQUEST", "getUpcomingMoviesByDate: minDate=$minDate, maxDate=$maxDate")
            val response = apiService.getUpcomingMoviesByDate(
                API_KEY, LANGUAGE, minDate, maxDate
            )
            val results = response.results
            android.util.Log.d("API_RESPONSE", "getUpcomingMoviesByDate: results=${results.map { it.title + ", " + it.releaseDate }}")
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

    fun getMovieWatchProviders(movieId: String): Flow<ApiResponse<com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse>> {
        EspressoIdlingResource.increment()
        val f = flow {
            try {
                val response = apiService.getMovieWatchProviders(movieId, API_KEY)
                emit(ApiResponse.Success(response))
            } catch (e: IOException) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
        EspressoIdlingResource.decrement()
        return f
    }

    companion object {
        private const val API_KEY = BuildConfig.TMDB_API_KEY
        private const val LANGUAGE = "en-US"

    }
}