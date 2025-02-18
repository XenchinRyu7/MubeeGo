package com.saefulrdevs.mubeego

import android.app.Application
import com.saefulrdevs.mubeego.core.di.databaseModule
import com.saefulrdevs.mubeego.core.di.networkModule
import com.saefulrdevs.mubeego.core.di.repositoryModule
import com.saefulrdevs.mubeego.di.useCaseModule
import com.saefulrdevs.mubeego.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

//@HiltAndroidApp
class MyApplication: Application()
{
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}