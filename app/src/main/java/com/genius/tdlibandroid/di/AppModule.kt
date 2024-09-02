package com.genius.tdlibandroid.di

import android.content.Context
import android.os.Build
import com.genius.tdlibandroid.R
import com.genius.tdlibandroid.data.TelegramClient
import com.genius.tdlibandroid.data.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import org.drinkless.tdlib.TdApi
import java.util.Locale
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

    @Provides
    fun provideTdlibParameters(@ApplicationContext context: Context): TdApi.SetTdlibParameters {
        return TdApi.SetTdlibParameters().apply {
            apiId = context.resources.getInteger(R.integer.telegram_api_id)
            apiHash = context.getString(R.string.telegram_api_hash)
            useMessageDatabase = true
            useSecretChats = true
            systemLanguageCode = Locale.getDefault().language
            databaseDirectory = context.filesDir.absolutePath
            deviceModel = Build.MODEL
            systemVersion = Build.VERSION.RELEASE
            applicationVersion = "0.1"
//            enableStorageOptimizer = true
        }
    }

    @Singleton
    @Provides
    fun provideTelegramClient(parameters: TdApi.SetTdlibParameters) = TelegramClient(parameters)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    fun provideUserRepository(client: TelegramClient) = UserRepository(client)

//    @Provides
//    fun provideChatsRepository(client: TelegramClient) = ChatsRepository(client)
//
//    @Provides
//    fun provideMessagesRepository(client: TelegramClient) = MessagesRepository(client)
}
