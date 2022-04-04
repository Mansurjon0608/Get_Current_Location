package com.mstar004.mapapp

import android.location.Location
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var singleShotLocationProvider: SingleShotLocationProvider


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private val callback = OnMapReadyCallback { googleMap ->
        singleShotLocationProvider = SingleShotLocationProvider()

        singleShotLocationProvider.requestSingleUpdate(requireContext(),
            object : SingleShotLocationProvider(),
                SingleShotLocationProvider.LocationCallback {
                override fun onNewLocationAvailable(location: GPSCoordinates?, loc:Location) {
                    val sydney = LatLng(location?.latitude!!.toDouble(), location.longitude.toDouble())

                    googleMap.addMarker(MarkerOptions().position(sydney).title("Your place"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

                    val comPos = CameraPosition.fromLatLngZoom(sydney, 16f)
                    val com = CameraPosition.builder(comPos).bearing(loc.bearing).build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(com))
                }

            })


    }
}