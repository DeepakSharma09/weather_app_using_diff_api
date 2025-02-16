package com.example.weather_app

import ForecastApiResponse
import ForecastDay
import HourlyWeather
import WeatherApiInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.databinding.ActivityForecastBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class ForecastActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForecastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cityName = intent.getStringExtra("CITY_NAME") ?: "Unknown City"
        binding.txtCityName.text = cityName

        // Initialize RecyclerViews
        binding.recyclerHourly.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerDaily.layoutManager = LinearLayoutManager(this)

        // Fetch weather forecast data
        fetchWeatherForecast(cityName)
    }

    private fun fetchWeatherForecast(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiInterface::class.java)

        val call = retrofit.getWeatherForecast("6e2a3ce63cce4a478c0130901251502", cityName, 7)
        call.enqueue(object : Callback<ForecastApiResponse> {
            override fun onResponse(call: Call<ForecastApiResponse>, response: Response<ForecastApiResponse>) {
                Log.d("API_RESPONSE", "Response Code: ${response.code()}")

                if (response.isSuccessful) {
                    val forecastData = response.body()
                    Log.d("API_RESPONSE", "Forecast Data: ${forecastData.toString()}")

                    forecastData?.let {
                        if (it.forecast.forecastday.isNotEmpty()) {
                            updateHourlyForecast(it.forecast.forecastday[0].hour) // Today's hourly forecast
                            updateDailyForecast(it.forecast.forecastday) // 7-day forecast
                        } else {
                            Log.e("API_RESPONSE", "No forecast data available!")
                        }
                    }
                } else {
                    Log.e("API_RESPONSE", "Request Failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ForecastApiResponse>, t: Throwable) {
                Log.e("API_RESPONSE", "API Call Failed: ${t.message}")
            }
        })
    }

    private fun updateHourlyForecast(hourlyList: List<HourlyWeather>) {
        val adapter = HourlyAdapter(hourlyList)
        binding.recyclerHourly.adapter = adapter
    }

    private fun updateDailyForecast(dailyList: List<ForecastDay>) {
        val adapter = DailyAdapter(dailyList)
        binding.recyclerDaily.adapter = adapter
    }
}