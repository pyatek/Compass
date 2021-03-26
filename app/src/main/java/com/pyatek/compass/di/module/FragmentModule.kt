package com.pyatek.compass.di.module

import com.pyatek.compass.ui.compass.CompassFragment
import com.pyatek.compass.ui.map.MapFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeCompassFragment(): CompassFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment
}