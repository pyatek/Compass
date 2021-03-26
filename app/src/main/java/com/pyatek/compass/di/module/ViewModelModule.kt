package com.pyatek.compass.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pyatek.compass.factory.ViewModelFactory
import com.pyatek.compass.ui.compass.CompassViewModel
import com.pyatek.compass.ui.main.MainViewModel
import com.pyatek.compass.ui.map.MapViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CompassViewModel::class)
    protected abstract fun compassViewModel(compassViewModel: CompassViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    protected abstract fun mainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    protected abstract fun mapViewModel(mapViewModel: MapViewModel): ViewModel
}