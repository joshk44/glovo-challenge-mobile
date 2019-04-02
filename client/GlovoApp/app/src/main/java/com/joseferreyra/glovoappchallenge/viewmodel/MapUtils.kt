package com.joseferreyra.glovoappchallenge.viewmodel

import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil
import com.joseferreyra.glovoappchallenge.data.dto.City

class MapUtils {

    fun buildPolygons(cities: List<City?>, map: GoogleMap): ArrayList<PolygonOptions> {
        var result = ArrayList<PolygonOptions>()
        for (city: City? in cities) {
            if (city != null) {
                val polylines = city?.workingArea
                if (polylines != null) {
                    for (polyline: String in polylines) {
                        if (polyline.isNotEmpty()) {
                            val options = PolygonOptions()
                            options.fillColor(Color.parseColor("#4400FF00"))
                            options.strokeColor(Color.parseColor("#4400FF00"))
                            options.strokeWidth(2.0f)
                            options.addAll(PolyUtil.decode(polyline))
                            result.add(options!!)
                        }
                    }
                }
            }
        }
        return result
    }

    fun buildCityPoints(cities: List<City?>, map: GoogleMap): ArrayList<MarkerOptions> {
        var result = ArrayList<MarkerOptions>()
        for (city: City? in cities) {
            if (city?.workingArea != null && city?.workingArea!!.size > 0) {
                var cityPoint: LatLng? = getLocationFromWorkingArea(city)
                if (cityPoint != null) {
                    result.add(MarkerOptions()
                            .position(LatLng(cityPoint.latitude, cityPoint.longitude))
                            .title(city.name))
                } else {
                    Log.d("JOSENOTFOUND", "cityPointnull ${city.name}")
                }
            } else {
                Log.d("JOSENOTFOUND", "emptyWA ${city?.name}")
            }
        }
        return result
    }

    fun getLocationFromWorkingArea(city: City): LatLng? {
        if (city?.workingArea != null && city?.workingArea!!.size > 0) {
            for (warea: String in city?.workingArea!!) {
                if (!warea.isEmpty()) {
                    return PolyUtil.decode(warea)[0]
                }
            }
        }
        return null
    }
}