package com.joseferreyra.glovoappchallenge.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.joseferreyra.glovoappchallenge.data.dto.City
import com.joseferreyra.glovoappchallenge.data.dto.CityInfo
import com.joseferreyra.glovoappchallenge.data.dto.Country
import com.joseferreyra.glovoappchallenge.ui.MainApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * This repository is a data provider.
 * In the future if we want to add another data source, we should put that logic here.
 * and decide which data would be send to the view Model.
 */
class MapRepository {



    private var disposable: Disposable? = null
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    var cities: MutableLiveData<List<City>> = MutableLiveData()
    var countries: MutableLiveData<List<Country>> = MutableLiveData()
    var cityInfo: MutableLiveData<CityInfo> = MutableLiveData()

    fun getCities(): LiveData<List<City>> {
        fetchCitiesFromServer()
        return cities
    }

    fun getCountries(): LiveData<List<Country>> {
        fetchCountriesFromServer()
        return countries
    }

    fun getCityInfo(cityCode: String?): LiveData<CityInfo> {
        if (cityCode != null) fetchCityInfoFromServer(cityCode)
        return cityInfo
    }


    fun fetchCitiesFromServer() = scope.launch(Dispatchers.IO) {
        disposable = MainApplication.restClientService.getCities()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            cities.postValue(result)
                        },
                        { error -> Log.d("ERROR DATA", "Error loading City list $error") }
                )
    }

    fun fetchCountriesFromServer() = scope.launch(Dispatchers.IO) {
        disposable = MainApplication.restClientService.getCountries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            countries.postValue(result)
                        },
                        { error -> Log.d("ERROR DATA", "Error loading County list $error") }
                )
    }

    fun fetchCityInfoFromServer(cityCode: String?) = scope.launch(Dispatchers.IO) {
        if (cityCode==null || cityCode.isEmpty()) {
            cityInfo.postValue(null)
        } else {
            disposable = MainApplication.restClientService.getCity(cityCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                cityInfo.postValue(result)
                            },
                            { error -> Log.d("ERROR DATA", "Error loading City Info $error") }
                    )
        }

    }

}