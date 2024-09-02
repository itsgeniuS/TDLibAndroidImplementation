package com.genius.tdlibandroid.screens

import androidx.navigation.NavBackStackEntry

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Home : Screen("home")

    data object Login : Screen("login")

    data object Chat : Screen("chat/{chatId}") {
        fun buildRoute(chatId: Long): String = "chat/${chatId}"
        fun getChatId(entry: NavBackStackEntry): Long =
            entry.arguments!!.getString("chatId")?.toLong()
                ?: throw IllegalArgumentException("chatId argument missing.")
    }
}
