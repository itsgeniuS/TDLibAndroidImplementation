package com.genius.tdlibandroid.presentation.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.core.TgBaseViewModel
import com.genius.tdlibandroid.data.TgValidationError
import com.genius.tdlibandroid.data.tgRepos.TgAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
@HiltViewModel
class LoginViewModel
@Inject constructor(
    private val tgAuthRepository: TgAuthRepository,
) : TgBaseViewModel(tgAuthRepository) {

    private val _uiState = mutableStateOf<LoginStates>(LoginStates.Loading)
    val uiState: State<LoginStates> get() = _uiState

    private val _authState = mutableStateOf("None")
    val authState: State<String> get() = _authState

    fun getAuthStatus() {
        _uiState.value = LoginStates.Loading

        viewModelScope.launch {
            tgAuthRepository.getAuthStatus().collect {
                _authState.value = it.toString()
                when (it) {
                    is TdApi.AuthorizationStateWaitPhoneNumber ->
                        _uiState.value = LoginStates.InsertNumber()

                    is TdApi.AuthorizationStateWaitCode ->
                        _uiState.value = LoginStates.InsertCode()

                    is TdApi.AuthorizationStateWaitPassword ->
                        _uiState.value = LoginStates.InsertPassword()

                    is TdApi.AuthorizationStateReady ->
                        _uiState.value = LoginStates.Authenticated

                    is TdApi.AuthorizationStateWaitTdlibParameters -> {}
                    is TdApi.AuthorizationStateLoggingOut -> {}
                    else -> Unit
                }
            }
        }
    }

    fun insertPhoneNumber(number: String) {
        viewModelScope.launch {
            tgAuthRepository.sendCodeForMobileNumber(number).collect {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> getAuthStatus()
                    TdApi.Error.CONSTRUCTOR -> showError(it as TdApi.Error)
                }
            }
        }
    }

    private fun showError(error: TdApi.Error) {
        Log.e("ShowError", "Error code --> ${error.code}")
        Log.e("ShowError", "Error message --> ${error.message}")

        when (error.message) {
            TgValidationError.PhoneNumberError.errorStr -> {}
            TgValidationError.PasswordError.errorStr -> {}
        }
    }

    fun insertCode(code: String) {
        _uiState.value = LoginStates.Loading
        viewModelScope.launch {
            tgAuthRepository.validateCode(code).collect {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> getAuthStatus()
                    TdApi.Error.CONSTRUCTOR -> showError(it as TdApi.Error)
                }
            }
        }
    }

    fun insertPassword(password: String) {
        _uiState.value = LoginStates.Loading
        viewModelScope.launch {
            tgAuthRepository.validate2FA(password).collect {
                when (it.constructor) {
                    TdApi.Ok.CONSTRUCTOR -> getAuthStatus()
                    TdApi.Error.CONSTRUCTOR -> showError(it as TdApi.Error)
                }
            }
        }
    }
}
