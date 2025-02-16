import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {
    @GET("forecast.json")
    fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 7, // Fetch forecast for next 7 days
        @Query("aqi") airQuality: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Call<ForecastApiResponse>
}