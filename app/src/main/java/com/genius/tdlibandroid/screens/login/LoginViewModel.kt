package com.genius.tdlibandroid.screens.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.tdlibandroid.data.UserRepository
import com.genius.tdlibandroid.data.tgClient.TgClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
@HiltViewModel
class LoginViewModel @OptIn(ExperimentalCoroutinesApi::class)
@Inject constructor(
//    private val client: TelegramClient,
    private val tgClient: TgClient,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf<LoginStates>(LoginStates.Loading)
    val uiState: State<LoginStates> get() = _uiState

    fun getCC() {
        viewModelScope.launch {

            tgClient.sendAsFlow(TdApi.GetCountries()).collect {
                when (it.constructor) {
                    TdApi.Error.CONSTRUCTOR -> {

                    }

                    else -> {
                        Log.e("LoginVM", "getCountryCodes success... $it ")
                    }
                }
            }


            tgClient.getCountryCodes(
                onSuccess = {
                    Log.e("LoginVM", "getCountryCodes success... $it ")
                },
                onFailure = {
                    Log.e("LoginVM", "getCountryCodes failed... $it ")
                }
            )
        }
    }

    fun insertPhoneNumber(number: String) {
        _uiState.value = LoginStates.Loading
//        client.insertPhoneNumber(number)
    }

    fun insertCode(code: String) {
        _uiState.value = LoginStates.Loading
//        client.insertCode(code)
    }

    fun insertPassword(password: String) {
        _uiState.value = LoginStates.Loading
//        client.insertPassword(password)
    }
}
