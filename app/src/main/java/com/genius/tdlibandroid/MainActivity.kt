package com.genius.tdlibandroid

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.genius.tdlibandroid.ui.theme.TDLibAndroidTheme
import org.drinkless.tdlib.TdApi
import java.util.Locale

class MainActivity : ComponentActivity() {

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TDLibAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        kotlin.runCatching {
                            val telegramClient = TelegramClient(
                                tdLibParameters = TdApi.SetTdlibParameters().apply {
                                    apiId = context.resources.getInteger(R.integer.telegram_api_id)
                                    apiHash = context.getString(R.string.telegram_api_hash)
                                    useMessageDatabase = true
                                    useSecretChats = true
                                    systemLanguageCode = Locale.getDefault().language
                                    databaseDirectory = context.filesDir.absolutePath
                                    deviceModel = Build.MODEL
                                    systemVersion = Build.VERSION.RELEASE
                                    applicationVersion = "0.1"
                                }
                            )

                            val authStateValue = telegramClient.authState.value
                            Log.e("MainActivity", "authStateValue $authStateValue®®")

                        }.onFailure {
                            Log.e(
                                "MainActivity",
                                "telegram client creation failed with exception : $it"
                            )
                        }.onSuccess {
                            Log.e("MainActivity", "telegram client created...")
                        }

                        LoginScreen()
                    }
                }
            }
        }
    }
}
