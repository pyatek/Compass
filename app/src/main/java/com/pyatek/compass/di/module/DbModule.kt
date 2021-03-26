package com.pyatek.compass.di.module

import android.app.Application
import androidx.room.Room
import com.pyatek.compass.data.local.AppDatabase
import com.pyatek.compass.data.local.dao.CoordinatesDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Provides
    @Singleton
    internal fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, "Compass_history.db"
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    internal fun provideCoordinatesDao(appDatabase: AppDatabase): CoordinatesDao {
        return appDatabase.coordinatesDao()
    }
}