package com.pyatek.compass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pyatek.compass.data.local.dao.CoordinatesDao
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity

@Database(entities = [HistoryCoordinatesEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun coordinatesDao(): CoordinatesDao
}