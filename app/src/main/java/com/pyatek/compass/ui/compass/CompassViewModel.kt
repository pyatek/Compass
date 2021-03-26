package com.pyatek.compass.ui.compass

import android.hardware.SensorManager
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyatek.compass.R
import com.pyatek.compass.data.local.dao.CoordinatesDao
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity
import com.pyatek.compass.data.repository.CompassHistoryRepository
import com.pyatek.compass.helpers.ApplicationConstants.EARTH_RADIUS
import com.pyatek.compass.helpers.GeoCalculationHelper
import com.pyatek.compass.helpers.Res
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

class CompassViewModel @Inject constructor(
    coordinatesDao: CoordinatesDao
): ViewModel() {
    private val repository = CompassHistoryRepository(coordinatesDao)
    private var gravityData = FloatArray(3)
    private var geomagneticData = FloatArray(3)
    private var hasGravityData = false
    private var hasGeomagneticData = false
    private var currentCoordinatesId: Int? = null
    private var myLat = 0.0
    private var myLon = 0.0
    private var hasUserLocation = false
    private var hasDestinationLocation = false

    val rotationDegreeInt = ObservableField(0)
    val destinationBearing = ObservableField(0)
    val destinationDistance = ObservableField(0.0)
    val destinationDetails = ObservableField("")

    var destinationLat = 0.0
    var destinationLon = 0.0
    var currentCompassAccuracy = -1

    val isDestinationVisible: LiveData<Boolean>
        get() = _isDestinationVisible
    private val _isDestinationVisible = MutableLiveData<Boolean>()

    val coordinatesList: LiveData<List<HistoryCoordinatesEntity>>
        get() = _coordinatesList
    private val _coordinatesList= MutableLiveData<List<HistoryCoordinatesEntity>>()

    fun getCoordinatesListData() {
        viewModelScope.launch {
            repository.getCoordinatesList().collect{
                _coordinatesList.value = it
            }
        }
    }

    fun getCurrentCoordinates(): Int {
        currentCoordinatesId?.let {
            return it
        }

        return -1
    }

    fun setDestination(latitude: Double, longitude: Double) {
        destinationLat = latitude
        destinationLon = longitude
        hasDestinationLocation = true
        handleDestinationCalculation()
    }

    fun setCurrentLocation(lat: Double, lon: Double) {
        myLat = lat
        myLon = lon
        hasUserLocation = true
        handleDestinationCalculation()
    }

    fun setGravityData(gravityArray: FloatArray) {
        System.arraycopy(gravityArray, 0, gravityData, 0, 3)
        hasGravityData = true
        checkSensorData()
    }

    fun setGeomagneticData(geomagneticArray: FloatArray) {
        System.arraycopy(geomagneticArray, 0, geomagneticData, 0, 3)
        hasGeomagneticData = true
        checkSensorData()
    }

    fun calculateCurrentBearingFromSensors(rotationMatrix: FloatArray): Int {
        val orientationMatrix = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationMatrix)
        val rotationInRadians = orientationMatrix[0]
        val rotationInDegrees = Math.toDegrees(rotationInRadians.toDouble())

        return rotationInDegrees.toInt()
    }

    fun saveCoordinatesHistory(): Boolean {
        destinationDistance.get()?.let { destinationDistance ->
            destinationBearing.get()?.let { destinationBearing ->
                val destinationId = repository.insertCoordinates(
                    myLat,
                    myLon,
                    destinationLat,
                    destinationLon,
                    destinationDistance,
                    destinationBearing
                )
                setupCurrentDestination(destinationId)
                return true
            }
        }

        return false
    }

    private fun setupCurrentDestination(id: Int) {
        currentCoordinatesId = id
        destinationDistance.get()?.let { distance ->
            destinationBearing.get()?.let { bearing ->
                _isDestinationVisible.value = true
                destinationDetails.set(
                    "${Res.string(R.string.current_destination_details)} ${String.format(Res.string(R.string.destination_meters), distance)}, ${String.format(Res.string(R.string.current_bearing), bearing)}"
                )
            }

        }

    }

    private fun handleDestinationCalculation() {
        if (hasDestinationLocation && hasUserLocation) {
            calculateDestinationData()
            saveCoordinatesHistory()
        }
    }

    private fun calculateDestinationData() {
        val bearing = GeoCalculationHelper.calculateBearingBetweenDestinations(myLat, myLon, destinationLat, destinationLon)
        val distance = GeoCalculationHelper.calculateDestinationDistance(myLat, myLon, destinationLat, destinationLon)
        destinationBearing.set(bearing)
        destinationDistance.set(distance)
    }

    private fun checkSensorData() {
        if (hasGravityData && hasGeomagneticData) {
            val identityMatrix = FloatArray(9)
            val rotationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                rotationMatrix, identityMatrix,
                gravityData, geomagneticData
            )

            if (success) {
                val bearing = calculateCurrentBearingFromSensors(rotationMatrix)
                rotationDegreeInt.get()?.let { setBearing ->
                    if (bearing > setBearing + 2 || bearing < setBearing - 2) {
                        rotationDegreeInt.set(bearing)
                    }
                }
            }
        }
    }
}