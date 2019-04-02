package com.joseferreyra.glovoappchallenge.data.dto

import com.google.gson.annotations.SerializedName

class CityInfo(
        var code: String? = null,
        var name: String? = null,
        var currency: String? = null,
        @SerializedName("country_code")
        var countryCode: String? = null,
        var enabled: Boolean? = null,
        @SerializedName("time_zone")
        var timeZone: String? = null,
        @SerializedName("working_area")
        var workingArea: List<String>? = null,
        var busy: Boolean? = null,
        @SerializedName("language_code")
        var languageCode: String? = null
)
