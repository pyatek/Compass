package com.pyatek.compass.data.repository

import com.pyatek.compass.data.local.dao.CoordinatesDao
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class CompassHistoryRepository(private val coordinatesDao: CoordinatesDao) {

    fun insertCoordinates(lat: Double, lon: Double, destinationLat: Double, destinationLon: Double,
        distance: Double, bearing: Int): Int {
        return coordinatesDao.insertCoordinates(HistoryCoordinatesEntity(
            startLat = lat,
            startLon = lon,
            endLat = destinationLat,
            endLon = destinationLon,
            distance = distance,
            bearing = bearing
        )).toInt()
    }

    fun getCoordinatesList(): Flow<List<HistoryCoordinatesEntity>> {
        return coordinatesDao.getHistoricCoordinateList()
    }

    fun getCoordinatesById(id: Int): Flow<HistoryCoordinatesEntity> {
        return coordinatesDao.getHistoricCoordinates(id)
    }
}