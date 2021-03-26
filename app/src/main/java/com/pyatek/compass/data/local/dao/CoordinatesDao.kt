package com.pyatek.compass.data.local.dao

import androidx.room.*
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoordinates(coordinates: HistoryCoordinatesEntity): Long

    @Query("SELECT * FROM `history_coordinates`")
    fun getHistoricCoordinateList(): Flow<List<HistoryCoordinatesEntity>>

    @Query("SELECT * FROM `history_coordinates` WHERE id = :id")
    fun getHistoricCoordinates(id: Int): Flow<HistoryCoordinatesEntity>

    @Query("DELETE FROM `history_coordinates` WHERE id = :id")
    fun deleteCoordinateFromHistory(id: Int)
}