package com.genius.tdlibandroid.data.tgRepos

import com.genius.tdlibandroid.data.TgCore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TgUserRepository @Inject constructor(
    private val tgCore: TgCore,
) {
    fun getUser(userId: Int): Flow<TdApi.User> = callbackFlow {
        tgCore.client?.send(TdApi.GetUser(userId.toLong())) {
            trySend(it as TdApi.User).isSuccess
        }
        awaitClose { }
    }
}
