package com.genius.tdlibandroid.core

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.data.tgRepos.TgAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

@HiltViewModel
open class TgBaseViewModel @Inject constructor(
    private val tgAuthRepository: TgAuthRepository,
) : ViewModel() {

    private val _userAuthState = mutableStateOf<TgUserAuthState>(TgUserAuthState.Init)
    val userAuthState: State<TgUserAuthState> get() = _userAuthState

    fun getUserAuthStatus() {
        _userAuthState.value = TgUserAuthState.Loading

        viewModelScope.launch {
            tgAuthRepository.getAuthStatus().collect {
                Log.e("AuthState --> ", it.toString())
                when (it) {
                    is TdApi.AuthorizationStateWaitPhoneNumber ->
                        _userAuthState.value = TgUserAuthState.InsertNumber

                    is TdApi.AuthorizationStateWaitCode ->
                        _userAuthState.value = TgUserAuthState.InsertCode

                    is TdApi.AuthorizationStateWaitPassword ->
                        _userAuthState.value = TgUserAuthState.InsertPassword

                    is TdApi.AuthorizationStateReady ->
                        _userAuthState.value = TgUserAuthState.Authenticated

                    is TdApi.AuthorizationStateLoggingOut ->
                        _userAuthState.value = TgUserAuthState.LoggedOut

                    is TdApi.AuthorizationStateClosed ->
                        _userAuthState.value = TgUserAuthState.Closed

                    else -> _userAuthState.value = TgUserAuthState.Unknown
                }
            }
        }
    }
}