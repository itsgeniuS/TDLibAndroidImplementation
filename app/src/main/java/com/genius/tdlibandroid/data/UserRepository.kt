package com.genius.tdlibandroid.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepository @Inject constructor(private val client: TelegramClient) {

    fun getUser(userId: Int): Flow<TdApi.User> = callbackFlow {
        client.client.send(TdApi.GetUser(userId.toLong())) {
            trySend(it as TdApi.User).isSuccess
        }
        awaitClose { }
    }
}