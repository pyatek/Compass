package com.pyatek.compass.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.pyatek.compass.di.component.AppComponent
import com.pyatek.compass.di.component.DaggerAppComponent
import com.pyatek.compass.di.module.DbModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App: Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    init {
        instance = this
    }

    companion object {
        lateinit var instance: App

        fun appContext(): Context {
            return instance.applicationContext

        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        DaggerAppComponent.builder()
            .application(this)
            .dbModule(DbModule())
            .build()
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}