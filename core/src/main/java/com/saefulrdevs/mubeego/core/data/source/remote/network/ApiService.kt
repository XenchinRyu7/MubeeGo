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
        @Query("apiKey") apiKey: String,
        @Query("language") language: String
    ): DiscoverMovieResponse

    @GET("discover/tv")
    suspend fun getDiscoverTvShow(
        @Query("apiKey") apiKey: String,
        @Query("language") language: String
    ): DiscoverTvShowResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(
        @Path("movieId") movieId: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query("appendToResponse") appendToResponse: String? = null
    ): MovieDetailResponse

    @GET("tv/{tvShowId}")
    suspend fun getTvShowDetail(
        @Path("tvShowId") tvShowId: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String,
        @Query("appendToResponse") appendToResponse: String? = null
    ): TvShowDetailResponse

    @GET("search/multi")
    suspend fun getSearchResult(
        @Query("apiKey") apiKey: String?,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: String
    ): SearchResponse

    @GET("trending/all/week")
    suspend fun getTrendings(
        @Query("apiKey") apiKey: String?
    ): SearchResponse
}