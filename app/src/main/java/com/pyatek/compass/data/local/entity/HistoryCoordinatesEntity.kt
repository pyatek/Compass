package com.pyatek.compass.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "history_coordinates")
data class HistoryCoordinatesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "startLat") val startLat: Double,
    @ColumnInfo(name = "startLon") val startLon: Double,
    @ColumnInfo(name = "endLat") val endLat: Double,
    @ColumnInfo(name = "endLon") val endLon: Double,
    @ColumnInfo(name = "calculatedDistance") val distance: Double,
    @ColumnInfo(name = "destinationBearing") val bearing: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long = Date().time,
)