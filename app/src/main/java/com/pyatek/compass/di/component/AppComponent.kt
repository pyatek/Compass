package com.pyatek.compass.di.component

import android.app.Application
import com.pyatek.compass.app.App
import com.pyatek.compass.di.module.ActivityModule
import com.pyatek.compass.di.module.DbModule
import com.pyatek.compass.di.module.FragmentModule
import com.pyatek.compass.di.module.ViewModelModule
import com.pyatek.compass.ui.main.MainActivity
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityModule::class,
    FragmentModule::class,
    ViewModelModule::class,
    DbModule::class
])
interface AppComponent: AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun dbModule(dbModule: DbModule): Builder

        fun build(): AppComponent
    }

    override fun inject(app: App)
    fun inject(mainActivity: MainActivity)
}