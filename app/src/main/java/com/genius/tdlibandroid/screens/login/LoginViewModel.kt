package com.genius.tdlibandroid.screens.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.genius.tdlibandroid.data.TelegramClient
import com.genius.tdlibandroid.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
@HiltViewModel
class LoginViewModel @OptIn(ExperimentalCoroutinesApi::class)
@Inject constructor(
    private val client: TelegramClient,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf<LoginStates>(LoginStates.Loading)
    val uiState: State<LoginStates> get() = _uiState


    fun insertPhoneNumber(number: String) {
        _uiState.value = LoginStates.Loading
        client.insertPhoneNumber(number)
    }

    fun insertCode(code: String) {
        _uiState.value = LoginStates.Loading
        client.insertCode(code)
    }

    fun insertPassword(password: String) {
        _uiState.value = LoginStates.Loading
        client.insertPassword(password)
    }
}
