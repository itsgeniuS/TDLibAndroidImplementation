package com.genius.tdlibandroid.core

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
sealed interface TgUserAuthState {
    data object Init : TgUserAuthState
    data object Loading : TgUserAuthState
    data object InsertNumber : TgUserAuthState
    data object InsertCode : TgUserAuthState
    data object InsertPassword : TgUserAuthState
    data object Authenticated : TgUserAuthState
    data object LoggedOut : TgUserAuthState
    data object Closed : TgUserAuthState
    data object Unknown : TgUserAuthState
}