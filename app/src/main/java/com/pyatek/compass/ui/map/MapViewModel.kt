package com.pyatek.compass.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyatek.compass.data.local.dao.CoordinatesDao
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity
import com.pyatek.compass.data.repository.CompassHistoryRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel @Inject constructor(
    coordinatesDao: CoordinatesDao
): ViewModel() {

    val repository = CompassHistoryRepository(coordinatesDao)

    val currentDestination: LiveData<HistoryCoordinatesEntity>
        get() = _currentDestination
    private val _currentDestination = MutableLiveData<HistoryCoordinatesEntity>()

    fun setupCurrentDestination(destinationId: Int) {
        viewModelScope.launch {
            repository.getCoordinatesById(destinationId).collect {
                _currentDestination.value = it
            }
        }
    }
}