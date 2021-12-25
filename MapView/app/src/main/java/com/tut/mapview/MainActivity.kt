package com.tut.mapview

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback, SearchView.OnQueryTextListener {
    private var mMap: GoogleMap? = null
    private var geocoder: Geocoder? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, TutorialMap::class.java))

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        geocoder = Geocoder(this)

/*        findViewById<Button>(R.id.btn_close_tut).setOnClickListener {
            val tut_layout = findViewById<ConstraintLayout>(R.id.tut_view)
            tut_layout.visibility = View.GONE
        }*/

        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(this)

/*        mapView = findViewById(R.id.mapView)
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(this)
        mapView?.getMapAsync(this)
        mapView?.onCreate(savedInstanceState)
        geocoder = Geocoder(this)*/
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        try {
            val addresses = geocoder!!.getFromLocationName(query.toString(), 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                marker?.position = location
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return false
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap!!.setOnCameraIdleListener {
            val mapCenter = mMap!!.cameraPosition.target
            try {
                val addresses = geocoder!!.getFromLocation(mapCenter.latitude, mapCenter.longitude, 1)
                if (addresses.size > 0) {
                    val address = addresses[0]
                    val streetAddress = address.getAddressLine(0)
                    marker?.position = mapCenter
                    marker?.title = streetAddress
/*                    findViewById<TextView>(R.id.pinInfo).text = streetAddress
                    findViewById<LinearLayout>(R.id.marker).animate().apply {
                        duration = 100
                        interpolator = AnticipateOvershootInterpolator()
                        translationY(-30f)
                        withEndAction{
                            translationY(30f)
                        }
                        start()
                    }*/
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            // current location
            val addresses = geocoder!!.getFromLocationName("gurugram", 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                val markerOptions = MarkerOptions().apply {
                    position(location)
                    zIndex(3f)
                    title(address.getAddressLine(0))
                }
                marker = mMap!!.addMarker(markerOptions)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
