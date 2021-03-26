package com.pyatek.compass.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pyatek.compass.BR
import com.pyatek.compass.R
import com.pyatek.compass.app.base.BaseFragment
import com.pyatek.compass.databinding.FragmentMapBinding
import com.pyatek.compass.helpers.Res
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class MapFragment: BaseFragment<FragmentMapBinding, MapViewModel>() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun getLayoutId(): Int = R.layout.fragment_map
    override fun getViewModels(): MapViewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
    override fun getBindingVariable(): Int = BR.vm
    private val mapPadding = 200
    private var googleMap: GoogleMap? = null

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        handleNavigationArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupMapFragment()
        setupCoordinatesObservable()
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupCoordinatesObservable() {
        viewModel.currentDestination.observe(viewLifecycleOwner) {
            it?.let { coordinates ->
                googleMap?.let { googleMap ->
                    val startLatLng = LatLng(coordinates.startLat, coordinates.startLon)
                    val endLatLng = LatLng(coordinates.endLat, coordinates.endLon)
                    val markerStart = MarkerOptions().position(startLatLng).title(
                        Res.string(R.string.start))

                    val markerEnd = MarkerOptions().position(endLatLng).title(
                        Res.string(R.string.end))
                    val latLongBuilder = LatLngBounds.Builder()
                    latLongBuilder.include(markerStart.position)
                    latLongBuilder.include(markerEnd.position)
                    val cameraUpdateFactory = CameraUpdateFactory.newLatLngBounds(latLongBuilder.build(), mapPadding)

                    val polyLine = getPolyLine(startLatLng, endLatLng)

                    googleMap.addMarker(markerStart)
                    googleMap.addMarker(markerEnd)
                    googleMap.moveCamera(cameraUpdateFactory)
                    googleMap.addPolyline(polyLine)
                }
            }
        }
    }

    private fun getPolyLine(start: LatLng, end: LatLng): PolylineOptions {
        return PolylineOptions().apply {
            add(start)
            add(end)
            width(5f)
            color(Color.RED)
        }
    }

    private fun handleNavigationArgs() {
        arguments?.let {
            val coordinatesId = MapFragmentArgs.fromBundle(it).coordinatesId
            viewModel.setupCurrentDestination(coordinatesId)
        }
    }
}