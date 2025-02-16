package com.example.weather_app

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
interface apiinterface {
    @GET("weather")
    fun getWeatherdata(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<Weatherapp>

    @GET("weather")
    fun getWeatherDataByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<Weatherapp>
}