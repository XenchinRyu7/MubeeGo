package com.saefulrdevs.mubeego.core.data.source.remote.network

import com.saefulrdevs.mubeego.core.data.source.remote.response.DiscoverMovieResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.DiscoverTvShowResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.SearchResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
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

    @GET("movie/{movieId}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movieId") movieId: String,
        @Query("api_key") api_key: String
    ): WatchProvidersResponse

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

    @GET("discover/movie")
    suspend fun getUpcomingMoviesByDate(
        @Query("api_key") api_key: String,
        @Query("language") language: String,
        @Query("primary_release_date.gte") minDate: String,
        @Query("primary_release_date.lte") maxDate: String,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("with_release_type") releaseType: String = "2|3"
    ): DiscoverMovieResponse
}