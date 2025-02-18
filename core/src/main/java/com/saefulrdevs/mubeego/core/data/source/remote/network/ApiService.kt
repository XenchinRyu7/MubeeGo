package com.saefulrdevs.mubeego.core.data.source.remote.network

import com.saefulrdevs.mubeego.core.data.source.remote.response.DiscoverMovieResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.DiscoverTvShowResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.SearchResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("discover/movie")
    suspend fun getDiscoverMovie(
        @Query("api_key") api_key: String,
        @Query("language") language: String
    ): DiscoverMovieResponse

    @GET("discover/tv")
    suspend fun getDiscoverTvShow(
        @Query("api_key") api_key: String,
        @Query("language") language: String
    ): DiscoverTvShowResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(
        @Path("movieId") movieId: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("append_to_response") append_to_response: String? = null
    ): MovieDetailResponse

    @GET("tv/{tvShowId}")
    suspend fun getTvShowDetail(
        @Path("tvShowId") tvShowId: String,
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("append_to_response") append_to_response: String? = null
    ): TvShowDetailResponse

    @GET("search/multi")
    suspend fun getSearchResult(
        @Query("api_key") api_key: String?,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: String
    ): SearchResponse

    @GET("trending/all/week")
    suspend fun getTrendings(
        @Query("api_key") api_key: String?
    ): SearchResponse
}