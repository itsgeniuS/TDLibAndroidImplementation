package com.genius.tdlibandroid.data.tgClient

import android.content.Context
import android.os.Build
import android.util.Log
import com.genius.tdlibandroid.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 03/09/24
 */
@Singleton
class TgClient @Inject constructor(
    val context: Context,
) : Client.ResultHandler, Client.ExceptionHandler {

    private var client: Client? = null

    fun initializeClient() {
        kotlin.runCatching {
            client = Client.create(
                this,
                this,
                this,
            ).apply {
                send(TdApi.SetLogVerbosityLevel(1), this@TgClient)
                send(TdApi.SetTdlibParameters().apply {
                    apiId = context.resources.getInteger(R.integer.telegram_api_id)
                    apiHash = context.getString(R.string.telegram_api_hash)
                    useMessageDatabase = true
                    useSecretChats = true
                    systemLanguageCode = Locale.getDefault().language
                    databaseDirectory = context.filesDir.absolutePath
                    deviceModel = Build.MODEL
                    systemVersion = Build.VERSION.RELEASE
                    applicationVersion = "0.1"
                }, this@TgClient)
            }
        }.onFailure {
            Log.e("TgClient", "Exception while TgClient initializing")
            Log.e("TgClient", "Exception $it")
        }
    }

    override fun onResult(`object`: TdApi.Object?) {

        when (`object`) {
            is TdApi.CountryInfo -> {
                Log.e("TgClient line 58", "CountryInfo called $`object`")
            }

            is TdApi.Countries -> {
                Log.e("TgClient line 61", "Countries called $`object`")
            }

            is TdApi.GetCountryCode -> {
                Log.e("TgClient line 64", "GetCountryCode called $`object`")
            }

            else -> {
                Log.e("TgClient line 67", "onResult called $`object`")
            }
        }
    }

    override fun onException(e: Throwable?) {
        Log.e("TgClient line 59", "onException called ${e?.message}")
    }

    fun sendAsFlow(query: TdApi.Function<*>): Flow<TdApi.Object> = callbackFlow {
        client?.send(query) {
//            when (it.constructor) {
//                TdApi.Error.CONSTRUCTOR -> {
//                    error("")
//                }
//
//                else -> {
//                    trySend(it)
//                }
//            }
            trySend(it)
        }
        awaitClose { }
    }

    fun getCountryCodes(
        onSuccess: (TdApi.Countries) -> Unit,
        onFailure: (Throwable?) -> Unit,
    ) {
        runCatching {
            client?.send(TdApi.GetCountries()) {
                onSuccess.invoke(it as TdApi.Countries)
            }
        }.onFailure {
            onFailure.invoke(it)
        }

        client?.send(TdApi.GetCountryFlagEmoji("ZM")) {
            val response = it as TdApi.Text
            Log.e("TgClient", "Country code emoji $response")
        }
    }

    fun getUser(userId: Int): Flow<TdApi.User> = callbackFlow {
        client?.send(TdApi.GetUser(userId.toLong())) {
            trySend(it as TdApi.User).isSuccess
        }
        awaitClose { }
    }
}
