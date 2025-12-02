package com.example.simpleweatherapp.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import timber.log.Timber

/**
 * Базовый сервис для всех HTTP API.
 * Реализует общий safeApiCall и Json парсинг.
 */
abstract class ApiService(
    protected val gson: Gson = GsonBuilder().create(),
) {
    /**
     * Общий safeApiCall для API-запросов.
     * @param call - suspend блок, возвращающий HttpResponse
     * @param transform - как обработать тело ответа при успешном статусе
     */
    protected suspend fun <T> safeApiCall(
        call: suspend () -> HttpResponse,
        transform: (String) -> ApiResult<T>,
        notFoundMessage: String = "Ресурс не найден",
        defaultErrorTitle: String = "Ошибка API",
        defaultErrorMessage: String = "Произошла ошибка. Попробуйте ещё раз",
    ): ApiResult<T> =
        try {
            val response = call()
            val body = response.bodyAsText()

            Timber.d("@@@ Http ${response.status}: $body")

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> transform(body)

                HttpStatusCode.NotFound ->
                    ApiResult.Error(
                        title = "Не найдено",
                        message = notFoundMessage,
                        code = response.status.value,
                    )

                else ->
                    ApiResult.Error(
                        title = defaultErrorTitle,
                        message = "$defaultErrorMessage (код: ${response.status.value})",
                        code = response.status.value,
                    )
            }
        } catch (e: ClientRequestException) {
            Timber.e(e, "@@@ Client error")
            ApiResult.Error("Client error", e.message, e.response.status.value)
        } catch (e: ServerResponseException) {
            Timber.e(e, "@@@ Server error")
            ApiResult.Error("Server error", e.message, e.response.status.value)
        } catch (e: Exception) {
            Timber.e(e, "@@@ Unexpected error")
            ApiResult.Error("Unexpected error", e.message ?: "Неизвестная ошибка")
        }

    /**
     * Универсальный JSON‑парсер
     */
    protected inline fun <reified T> fromJson(json: String): T = gson.fromJson(json, T::class.java)

    /**
     * Хелпер: маппинг успешного JSON напрямую в ApiResult.Success
     */
    protected inline fun <reified T> mapSuccess(body: String): ApiResult<T> = ApiResult.Success(fromJson(body))
}
