package com.example.simpleweatherapp.network

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.models.WeatherResponse
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class WeatherApiService(
    private val client: BaseNetworkClient,
) : ApiService() {
    suspend fun getWeather(): ApiResult<WeatherResponse> =
        safeApiCall(
            call = {
                client.instance.get(urlString = "forecast.json") {
                    contentType(type = ContentType.Application.Json)
                    url { parameters.append(name = "key", value = BuildConfig.BASE_URL_API_KEY) }
                    // Москва по умолчанию как по ТЗ.
                    // Можно в перспективе передавать координаты пользователя или выбранный город из настроек.
                    url { parameters.append(name = "q", value = "55.7569,37.6151") }
                    url { parameters.append(name = "days", value = "3") }
                }
            },
            transform = { body ->
                val weatherResponse = fromJson<WeatherResponse>(json = body)
                ApiResult.Success(data = weatherResponse)
            },
            defaultErrorTitle = "Ошибка получения погоды",
            defaultErrorMessage = "Произошла ошибка при получении погоды",
        )

}