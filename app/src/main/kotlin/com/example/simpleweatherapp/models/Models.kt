package com.example.simpleweatherapp.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("forecast") val forecast: Forecast
)

data class Location(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val country: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("tz_id") val timezone: String,
    @SerializedName("localtime") val localtime: String
)

data class CurrentWeather(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("temp_f") val tempF: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("feelslike_c") val feelslikeC: Double
)

data class Forecast(
    @SerializedName("forecastday") val forecastDays: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: DayForecast,
    @SerializedName("hour") val hours: List<HourForecast>
)

data class DayForecast(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("avgtemp_c") val avgTempC: Double,
    @SerializedName("condition") val condition: Condition,
    @SerializedName("daily_chance_of_rain") val rainChance: Int
)

data class HourForecast(
    @SerializedName("time") val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: Condition
)

data class Condition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("code") val code: Int
)