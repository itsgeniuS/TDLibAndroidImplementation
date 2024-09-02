package com.genius.tdlibandroid

import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

/**
 * @Author: Thulasirajan P
 * @github: https:github.com/itsgeniuS
 * @Date: 29/08/24
 */
class MyApp : MultiDexApplication() {

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
    }
}
