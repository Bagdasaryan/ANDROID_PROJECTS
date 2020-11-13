package com.example.covid_19.PARSING

import com.google.gson.annotations.SerializedName

class StatisticResponse {
    @SerializedName("Countries")
    var countries: ArrayList<parametersOfCountries>? = null
}

class parametersOfCountries {
    @SerializedName("Country")
    var country: String? = null

    @SerializedName("CountryCode")
    var countryCode: String? = null

    @SerializedName("Slug")
    var slug: String? = null

    @SerializedName("NewConfirmed")
    var newConfirmed: Int = 0

    @SerializedName("TotalConfirmed")
    var totalConfirmed: Int = 0

    @SerializedName("NewDeaths")
    var newDeaths: Int = 0

    @SerializedName("TotalDeaths")
    var totalDeaths: Int = 0

    @SerializedName("NewRecovered")
    var newRecovered: Int = 0

    @SerializedName("TotalRecovered")
    var totalRecovered: Int = 0

    @SerializedName("Date")
    var date: String? = null

    // Premium
}






















