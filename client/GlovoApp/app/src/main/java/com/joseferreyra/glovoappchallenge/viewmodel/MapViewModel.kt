package com.joseferreyra.glovoappchallenge.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.location.Address
import com.joseferreyra.glovoappchallenge.data.MapRepository
import com.joseferreyra.glovoappchallenge.data.dto.City
import com.joseferreyra.glovoappchallenge.data.dto.CityInfo
import com.joseferreyra.glovoappchallenge.data.dto.Country

/**
 * ViewModel that provides Live Data to the Activity that suscribe for update events.
 */
open class MapViewModel(app: Application) : AndroidViewModel(app) {

    val repository = MapRepository()
    var cities: LiveData<List<City>>
    var countries: LiveData<List<Country>>
    var cityInfo: LiveData<CityInfo>

    init {
        //As the example request init the map on Hamburg.
        cities = repository.getCities()
        countries = repository.getCountries()
        cityInfo = repository.getCityInfo(null)
    }

    fun getCityInfo(cityCode: String?): LiveData<CityInfo> {
        repository.fetchCityInfoFromServer(cityCode)
        return cityInfo
    }

    fun getCityCode(cityName: String?): String? {
        if (cities != null && cities.value != null) {
            for (city: City in cities!!.value!!)
                if (city.name == cityName)
                    return city.code
        }
        return null
    }

    fun getCityFromName(cityName: String?): City? {
        if (cities != null && cities.value != null) {
            for (city: City in cities!!.value!!)
                if (city.name == cityName)
                    return city
        }
        return null
    }

    fun getCityFromName(adresses: List<Address>): City? {
        if (adresses != null && adresses.size > 0) {
            for (address: Address in adresses) {
                if (cities != null && cities.value != null) {
                    for (city: City in cities!!.value!!)
                        if (city.name == address.locality)
                            return city
                }
            }
        }
        return null
    }


}