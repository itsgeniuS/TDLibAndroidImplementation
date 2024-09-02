package com.genius.tdlibandroid.screens.login

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
sealed class LoginStates {
    data object Loading : LoginStates()
    data class InsertNumber(val previousError: Throwable? = null) : LoginStates()
    data class InsertCode(val previousError: Throwable? = null) : LoginStates()
    data class InsertPassword(val previousError: Throwable? = null) : LoginStates()
    data object Authenticated : LoginStates()
}