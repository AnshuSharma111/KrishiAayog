package com.bytebandits.krishiaayog.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytebandits.krishiaayog.BuildConfig
import com.bytebandits.krishiaayog.DataClass.HomePageAnnouncements
import com.bytebandits.krishiaayog.DataClass.currentWeather.CurrentWeatherDataClass
import com.bytebandits.krishiaayog.retrofitInterface.RetrofitInterface
import com.bytebandits.krishiaayog.retrofitInterface.provideOpenWeatherService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


private const val apikey = BuildConfig.WEATHER_API_KEY


@SuppressLint("MissingPermission")
class HomeScreenViewModel (application: Application) : AndroidViewModel(application) {

    val weatherData = mutableStateOf<CurrentWeatherDataClass?>(null)

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val _announcements = MutableStateFlow<HomePageAnnouncements?>(null)
    val announcements: StateFlow<HomePageAnnouncements?> = _announcements.asStateFlow()


    fun onPermissionGranted() {
        println("Permission Granted")
        val client = LocationServices.getFusedLocationProviderClient(getApplication() as Context)
        val request = LocationRequest.Builder(20000)
            .setMinUpdateIntervalMillis(10000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        client.requestLocationUpdates(request, object : LocationCallback() {
            @SuppressLint("SuspiciousIndentation")
            override fun onLocationResult(currentLocation: LocationResult) {
                val location = currentLocation.locations
                val lat = location[0].latitude
                val lon = location[0].longitude
                println("Latitude: $lat, Longitude: $lon")
                fetchWeather(lat, lon)

            }

        }, Looper.getMainLooper())


    }

    private fun fetchWeather(lat: Double, lon: Double) {
        println("Fetching current weather")

        viewModelScope.launch(Dispatchers.IO) {

            val weatherService = provideOpenWeatherService()
            val response_currentweather = weatherService.getCurrentWeather(
                lat, lon, apikey
            )

            withContext(Dispatchers.Main) {

                if (response_currentweather.isSuccessful) {
                    weatherData.value = response_currentweather.body()
                } else {
                    weatherData.value = null
                }

            }
        }
    }

    fun getAnnouncements() {
        viewModelScope.launch {
            try {
                val retrofit = RetrofitInterface.create(context)
                val response = retrofit.getAnnouncements()
                if (response.isSuccessful) {
                    _announcements.value = response.body()
                } else {
                    println("${response.errorBody()} + ${response.code()}")
                }
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}

class HomeScreenViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            return HomeScreenViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

