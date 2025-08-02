package com.ivangarzab.bark.sample.android

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startLogger()
    }

    private fun startLogger() {

    }
}