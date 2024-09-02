package com.genius.tdlibandroid.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

/*
 * Go to https://my.telegram.org to obtain api id (integer) and api hash (string).
 * Put those in values (for example in values/api_keys.xml):
 * <resources>
 *   <integer name="telegram_api_id">your integer api id</integer>
 *   <string name="telegram_api_hash">your string api hash</string>
 * </resources>
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TelegramClient @Inject constructor(
    private val tdLibParameters: TdApi.SetTdlibParameters
) : Client.ResultHandler {

    private val TAG = TelegramClient::class.java.simpleName

    val client = Client.create(
        this,
        null,
        null,
    )

    private val _authState = MutableStateFlow(Authentication.UNKNOWN)
    val authState: StateFlow<Authentication> get() = _authState

    init {
        client.send(TdApi.SetLogVerbosityLevel(1), this)
        //client.send(TdApi.GetCountries(), this)
        client.send(
            TdApi.SetTdlibParameters().apply {
                useTestDc = tdLibParameters.useTestDc
                databaseDirectory = tdLibParameters.databaseDirectory
                filesDirectory = tdLibParameters.filesDirectory
                databaseEncryptionKey = tdLibParameters.databaseEncryptionKey
                useFileDatabase = tdLibParameters.useFileDatabase
                useChatInfoDatabase = tdLibParameters.useChatInfoDatabase
                useMessageDatabase = tdLibParameters.useMessageDatabase
                useSecretChats = tdLibParameters.useSecretChats
                apiId = tdLibParameters.apiId
                apiHash = tdLibParameters.apiHash
                systemLanguageCode = tdLibParameters.systemLanguageCode
                deviceModel = tdLibParameters.deviceModel
                systemVersion = tdLibParameters.systemVersion
                applicationVersion = tdLibParameters.applicationVersion
            },
            this,
        )
        client.send(TdApi.GetAuthorizationState(), this)
    }

    fun close() {}

    private val requestScope = CoroutineScope(Dispatchers.IO)

    private fun setAuth(auth: Authentication) {
        _authState.value = auth
    }

    override fun onResult(data: TdApi.Object) {
        Log.d(TAG, "onResult: ${data::class.java.simpleName}")
        when (data.constructor) {
            TdApi.UpdateAuthorizationState.CONSTRUCTOR -> {
                Log.d(TAG, "UpdateAuthorizationState")
                onAuthorizationStateUpdated((data as TdApi.UpdateAuthorizationState).authorizationState)
            }

            TdApi.UpdateOption.CONSTRUCTOR -> {

            }

            else -> Log.d(TAG, "Unhandled onResult call with data: $data.")
        }
    }

    private fun doAsync(job: () -> Unit) {
        requestScope.launch { job() }
    }

    fun startAuthentication() {
        Log.d(TAG, "startAuthentication called")
        if (_authState.value != Authentication.UNAUTHENTICATED) {
            throw IllegalStateException("Start authentication called but client already authenticated. State: ${_authState.value}.")
        }

        doAsync {
            client.send(tdLibParameters) {
                Log.d(TAG, "SetTdlibParameters result: $it")
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> {
                        //result.postValue(true)
                    }

                    TdApi.Error.CONSTRUCTOR -> {
                        //result.postValue(false)
                    }
                }
            }
        }
    }

    fun insertPhoneNumber(phoneNumber: String) {
        Log.d("TelegramClient", "phoneNumber: $phoneNumber")

        val phoneNumberSettings = TdApi.PhoneNumberAuthenticationSettings().apply {
            allowFlashCall = false
            allowMissedCall = false
            isCurrentPhoneNumber = true
            hasUnknownPhoneNumber = false
            allowSmsRetrieverApi = false
        }
        Log.d("TelegramClient", "phoneNumberSettings: $phoneNumberSettings")

        client.send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, phoneNumberSettings)) {
            when (it.constructor) {
                TdApi.Ok.CONSTRUCTOR -> {
                    Log.d("TelegramClient", "OkCons: $it")
                }

                TdApi.Error.CONSTRUCTOR -> {
                    Log.d("TelegramClient", "ErrorCons: $it")
                }

                else -> {
                    Log.d("TelegramClient", "UnDefined: $it")
                }
            }
        }
    }


    fun insertCode(code: String) {
        Log.d("TelegramClient", "code: $code")
        doAsync {
            client.send(TdApi.CheckAuthenticationCode(code)) {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> {

                    }

                    TdApi.Error.CONSTRUCTOR -> {

                    }
                }
            }
        }
    }

    fun insertPassword(password: String) {
        Log.d("TelegramClient", "inserting password")
        doAsync {
            client.send(TdApi.CheckAuthenticationPassword(password)) {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> {

                    }

                    TdApi.Error.CONSTRUCTOR -> {

                    }
                }
            }
        }
    }

    private fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState) {
        when (authorizationState.constructor) {
            TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> {
                Log.d(
                    TAG,
                    "onResult: AuthorizationStateWaitTdlibParameters -> state = UNAUTHENTICATED"
                )
                setAuth(Authentication.UNAUTHENTICATED)
            }/* TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR -> {
                 Log.d(TAG, "onResult: AuthorizationStateWaitEncryptionKey")
                 client.send(TdApi.CheckDatabaseEncryptionKey()) {
                     when (it.constructor) {
                         TdApi.Ok.CONSTRUCTOR -> {
                             Log.d(TAG, "CheckDatabaseEncryptionKey: OK")
                         }
                         TdApi.Error.CONSTRUCTOR -> {
                             Log.d(TAG, "CheckDatabaseEncryptionKey: Error")
                         }
                     }
                 }
             }*/
            TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> {
                Log.d(
                    TAG, "onResult: AuthorizationStateWaitPhoneNumber -> state = WAIT_FOR_NUMBER"
                )
                setAuth(Authentication.WAIT_FOR_NUMBER)
            }

            TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitCode -> state = WAIT_FOR_CODE")
                setAuth(Authentication.WAIT_FOR_CODE)
            }

            TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateWaitPassword")
                setAuth(Authentication.WAIT_FOR_PASSWORD)
            }

            TdApi.AuthorizationStateReady.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateReady -> state = AUTHENTICATED")
                setAuth(Authentication.AUTHENTICATED)
            }

            TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateLoggingOut")
                setAuth(Authentication.UNAUTHENTICATED)
            }

            TdApi.AuthorizationStateClosing.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateClosing")
            }

            TdApi.AuthorizationStateClosed.CONSTRUCTOR -> {
                Log.d(TAG, "onResult: AuthorizationStateClosed")
            }

            else -> Log.d(TAG, "Unhandled authorizationState with data: $authorizationState.")
        }
    }

    fun downloadableFile(file: TdApi.File): Flow<String?> = file.takeIf {
        it.local?.isDownloadingCompleted == false
    }?.id?.let { fileId ->
        downloadFile(fileId).map { file.local?.path }
    } ?: flowOf(file.local?.path)

    fun downloadFile(fileId: Int): Flow<Unit> = callbackFlow {
        client.send(TdApi.DownloadFile(fileId, 1, 0, 0, true)) {
            when (it.constructor) {
                TdApi.Ok.CONSTRUCTOR -> {
                    trySend(Unit).isSuccess
                }

                else -> {
                    cancel("", Exception(""))

                }
            }
        }
        awaitClose()
    }

    fun sendAsFlow(query: TdApi.Function<*>): Flow<TdApi.Object> = callbackFlow {
        client.send(query) {
            when (it.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    error("")
                }

                else -> {
                    trySend(it).isSuccess
                }
            }
            //close()
        }
        awaitClose { }
    }

    inline fun <reified T : TdApi.Object> send(query: TdApi.Function<*>): Flow<T> =
        sendAsFlow(query).map { it as T }

}
