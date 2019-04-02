package com.joseferreyra.glovoappchallenge.data.dto

import com.google.gson.annotations.SerializedName

class City(
        @SerializedName("working_area")
        var workingArea: List<String>? = null,
        var code: String? = null,
        var name: String? = null,
        @SerializedName("country_code")
        var countryCode: String? = null)
