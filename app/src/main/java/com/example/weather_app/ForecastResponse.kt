data class ForecastApiResponse(
    val location: Location,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val country: String
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: DayWeather,
    val hour: List<HourlyWeather>
)

data class DayWeather(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val condition: Condition
)

data class HourlyWeather(
    val time: String,
    val temp_c: Double,
    val condition: Condition
)

data class Condition(
    val text: String,  // Weather condition (e.g., "Sunny", "Cloudy")
    val icon: String   // URL for weather icon
)