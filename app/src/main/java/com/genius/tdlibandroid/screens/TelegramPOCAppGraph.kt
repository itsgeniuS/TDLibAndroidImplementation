package com.genius.tdlibandroid.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.genius.tdlibandroid.MainActivity
import com.genius.tdlibandroid.screens.home.HomeScreen
import com.genius.tdlibandroid.screens.home.HomeViewModel
import com.genius.tdlibandroid.screens.login.LoginScreen
import com.genius.tdlibandroid.screens.login.LoginViewModel
import com.genius.tdlibandroid.screens.splash.SplashScreen
import com.genius.tdlibandroid.screens.splash.SplashViewModel
import com.genius.tdlibandroid.ui.theme.TDLibAndroidTheme

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 02/09/24
 */
@Composable
fun TelegramPOCAppGraph(activity: MainActivity) {
    TDLibAndroidTheme {
        activity.window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
        val navController = rememberNavController()
        AppNavHost(navController)
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                viewModel = hiltViewModel<SplashViewModel>(),
                onOpenHome = {
                    navController.navigate(Screen.Home.route)
                },
                onOpenLogin = {
                    navController.navigate(Screen.Login.route)
                },
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = hiltViewModel<LoginViewModel>(),
                onOpenHome = {
                    navController.navigate(Screen.Home.route)
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(hiltViewModel<HomeViewModel>())
        }
    }
}
