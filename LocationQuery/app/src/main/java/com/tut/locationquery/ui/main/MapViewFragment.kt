package com.tut.locationquery.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tut.locationquery.MainActivity
import com.tut.locationquery.R
import com.tut.locationquery.adaptors.LocCardAdaptor
import com.tut.locationquery.container.CardData
import com.tut.locationquery.databinding.FragmentMapViewBinding

/**
 * A placeholder fragment containing a simple view.
 */
class MapViewFragment : Fragment(), OnMapReadyCallback {

//    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMapViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapViewBinding.inflate(inflater, container, false)
        val root = binding.root

        mapView = binding.mapView
        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)

//        val address: Address? = null
//        (activity as MainActivity).pin_locations.add(
//            CardData(address,"Sector 5","address.getAddressLine(0)","10")
//        )
        val rv = binding.rvLocDetailCards
        rv.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        rv.adapter = (activity as MainActivity).listDataAdaptor

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
        val geocoder = Geocoder(activity)
        var address = geocoder.getFromLocationName((activity as MainActivity).myLoc, 1)[0]
        val myPos = LatLng(address.latitude, address.longitude)
        val markerOptions = MarkerOptions().apply {
            position(myPos)
            title(address.featureName)
            icon(bitmapDescriptorFromVector(activity as MainActivity, R.drawable.person_pin))
        }
        mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14f))

        for(loc in (activity as MainActivity).locations){
            address = geocoder.getFromLocationName(loc, 1)[0]
//            println(address.getAddressLine(0))
            val markerPos = LatLng(address.latitude, address.longitude)
            (activity as MainActivity).pin_locations.add(
                CardData(address,"${markerPos.latitude}, ${markerPos.longitude}",
                    address.getAddressLine(0),"1.5Km")
            )
            mMap.addMarker(MarkerOptions().position(markerPos).title(address.featureName))
        }
        binding.rvLocDetailCards.adapter?.notifyItemRangeInserted(0,(activity as MainActivity).pin_locations.size)
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth + 36,
            vectorDrawable.intrinsicHeight + 36
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth * 2,
            vectorDrawable.intrinsicHeight * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mapView.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}