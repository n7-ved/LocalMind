package com.n7.localmind

import android.app.Application
import com.n7.localmind.di.AppDI

class LocalMindApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDI.create(this)
    }
}