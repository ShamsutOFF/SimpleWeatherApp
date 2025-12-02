package com.example.simpleweatherapp.network

import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class WeatherApiService(
    private val client: BaseNetworkClient,
) : ApiService() {
    suspend fun getWeather(): ApiResult<String> =
        safeApiCall(
            call = {
                client.instance.get(urlString = "forecast.json") {
                    contentType(type = ContentType.Application.Json)
                    url { parameters.append(name = "key", value = "fa8b3df74d4042b9aa7135114252304") }
                    url { parameters.append(name = "q", value = "55.7569,37.6151") }
                    url { parameters.append(name = "days", value = "3") }
                }
            },
            transform = { body ->
                val fooString = fromJson<String>(json = body)
                ApiResult.Success(data = fooString)
            },
            defaultErrorTitle = "Ошибка получения погоды",
            defaultErrorMessage = "Произошла ошибка при получении погоды",
        )

}