package com.tut.mapmarker

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import android.content.Context
import android.graphics.Canvas
import androidx.annotation.DrawableRes

import com.google.android.gms.maps.model.BitmapDescriptor

class MapsActivity : FragmentActivity(), OnMapReadyCallback,
    OnMapLongClickListener, OnMarkerDragListener {

    private val TAG = "MapsActivity"
    private var mMap: GoogleMap? = null
    private var geocoder: Geocoder? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        geocoder = Geocoder(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap!!.setOnMapLongClickListener(this)
        mMap!!.setOnMarkerDragListener(this)

        // Get current location using GPS (permission required)
        // Then create non draggable marker for current location
        try {
            // current location
            val addresses = geocoder!!.getFromLocationName("gurugram", 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                val markerOptions = MarkerOptions()
                    .position(location)
                    .title(address.locality)
//                    .draggable(true)
                // Blue dot marker for showing current location
                mMap!!.addMarker(markerOptions).apply {
                    setIcon(
                        bitmapDescriptorFromVector(this@MapsActivity, R.drawable.current_loc_marker)
                    )
                }
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //  bitmap for marker icon from vector image
    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {

        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth ,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapLongClick(latLng: LatLng) {
        try {
            val addresses = geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                if (marker != null) {
                    marker!!.let {
                        it.position = latLng
                        it.title = streetAddress
                    }
                }else{
                    val markerOptions = MarkerOptions()
                        .position(LatLng(address.latitude, address.longitude))
                        .title(address.locality)
                        .draggable(true)
                    marker = mMap!!.addMarker(markerOptions)
                    marker?.rotation
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMarkerDragStart(marker: Marker) {
        Log.d(TAG, "onMarkerDragStart: ")
        // Bounce up animation
    }

    override fun onMarkerDrag(marker: Marker) {
        Log.d(TAG, "onMarkerDrag: ")
        // Bounce down animation
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val latLng = marker.position
        try {
            val addresses = geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                marker.title = streetAddress
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}