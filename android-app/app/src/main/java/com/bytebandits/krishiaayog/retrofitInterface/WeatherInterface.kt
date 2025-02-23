package com.bytebandits.krishiaayog.retrofitInterface

import com.bytebandits.krishiaayog.DataClass.currentWeather.CurrentWeatherDataClass
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("units") units: String = "metric"
    ): Response<CurrentWeatherDataClass>
}

fun provideOpenWeatherService(): WeatherInterface {
    return Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create()).build().create()
}
