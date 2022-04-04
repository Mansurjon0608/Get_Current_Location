package com.mstar004.mapapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var singleShotLocationProvider: SingleShotLocationProvider
    private var dialogFineLocation: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        checkPermission()

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

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        dialogFineLocation?.dismiss()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                dialogFineLocation = Dialog(requireContext(), androidx.appcompat.R.style.AlertDialog_AppCompat)
                dialogFineLocation?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogFineLocation?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialogFineLocation?.setContentView(R.layout.dialog_error_location)
                dialogFineLocation?.setCanceledOnTouchOutside(false)
                dialogFineLocation?.setCancelable(false)

                dialogFineLocation?.findViewById<AppCompatTextView>(R.id.tvTurnLocation)
                    ?.setOnClickListener {
                        val viewIntent =
                            Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                                "package:$ACTION_APPLICATION_DETAILS_SETTINGS"))
                        startActivity(viewIntent)
                    }
                dialogFineLocation?.show()
            }
        } else if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            dialogFineLocation = Dialog(requireContext(), androidx.appcompat.R.style.AlertDialog_AppCompat)
            dialogFineLocation?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogFineLocation?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogFineLocation?.setContentView(R.layout.dialog_error_location)
            dialogFineLocation?.setCanceledOnTouchOutside(false)
            dialogFineLocation?.setCancelable(false)

            dialogFineLocation?.findViewById<AppCompatTextView>(R.id.tvTurnLocation)
                ?.setOnClickListener {

                    val viewIntent =
                        Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                            "package: $ACTION_APPLICATION_DETAILS_SETTINGS"))
                    startActivity(viewIntent)
                }
            dialogFineLocation?.show()
        }

    }
}