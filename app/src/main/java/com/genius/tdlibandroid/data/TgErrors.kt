package com.genius.tdlibandroid.data

sealed class TgValidationError(val errorStr: String) {
    data object PasswordError : TgValidationError("PASSWORD_HASH_INVALID")
    data object PhoneNumberError : TgValidationError("PHONE_NUMBER_INVALID")
}

