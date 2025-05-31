# Kode Manajemen Database SQLite (Core Module)

---

## 1. TmdbDatabase.kt (Room Database + SQLCipher)

```kotlin
// [SQLITE-MANAGEMENT] d:/Kotlin Project/MubeeGo/core/src/main/java/com/saefulrdevs/mubeego/core/data/source/local/room/TmdbDatabase.kt
@Database(
    entities = [MovieEntity::class, TvShowEntity::class, SeasonEntity::class, GenreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TmdbDatabase : RoomDatabase() {
    abstract fun tmdbDao(): TmdbDao

    companion object {
        @Volatile
        private var INSTANCE: TmdbDatabase? = null
        fun getInstance(context: Context): TmdbDatabase =
            INSTANCE ?: synchronized(this) {
                val passphrase: ByteArray = SQLiteDatabase.getBytes("saefulr".toCharArray())
                val factory = SupportFactory(passphrase)
                Room.databaseBuilder(
                    context.applicationContext,
                    TmdbDatabase::class.java,
                    "Tmdb.db"
                )
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()
                    .apply {
                        INSTANCE = this
                    }
            }
    }
}
```

---

## 2. TmdbDao.kt (Room DAO)

```kotlin
// [SQLITE-MANAGEMENT] d:/Kotlin Project/MubeeGo/core/src/main/java/com/saefulrdevs/mubeego/core/data/source/local/room/TmdbDao.kt
@Dao
interface TmdbDao {
    //Movie
    @Query("SELECT * FROM movies ORDER BY voteCount DESC")
    fun getDiscoverMovie(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies where favorited = 1")
    fun getFavoriteMovie(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    fun getMovieById(movieId: String): Flow<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: List<MovieEntity>)

    @Update
    fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM movies")
    fun clearMovies()

    //Tv Show
    @Query("SELECT * FROM tvShows ORDER BY voteCount DESC")
    fun getDiscoverTvShow(): Flow<List<TvShowEntity>>

    @Query("SELECT * FROM tvShows where favorited = 1")
    fun getFavoriteTvShow(): Flow<List<TvShowEntity>>

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
```

---

## 3. LocalDataSource.kt (Abstraksi Akses Lokal)

```kotlin
// [SQLITE-MANAGEMENT] d:/Kotlin Project/MubeeGo/core/src/main/java/com/saefulrdevs/mubeego/core/data/source/local/LocalDataSource.kt
class LocalDataSource private constructor(private val tmdbDao: TmdbDao) {
    companion object {
        private var INSTANCE: LocalDataSource? = null
        fun getInstance(tmdbDao: TmdbDao): LocalDataSource =
            INSTANCE ?: LocalDataSource(tmdbDao)
    }
    fun getAllMovies(): Flow<List<MovieEntity>> = tmdbDao.getDiscoverMovie()
    fun getMovieById(movieId: String): Flow<MovieEntity> = tmdbDao.getMovieById(movieId)
    fun getFavoriteMovie(): Flow<List<MovieEntity>> = tmdbDao.getFavoriteMovie()
    fun getAllTvShow(): Flow<List<TvShowEntity>> = tmdbDao.getDiscoverTvShow()
    fun getTvShowById(showId: String): Flow<TvShowEntity> = tmdbDao.getTvShowById(showId)
    fun getFavoriteTvShow(): Flow<List<TvShowEntity>> = tmdbDao.getFavoriteTvShow()
    fun getTvShowWithSeason(showId: String): Flow<TvShowWithSeasonEntity> = tmdbDao.getSeasonByTvShowId(showId)
    fun getGenres(): Flow<List<GenreEntity>> = tmdbDao.getGenres()
    fun insertMovies(movies: List<MovieEntity>) = tmdbDao.insertMovie(movies)
    fun insertTvShow(tvShows: List<TvShowEntity>) = tmdbDao.insertTvShow(tvShows)
    fun insertSeason(seasons: List<SeasonEntity>) = tmdbDao.insertSeason(seasons)
    fun insertGenres(genres: List<GenreEntity>) = tmdbDao.insertGenres(genres)
    fun setMovieFavorite(movie: MovieEntity, newState: Boolean) {
        movie.favorited = newState
        tmdbDao.updateMovie(movie)
    }
    fun setTvShowFavorite(tvShow: TvShowEntity, newState: Boolean) {
        tvShow.favorited = newState
        tmdbDao.updateTvShow(tvShow)
    }
    fun clearMovies() = tmdbDao.clearMovies()
}
```

---

## 4. build.gradle.kts (Dependensi SQLCipher & SQLite)

```kts
// [SQLITE-MANAGEMENT] d:/Kotlin Project/MubeeGo/core/build.gradle.kts
// Dependensi SQLCipher untuk Android
implementation(libs.zetetic.android.database.sqlcipher)
// Dependensi AndroidX SQLite
implementation(libs.androidx.sqlite)
```

---

## 5. consumer-rules.pro (Proguard untuk SQLCipher)

```proguard
# [SQLITE-MANAGEMENT] d:/Kotlin Project/MubeeGo/core/consumer-rules.pro
##---------------Begin: proguard configuration for SQLCipher  ----------
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
```

---

Penanda: `[SQLITE-MANAGEMENT]` pada setiap blok kode di atas.
