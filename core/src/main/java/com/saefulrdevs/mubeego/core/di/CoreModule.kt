package com.saefulrdevs.mubeego.core.di

import android.util.Base64
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.saefulrdevs.mubeego.core.BuildConfig
import com.saefulrdevs.mubeego.core.data.AuthRepository
import com.saefulrdevs.mubeego.core.data.TmdbRepository
import com.saefulrdevs.mubeego.core.data.UserPreferencesRepository
import com.saefulrdevs.mubeego.core.data.source.local.LocalDataSource
import com.saefulrdevs.mubeego.core.data.source.local.room.TmdbDatabase
import com.saefulrdevs.mubeego.core.data.source.remote.RemoteDataSource
import com.saefulrdevs.mubeego.core.data.source.remote.network.ApiService
import com.saefulrdevs.mubeego.core.domain.repository.IAuthRepository
import com.saefulrdevs.mubeego.core.domain.repository.ITmdbRepository
import com.saefulrdevs.mubeego.core.domain.repository.IUserPreferencesRepository
import com.saefulrdevs.mubeego.core.util.AppExecutors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

val databaseModule = module {
    single { TmdbDatabase.getInstance(androidContext()) }
    factory { get<TmdbDatabase>().tmdbDao() }
}

val networkModule = module {
    single {
        val hostname = "api.themoviedb.org"

        val certificatePinner = runBlocking {
            CertificatePinner.Builder().apply {
                getCertificatePins(hostname).forEach { pin ->
                    add(hostname, pin)
                }
            }.build()
        }

        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource.getInstance(get()) }
    single { RemoteDataSource(get()) }
    factory { AppExecutors() }
    single<ITmdbRepository> {
        TmdbRepository.getInstance(
            get(),
            get(),
            get()
        )
    }
    single<FirebaseAuth> {
        FirebaseApp.initializeApp(androidContext()) ?: throw IllegalStateException("FirebaseApp initialization failed")
        FirebaseAuth.getInstance()
    }
    single<IAuthRepository> { AuthRepository(get()) }
    single<IUserPreferencesRepository> { UserPreferencesRepository(androidContext()) }
}

suspend fun getCertificatePins(hostname: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://$hostname")
            val connection = url.openConnection() as HttpsURLConnection
            connection.connect()

            val certs = connection.serverCertificates
            val hashList = mutableListOf<String>()

            for (cert in certs) {
                if (cert is X509Certificate) {
                    val publicKey = cert.publicKey.encoded
                    val sha256 = MessageDigest.getInstance("SHA-256").digest(publicKey)
                    val pin = "sha256/${Base64.encodeToString(sha256, Base64.NO_WRAP)}"
                    hashList.add(pin)
                }
            }
            hashList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
