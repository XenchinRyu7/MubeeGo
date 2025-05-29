package com.saefulrdevs.mubeego.core.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
import com.saefulrdevs.mubeego.core.domain.model.Genre
import com.saefulrdevs.mubeego.core.data.source.local.entity.GenreEntity
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TmdbRepository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ITmdbRepository {

    private var favoriteMovieListener: ListenerRegistration? = null
    private var favoriteTvListener: ListenerRegistration? = null

    companion object {
        @Volatile
        private var instance: TmdbRepository? = null

        fun getInstance(
            remoteData: RemoteDataSource,
            localData: LocalDataSource,
            appExecutors: AppExecutors,
            auth: FirebaseAuth,
            firestore: FirebaseFirestore
        ): TmdbRepository =
            instance ?: synchronized(this) {
                instance ?: TmdbRepository(remoteData, localData, appExecutors, auth, firestore).apply {
                    instance = this
                }
            }
    }

    override fun getNowPlayingMovies(): Flow<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, List<ResultsItemMovie>>() {
            public override fun loadFromDB(): Flow<List<Movie>> {
                return localDataSource.getAllMovies().map { list ->
                    list.map {
                        it.toDomain()
                    }
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean = data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<ResultsItemMovie>>> =
                remoteDataSource.getNowPlayingMovies()

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
        return localDataSource.getFavoriteMovie().map { it.map { it.toDomain() } }
    }

    override fun getFavoriteTvShow(): Flow<List<TvShow>> {
        return localDataSource.getFavoriteTvShow().map { it.map { it.toDomain() } }
    }

    override fun setFavoriteMovie(movie: Movie, newState: Boolean) {
        Log.d("DEBUG_TMDB", "setFavoriteMovie called: ${movie.title}, favorited: $newState")
        val movieEntity = movie.toEntity()
        appExecutors.diskIO().execute {
            localDataSource.setMovieFavorite(movieEntity, newState)

            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.e("DEBUG_TMDB", "User not logged in, skipping Firestore save")
                return@execute
            }
            val movieRef = firestore.collection("users").document(userId).collection("favorites_movies").document(movie.movieId.toString())

            if (newState) {
                Log.d("DEBUG_TMDB", "Saving to Firestore for movieId: ${movie.movieId}")
                movieRef.set(hashMapOf("favorited" to true))
                    .addOnSuccessListener {
                        Log.d("DEBUG_TMDB", "Successfully saved to Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e("DEBUG_TMDB", "Error saving to Firestore", e)
                    }
            } else {
                Log.d("DEBUG_TMDB", "Deleting from Firestore for movieId: ${movie.movieId}")
                movieRef.delete()
                    .addOnSuccessListener {
                        Log.d("DEBUG_TMDB", "Successfully deleted from Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e("DEBUG_TMDB", "Error deleting from Firestore", e)
                    }
            }
        }
    }

    override fun setFavoriteTvShow(tvShow: TvShow, newState: Boolean) {
        val tvEntity = tvShow.toEntity()
        appExecutors.diskIO().execute {
            localDataSource.setTvShowFavorite(tvEntity, newState)

            val userId = auth.currentUser?.uid ?: return@execute
            val tvRef = firestore.collection("users").document(userId).collection("favorites_tv").document(tvShow.tvShowId.toString())

            if (newState) {
                tvRef.set(hashMapOf("favorited" to true))
            } else {
                tvRef.delete()
            }
        }
    }

    override fun observeFavoriteMoviesRealtime() {
        val userId = auth.currentUser?.uid ?: return

        favoriteMovieListener = firestore.collection("users")
            .document(userId)
            .collection("favorites_movies")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.mapNotNull { it.id.toIntOrNull() }?.forEach { movieId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        fetchAndSaveMovie(movieId)
                    }
                }
            }
    }

    override fun observeFavoriteTvShowsRealtime() {
        val userId = auth.currentUser?.uid ?: return

        favoriteTvListener = firestore.collection("users")
            .document(userId)
            .collection("favorites_tv")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.mapNotNull { it.id.toIntOrNull() }?.forEach { tvId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        fetchAndSaveTvShow(tvId)
                    }
                }
            }
    }

    private suspend fun fetchAndSaveMovie(id: Int) {
        val response = remoteDataSource.getMovieDetail(id.toString())
        response?.let {
            val entity = it.toEntity().apply { favorited = true }
            localDataSource.insertMovies(listOf(entity))
        }
    }

    private suspend fun fetchAndSaveTvShow(id: Int) {
        val response = remoteDataSource.getTvShowDetail(id.toString())
        response?.let {
            val entity = it.toEntity().apply { favorited = true }
            localDataSource.insertTvShow(listOf(entity))
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

    override fun getUpcomingMoviesByDate(
        minDate: String,
        maxDate: String
    ): Flow<Resource<List<Movie>>> =
        flow<Resource<List<Movie>>> {
            val response = remoteDataSource.getUpcomingMoviesByDate(minDate, maxDate)
            response.collect { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Success -> {
                        val movies = apiResponse.data.map {
                            val posterPath = it.posterPath?.let { path ->
                                if (path.startsWith("http")) path else "https://image.tmdb.org/t/p/w500$path"
                            } ?: ""
                            val backdropPath = it.backdropPath?.let { path ->
                                if (path.startsWith("http")) path else "https://image.tmdb.org/t/p/w500$path"
                            } ?: ""
                            Movie(
                                movieId = it.id,
                                title = it.title,
                                overview = it.overview,
                                posterPath = posterPath,
                                backdropPath = backdropPath,
                                releaseDate = it.releaseDate,
                                voteCount = it.voteCount,
                                voteAverage = it.voteAverage,
                                runtime = 0,
                                genres = "",
                                youtubeTrailerId = "",
                                favorited = false
                            )
                        }
                        emit(Resource.Success(movies))
                    }

                    is ApiResponse.Empty -> {
                        emit(Resource.Success(emptyList()))
                    }

                    is ApiResponse.Error -> {
                        emit(Resource.Error<List<Movie>>(apiResponse.errorMessage))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    fun getMovieWatchProviders(movieId: String): Flow<ApiResponse<com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse>> {
        return remoteDataSource.getMovieWatchProviders(movieId)
    }

    override fun getGenres(): Flow<Resource<List<Genre>>> {
        return object : NetworkBoundResource<List<Genre>, List<GenreResponse>>() {
            override fun loadFromDB(): Flow<List<Genre>> {
                return localDataSource.getGenres().map { list ->
                    list.map { Genre(it.id, it.name) }
                }
            }

            override fun shouldFetch(data: List<Genre>?): Boolean = data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<List<GenreResponse>>> =
                remoteDataSource.getMovieGenres()

            override suspend fun saveCallResult(data: List<GenreResponse>) {
                val genres = data.map { GenreEntity(it.id, it.name) }
                appExecutors.diskIO().execute {
                    localDataSource.insertGenres(genres)
                }
            }
        }.asFlow()
    }

    override suspend fun getMovieDetailRemote(movieId: String): MovieDetailResponse? {
        return remoteDataSource.getMovieDetail(movieId)
    }

    override suspend fun getGenresRemote(): List<GenreResponse>? {
        return try {
            remoteDataSource.getMovieGenresOnce()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMovieCreditsRemote(movieId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse? {
        return try {
            remoteDataSource.getMovieCredits(movieId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMovieWatchProvidersRemote(movieId: String): com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse? {
        return try {
            remoteDataSource.getMovieWatchProvidersOnce(movieId)
        } catch (e: Exception) {
            null
        }
    }

    override fun clearMovies() {
        localDataSource.clearMovies()
    }
}