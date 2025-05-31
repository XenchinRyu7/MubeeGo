package com.saefulrdevs.mubeego.core.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saefulrdevs.mubeego.core.data.source.local.entity.GenreEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.MovieEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.SeasonEntity
import com.saefulrdevs.mubeego.core.data.source.local.entity.TvShowEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

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
                // Nonaktifkan SQLCipher sementara untuk App Inspection
                // val passphrase: ByteArray = SQLiteDatabase.getBytes("saefulr".toCharArray())
                // val factory = SupportFactory(passphrase)
                Room.databaseBuilder(
                    context.applicationContext,
                    TmdbDatabase::class.java,
                    "Tmdb.db"
                )
                    .fallbackToDestructiveMigration()
                    // .openHelperFactory(factory) // SQLCipher dinonaktifkan sementara
                    .build()
                    .apply {
                        INSTANCE = this
                    }
            }
    }
}