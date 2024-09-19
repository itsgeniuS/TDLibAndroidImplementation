package com.genius.tdlibandroid.di

import android.content.Context
import com.genius.tdlibandroid.data.TgCore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    @Named("ioDispatcher")
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    @Named("applicationScope")
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideTgClient(@ApplicationContext context: Context) = TgCore(context)
}
