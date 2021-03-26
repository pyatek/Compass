package com.pyatek.compass.ui.compass

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.pyatek.compass.BR
import com.pyatek.compass.R
import com.pyatek.compass.adapters.CoordinatesAdapter
import com.pyatek.compass.adapters.ICoordinatesListener
import com.pyatek.compass.app.base.BaseFragment
import com.pyatek.compass.data.local.entity.HistoryCoordinatesEntity
import com.pyatek.compass.databinding.FragmentCompassBinding
import com.pyatek.compass.helpers.PermissionsManager
import com.pyatek.compass.helpers.Router
import com.pyatek.compass.helpers.visibility
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_compass.*
import java.lang.ref.WeakReference
import javax.inject.Inject

class CompassFragment : BaseFragment<FragmentCompassBinding, CompassViewModel>(),
    SensorEventListener, ICoordinatesListener {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun getLayoutId(): Int = R.layout.fragment_compass
    override fun getViewModels(): CompassViewModel =
        ViewModelProvider(this, viewModelFactory).get(CompassViewModel::class.java)
    override fun getBindingVariable(): Int = BR.vm

    private var isListVisible = false
    private val locationPermissionRequestCode = 2321
    private val locationPermissionsList = listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    private lateinit var permissionManager: PermissionsManager
    private lateinit var coordinatesAdapter: WeakReference<CoordinatesAdapter>
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupPermissionManager()
        initSensors()
        handleSetDestinationListener()
        handleCurrentNavigationListener()
        handleCurrentDestinationObservable()
        handleCoordinatesListObservable()
        handleListVisibilityClick()
        setupUserLocation()
        handleCompassClick()
        setupRecycler()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            handleSensorData(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        handleAccuracyState(sensor, accuracy)
    }

    override fun onMapClick(id: Int) {
        findNavController().navigate(CompassFragmentDirections.actionNavCompassToNavMap(id))
    }

    override fun onSelected(coordinates: HistoryCoordinatesEntity) {
        viewModel.setDestination(coordinates.endLat, coordinates.endLon)
        isListVisible = false
        setupListVisibility(false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCoordinatesDialog(viewModel.destinationLat, viewModel.destinationLon)
        }
    }

    private fun setupPermissionManager() {
        permissionManager = PermissionsManager(
            requireActivity(),
            locationPermissionsList,
            locationPermissionRequestCode
        )
    }

    @SuppressLint("MissingPermission")
    private fun setupUserLocation() {
        if (!permissionManager.checkPermissions()) {
            val client = LocationServices.getFusedLocationProviderClient(requireActivity())
            client.lastLocation.addOnSuccessListener { location : Location? ->
                location?.let {
                    viewModel.setCurrentLocation(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun initSensors() {
        sensorManager = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    private fun handleSensorData(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                viewModel.setGravityData(event.values)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                viewModel.setGeomagneticData(event.values)
            }
            else -> return
        }
    }

    private fun handleAccuracyState(sensor: Sensor?, accuracy: Int) {
        sensor?.let {
            if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                viewModel.currentCompassAccuracy = accuracy
            }
        }
    }

    private fun handleCompassClick() {
        compassView.setOnClickListener {
            Router.showCalibrateDialog(requireContext(), viewModel.currentCompassAccuracy)
        }
    }

    private fun setupRecycler() {
        val data = listOf<HistoryCoordinatesEntity>()
        coordinatesAdapter = WeakReference(CoordinatesAdapter(data, this))
        compassHistoryRecycler.adapter = coordinatesAdapter.get()
        compassHistoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        compassHistoryRecycler.setHasFixedSize(true)
    }

    private fun handleListVisibilityClick() {
        compassSetDestinationFromListCardView.setOnClickListener {
            isListVisible = !isListVisible
            setupListVisibility(isListVisible)
        }
    }

    private fun setupListVisibility(isVisible: Boolean) {
        compassHistoryRecycler.visibility(isVisible)
        compassView.visibility(!isVisible)
        compassBearingTextView.visibility(!isVisible)
    }

    private fun handleCoordinatesListObservable() {
        viewModel.getCoordinatesListData()
        viewModel.coordinatesList.observe(viewLifecycleOwner) {
            it?.let { coordinatesList ->
                coordinatesAdapter.get()?.dataList = coordinatesList
                coordinatesAdapter.get()?.notifyDataSetChanged()
            }
        }
    }

    private fun handleCurrentDestinationObservable() {
        viewModel.isDestinationVisible.observe(viewLifecycleOwner) {
            it?.let {
                compassCurrentDestinationCardView.visibility(it)
            }
        }
    }

    private fun handleCurrentNavigationListener() {
        compassCurrentDestinationCardView.setOnClickListener {
            findNavController().navigate(CompassFragmentDirections.actionNavCompassToNavMap(
                viewModel.getCurrentCoordinates())
            )
        }
    }

    private fun handleSetDestinationListener() {
        compassSetDestinationCardView.setOnClickListener {
            if (checkLocationPermissions()) {
                openCoordinatesDialog(viewModel.destinationLat, viewModel.destinationLon)
            }
        }
    }

    private fun openCoordinatesDialog(lat: Double, lon: Double) {
        Router.showInsertCoordinatesDialog(requireContext(), lat, lon) {
            viewModel.setDestination(it.latitude, it.longitude)
        }
    }

    private fun checkLocationPermissions(): Boolean {
        if (permissionManager.checkPermissions()) {
            permissionManager.requestPermissions()
            return false
        }
        return true
    }
}