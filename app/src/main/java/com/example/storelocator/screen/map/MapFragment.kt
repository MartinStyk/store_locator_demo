package com.example.storelocator.screen.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.storelocator.model.map.MapPinData
import com.example.storelocator.util.provideViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : SupportMapFragment() {

    @Inject
    lateinit var factory: MapFragmentViewModel.Factory

    private lateinit var viewModel: MapFragmentViewModel
    private val mapDataModel by viewModels<MapDataModel>(ownerProducer = { requireParentFragment() })
    private var markers = emptyList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = provideViewModel { factory.create(mapDataModel) }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMapAsync { it.setOnMarkerClickListener(viewModel) }

        with(viewModel) {
            showMyPosition.observe(viewLifecycleOwner, { show ->
                getMapAsync { it.isMyLocationEnabled = show }
            })
            showPins.observe(viewLifecycleOwner, { showPins(it) })
            cameraAnimation.observe(viewLifecycleOwner, { updateCamera(it) })
        }
    }

    private fun showPins(pins: List<MapPinData>) = getMapAsync { map ->
        markers.forEach { it.remove() }
        markers = pins.map {
            MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(LatLng(it.location.latitude, it.location.longitude))
                .title(it.title)
        }.map {
            map.addMarker(it)!!
        }
    }

    private fun updateCamera(update: CameraUpdate) = getMapAsync { map ->
        map.animateCamera(update)
    }
}