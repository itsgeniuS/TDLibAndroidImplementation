package com.genius.tdlibandroid.data.tgRepos

import com.genius.tdlibandroid.data.TgCore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

class TgAuthRepository @Inject constructor(
    private val tgCore: TgCore,
) {
    fun getAuthStatus(): Flow<TdApi.AuthorizationState> = callbackFlow {
        tgCore.client?.send(TdApi.GetAuthorizationState()) {
            when (it.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    error("")
                }

                else -> trySend(it as TdApi.AuthorizationState)
            }
        }
        awaitClose {}
    }

    fun sendCodeForMobileNumber(phoneNumberWithCC: String): Flow<TdApi.Object> = callbackFlow {
        tgCore.client?.send(TdApi.SetAuthenticationPhoneNumber(phoneNumberWithCC, null)) {
            trySend(it)
        }
        awaitClose {}
    }

    fun validateCode(code: String) = callbackFlow {
        tgCore.client?.send(TdApi.CheckAuthenticationCode(code)) {
            trySend(it)
        }
        awaitClose {}
    }

    fun validate2FA(password: String) = callbackFlow {
        tgCore.client?.send(TdApi.CheckAuthenticationPassword(password)) {
            trySend(it)
        }
        awaitClose {}
    }
}