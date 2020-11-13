package com.example.covid_19.PARSING

import retrofit2.Call
import retrofit2.http.GET

interface StatisticGet {
    @GET("summary")
    fun getCurrentStatistic(): Call<StatisticResponse>
}