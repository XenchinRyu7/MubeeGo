package com.saefulrdevs.mubeego.core.domain.usecase

import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.domain.model.Genre
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.model.TvShowWithSeason
import com.saefulrdevs.mubeego.core.domain.repository.ITmdbRepository
import kotlinx.coroutines.flow.Flow

class TmdbInteractor(private val tmdbRepository: ITmdbRepository) : TmdbUseCase {
    override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> {
        return tmdbRepository.getNowPlayingMovies()
    }

    override fun getDiscoverMovies(): Flow<Resource<List<Movie>>> {
        return tmdbRepository.getDiscoverMovies()
    }

    override fun getDiscoverTvShow(): Flow<Resource<List<TvShow>>> {
        return tmdbRepository.getDiscoverTvShow()
    }

    override fun getMovieDetail(movieId: String): Flow<Resource<Movie>> {
        return tmdbRepository.getMovieDetail(movieId)
    }

    override fun getTvShowDetail(showId: String): Flow<Resource<TvShow>> {
        return tmdbRepository.getTvShowDetail(showId)
    }

    override fun getTvShowWithSeason(showId: String): Flow<Resource<TvShowWithSeason>> {
        return tmdbRepository.getTvShowWithSeason(showId)
    }

    override fun getFavoriteMovie(): Flow<List<Movie>> {
        return tmdbRepository.getFavoriteMovie()
    }

    override fun getFavoriteTvShow(): Flow<List<TvShow>> {
        return tmdbRepository.getFavoriteTvShow()
    }

    override fun setFavoriteMovie(movie: Movie, newState: Boolean) {
        return tmdbRepository.setFavoriteMovie(movie, newState)
    }

    override fun setFavoriteTvShow(tvShow: TvShow, newState: Boolean) {
        return tmdbRepository.setFavoriteTvShow(tvShow, newState)
    }

    override fun observeFavoriteMoviesRealtime() {
        return tmdbRepository.observeFavoriteMoviesRealtime()
    }

    override fun observeFavoriteTvShowsRealtime() {
        return tmdbRepository.observeFavoriteTvShowsRealtime()
    }

    override fun getSearchResult(title: String): Flow<Resource<List<SearchItem>>> {
        return tmdbRepository.getSearchResult(title)
    }

    override fun getPopular(): Flow<Resource<List<SearchItem>>> {
        return tmdbRepository.getTrendings()
    }

    override fun getUpcomingMoviesByDate(minDate: String, maxDate: String): Flow<Resource<List<Movie>>> {
        return tmdbRepository.getUpcomingMoviesByDate(minDate, maxDate)
    }

    override fun getMovieWatchProviders(movieId: String) =
        (tmdbRepository as? com.saefulrdevs.mubeego.core.data.TmdbRepository)?.getMovieWatchProviders(movieId)
            ?: throw NotImplementedError("getMovieWatchProviders not implemented in repository")

    override fun getGenres() = tmdbRepository.getGenres()

    override fun clearMovies() {
        tmdbRepository.clearMovies()
    }

    override suspend fun getMovieDetailRemote(movieId: String) = tmdbRepository.getMovieDetailRemote(movieId)

    override suspend fun getGenresRemote() = tmdbRepository.getGenresRemote()

    override suspend fun getMovieCreditsRemote(movieId: String): CreditsResponse? {
        return tmdbRepository.getMovieCreditsRemote(movieId)
    }

    override suspend fun getMovieWatchProvidersRemote(movieId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse? {
        return tmdbRepository.getMovieWatchProvidersRemote(movieId)
    }

    override suspend fun getTvShowDetailRemote(tvShowId: String) = tmdbRepository.getTvShowDetailRemote(tvShowId)
    override suspend fun getTvShowAggregateCreditsRemote(tvShowId: String) = tmdbRepository.getTvShowAggregateCreditsRemote(tvShowId)
    override suspend fun getTvShowWatchProvidersRemote(tvShowId: String) = tmdbRepository.getTvShowWatchProvidersRemote(tvShowId)
}