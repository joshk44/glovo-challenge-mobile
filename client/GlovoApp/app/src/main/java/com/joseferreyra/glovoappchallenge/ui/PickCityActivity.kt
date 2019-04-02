package com.joseferreyra.glovoappchallenge.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.joseferreyra.glovoappchallenge.R
import com.joseferreyra.glovoappchallenge.data.dto.City
import com.joseferreyra.glovoappchallenge.data.dto.Country
import com.joseferreyra.glovoappchallenge.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.activity_maps.view.*
import kotlinx.android.synthetic.main.activity_pick_city.*


class PickCityActivity : AppCompatActivity() {

    var countries: List<Country>? = null
    var cities: List<City>? = null
    var countriesAdapter: ArrayAdapter<String>? = null
    var citiesAdapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_city)
        initViews()

        val viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        viewModel?.countries?.observe(this@PickCityActivity, Observer<List<Country>> { countries ->
            if (countries != null && countries.size > 0) {
                countrySpinner.isEnabled = true
                this@PickCityActivity.countries = countries
                for (country: Country in countries) {
                    countriesAdapter?.add(country.name)
                }
                countriesAdapter?.notifyDataSetChanged()
            }
        })

        viewModel?.cities?.observe(this@PickCityActivity, Observer<List<City>> { cities ->
            if (cities != null && cities.size > 0) {
                citySpinner.isEnabled = true
                this@PickCityActivity.cities = cities
            }
        })
    }

    fun initViews() {

        countriesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayList<String>())
        countrySpinner.adapter = countriesAdapter
        citiesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayList<String>())
        citySpinner.adapter = citiesAdapter
        countrySpinner.isEnabled=false
        citySpinner.isEnabled=false
        selectCity.isEnabled = false

        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                citySpinner.isEnabled = false
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                citySpinner.isEnabled = true
                if (this@PickCityActivity?.cities != null && this@PickCityActivity?.cities!!.size > 0) {
                    populateCitySpinner(this@PickCityActivity?.countries!![countrySpinner.selectedItemPosition]?.code!!)
                }

            }

        }

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectCity.isEnabled = false
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectCity.isEnabled = true
            }
        }

        selectCity.setOnClickListener {
            val i = intent
            i.putExtra("cityName", citiesAdapter!!.getItem(citySpinner.selectedItemPosition))
            setResult(RESULT_OK, i)
            finish()
        }
    }

    fun populateCitySpinner(countryCode: String) {
        if (cities != null) {
            citiesAdapter?.clear()
            for (city: City in cities!!) {
                if (city.countryCode == countryCode)
                    citiesAdapter?.add(city.name)
            }
            citiesAdapter?.notifyDataSetChanged()
        }
    }

}

