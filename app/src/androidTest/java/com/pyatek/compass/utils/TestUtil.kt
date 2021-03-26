package com.pyatek.compass.utils

import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity

object TestUtil {

    fun createDbCoordinates(): HistoryCoordinatesEntity {
        return HistoryCoordinatesEntity(
            startLon = 20.0,
            startLat = 20.0,
            endLon = 0.0,
            endLat = 0.0,
            bearing = 0,
            distance = 0.0
        )
    }
}
