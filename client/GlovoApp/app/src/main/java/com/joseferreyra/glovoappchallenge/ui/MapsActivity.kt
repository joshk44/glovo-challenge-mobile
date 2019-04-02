package com.joseferreyra.glovoappchallenge.ui

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.joseferreyra.glovoappchallenge.R
import com.joseferreyra.glovoappchallenge.data.dto.City
import com.joseferreyra.glovoappchallenge.data.dto.CityInfo
import com.joseferreyra.glovoappchallenge.viewmodel.MapUtils
import com.joseferreyra.glovoappchallenge.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private var viewModel: MapViewModel? = null
    private var lastLocation: Location? = null
    private var maputils = MapUtils()
    private var polygons: List<PolygonOptions> = ArrayList()
    private var cityMarkers: List<MarkerOptions> = ArrayList()
    var lastZoom = 0F
    val PICK_CITY = 0
    val CITY_ZOOM = 10f
    var locationUpdated = false

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        moreCities.setOnClickListener {
            val i = Intent(this, PickCityActivity::class.java)
            startActivityForResult(i, PICK_CITY)
        }
        getLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        viewModel?.cities?.observe(this@MapsActivity, Observer<List<City>> { cities ->
            if (cities != null) {
                //Build the structures in order to avoid extra calls
                polygons = maputils.buildPolygons(cities, mMap)
                cityMarkers = maputils.buildCityPoints(cities, mMap)
            }
        })
        //SetClickListener for Markers.
        mMap.setOnMarkerClickListener(this)

        mMap.setOnCameraIdleListener(GoogleMap.OnCameraIdleListener {
            var zoomLevel = mMap.cameraPosition.zoom
            //if does not change the umbral of citysize zoom
            if (!((lastZoom < CITY_ZOOM && zoomLevel < CITY_ZOOM) || (lastZoom >= CITY_ZOOM && zoomLevel >= CITY_ZOOM))) {
                mMap.clear()
                if (zoomLevel < CITY_ZOOM) {
                    for (marker: MarkerOptions in cityMarkers) {
                        mMap.addMarker(marker)
                    }
                } else {
                    for (polygon: PolygonOptions in polygons) {
                        mMap.addPolygon(polygon)
                    }
                }
            }
            fetchCityInfo(mMap.cameraPosition.target)
            lastZoom = zoomLevel
        })
        getLocation()
    }

    fun updatePosition(loc: LatLng?) {
        if (loc == null) return
        val point = LatLng(loc.latitude, loc.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, CITY_ZOOM))
    }


    fun getLocation() {

        var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        var locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {

                if (locationUpdated) return
                if (location == null || (lastLocation != null
                                && lastLocation?.latitude == location?.latitude
                                && lastLocation?.longitude == location?.longitude)) {
                    return
                } else if (location != null) {
                    locationUpdated = true
                    lastLocation = location
                    updatePosition(LatLng(location.latitude, location.longitude))
                    fetchCityInfo(LatLng(location.latitude, location.longitude))
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String?) {}
            override fun onProviderDisabled(provider: String?) {}
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }

        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults == null || grantResults.size == 0) return
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> for (marker: MarkerOptions in cityMarkers) {
                    mMap.addMarker(marker)
                }
            }
        }
    }


    fun getCityFromLocation(latlng: LatLng): City? {
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 100)
        if (addresses != null && addresses.size > 0) {
            val city = viewModel?.getCityFromName(addresses)
            return city
        }
        return null
    }


    fun fetchCityInfo(latlng: LatLng) {
        val city = getCityFromLocation(latlng)
        if (city != null) {
            val size = viewModel?.cities?.value?.size ?: 0
            if (size > 0) {
                fetchCityInfo(city)
            } else {
                viewModel?.cities?.observe(this@MapsActivity, Observer<List<City>> { cities ->
                    if (cities != null) fetchCityInfo(city)
                })
            }
        } else {
            updateCityUI(null)
        }
    }

    fun fetchCityInfo(city: City) {
        viewModel?.getCityInfo(viewModel?.getCityCode(city.name))?.observe(this@MapsActivity, Observer<CityInfo> { updateCityUI(it) })
    }


    fun updateCityUI(info: CityInfo?) {
        if (info != null) {
            cityDetails.visibility = View.VISIBLE
            moreCities.visibility = View.GONE
            cityName.text = info.name
            currency.text = info.currency
            languague.text = info.languageCode
            enabled.text = info.enabled.toString()
        } else {
            cityDetails.visibility = View.GONE
            moreCities.visibility = View.VISIBLE
        }
    }

    //zoom and center on the position
    override fun onMarkerClick(p0: Marker?): Boolean {
        if (p0 != null) {
            updatePosition(p0.position)
            fetchCityInfo(p0.position)
        }
        return true
    }


    //Once that the user select a city update position.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CITY) {
            if (resultCode == RESULT_OK) {
                val city = data?.getStringExtra("cityName")
                if (city != null && viewModel != null && viewModel!!.getCityFromName(city) != null) {
                    val latLong = maputils.getLocationFromWorkingArea(viewModel!!.getCityFromName(city)!!)
                    if (latLong != null) {
                        updatePosition(latLong)
                        fetchCityInfo(latLong)
                    }
                }
            }
        }
    }

}


