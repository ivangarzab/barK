package com.ivangarzab.bark.sample.android

import android.app.Application
import com.ivangarzab.bark.Bark
import com.ivangarzab.bark.Level
import com.ivangarzab.bark.trainers.AndroidLogTrainer

/**
 * The purpose of this class is to serve as the main [Application] entry point of this
 * sample project, while staying open for the extension on different environments.
 */
open class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startLogger()
    }

    open fun startLogger() {
        Bark.train(AndroidLogTrainer(
            volume = if (BuildConfig.DEBUG) Level.DEBUG else Level.WARNING
        ))
        Bark.v("barK logger has started")
    }
}