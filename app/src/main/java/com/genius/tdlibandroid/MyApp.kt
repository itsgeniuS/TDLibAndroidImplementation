package com.genius.tdlibandroid

import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.genius.tdlibandroid.data.tgClient.TgClient
import dagger.hilt.android.HiltAndroidApp
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import javax.inject.Inject

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 29/08/24
 */
@HiltAndroidApp
class MyApp : MultiDexApplication(), Client.ResultHandler, Client.ExceptionHandler {

    @Inject lateinit var client: TgClient

    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)

        runCatching {
            System.loadLibrary("tdjni")
        }.onSuccess {
            Log.e("Application", "tdlibjson loaded successfully!")
        }.onFailure {
            Log.e("Application", "tdlibjson load error! : ${it.message}")
        }

        client.initializeClient()
    }

    override fun onResult(`object`: TdApi.Object?) {

    }

    override fun onException(e: Throwable?) {

    }
}
