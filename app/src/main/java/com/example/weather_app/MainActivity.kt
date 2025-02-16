package com.example.weather_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.example.weather_app.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Fetch weather for current location
        getUserLocation()
        SearchCity()
        binding.btnViewForecast.setOnClickListener {
            val intent = Intent(this, ForecastActivity::class.java)
            intent.putExtra("CITY_NAME", binding.cityname.text.toString())
            startActivity(intent)
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                fetchWeatherDataByCoordinates(latitude, longitude)
            } else {
                Log.e("TAG", "Location is null. Using default location (Delhi).")
                fetchweatherdata("Delhi")
            }
        }.addOnFailureListener {
            Log.e("TAG", "Failed to get location: ${it.message}")
            fetchweatherdata("Delhi") // Default if location fails
        }
    }

    private fun fetchWeatherDataByCoordinates(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(apiinterface::class.java)

        val response = retrofit.getWeatherDataByCoordinates(lat, lon, "3697ac12c6b5b71d224504e0b8ed82a5", "metric")
        response.enqueue(object : Callback<Weatherapp> {
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        updateUI(responseBody)
                    } else {
                        Log.e("TAG", "Response Body is NULL")
                    }
                } else {
                    Log.e("TAG", "Request Failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                Log.e("TAG", "API Call Failed: ${t.message}")
            }
        })
    }

    private fun fetchweatherdata(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(apiinterface::class.java)

        val response = retrofit.getWeatherdata(cityName, "3697ac12c6b5b71d224504e0b8ed82a5", "metric")
        response.enqueue(object : Callback<Weatherapp> {
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        updateUI(responseBody)
                    } else {
                        Log.e("TAG", "Response Body is NULL")
                    }
                } else {
                    Log.e("TAG", "Request Failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                Log.e("TAG", "API Call Failed: ${t.message}")
            }
        })
    }

    private fun updateUI(responseBody: Weatherapp) {
        val temperature = responseBody.main.temp.toString()
        val humidity = responseBody.main.humidity
        val windspeed = responseBody.wind.speed
        val sunrise = responseBody.sys.sunrise.toLong()
        val sunset = responseBody.sys.sunset.toLong()
        val sealevel = responseBody.main.pressure
        val maxtemp = responseBody.main.temp_max
        val mintemp = responseBody.main.temp_min
        val condition = responseBody.weather.firstOrNull()?.main ?: "Unknown"

        binding.temp.text = "$temperature °C"
        binding.daytype.text = condition
        binding.minTemp.text = "Min Temp $mintemp °C"
        binding.maxTemp.text = "Max Temp $maxtemp °C"
        binding.Humidity.text = "$humidity %"
        binding.windspeed.text = "$windspeed m/s"
        binding.sunrise.text = time(sunrise)
        binding.sunset.text = time(sunset)
        binding.sea.text = "$sealevel hPa"
        binding.sunny.text = condition
        binding.day.text = dayName(System.currentTimeMillis())
        binding.date.text = date()
        binding.cityname.text = responseBody.name

        changeweather(condition)
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        } else {
            Log.e("TAG", "Location permission denied. Using default location (Delhi).")
            fetchweatherdata("Delhi")
        }
    }
    private fun changeweather(conditions: String) {
        Log.e("TAG", "Weather Condition: $conditions") // Debugging

        when (conditions.lowercase()) { // Use lowercase to avoid case mismatches
            "clear", "sunny" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "partly cloudy", "clouds", "overcast", "mist", "fog" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "rain", "drizzle", "moderate rain", "showers", "heavy rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "snow", "moderate snow", "heavy snow", "blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

        }
        binding.lottieAnimationView.playAnimation()
    }
}
