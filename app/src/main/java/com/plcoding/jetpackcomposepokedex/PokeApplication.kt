package com.plcoding.jetpackcomposepokedex

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PokeApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}