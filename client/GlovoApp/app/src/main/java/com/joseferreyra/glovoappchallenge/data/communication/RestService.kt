package com.joseferreyra.glovoappchallenge.data.communication

import com.joseferreyra.glovoappchallenge.data.dto.City
import com.joseferreyra.glovoappchallenge.data.dto.CityInfo
import com.joseferreyra.glovoappchallenge.data.dto.Country
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for requests.
 */
interface RestService {

    @GET("countries/")
    fun getCountries(): Observable<List<Country>>

    @GET("cities/")
    fun getCities(): Observable<List<City>>

    @GET("cities/{city}")
    fun getCity(@Path("city") city: String): Observable<CityInfo>

    companion object {
        val BASE_URL = "http://192.168.1.14:3000/api/"
        fun create(): RestService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build()

            return retrofit.create(RestService::class.java)
        }
    }
}