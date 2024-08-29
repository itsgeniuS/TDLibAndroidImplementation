package com.genius.tdlibandroid

import android.app.Application
import android.util.Log


/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 29/08/24
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        runCatching {
            System.loadLibrary("tdjni")
        }.onSuccess {
            Log.e("Application", "tdlibjson loaded successfully!")
        }.onFailure {
            Log.e("Application", "tdlibjson load error! : ${it.message}")
        }
    }
}
