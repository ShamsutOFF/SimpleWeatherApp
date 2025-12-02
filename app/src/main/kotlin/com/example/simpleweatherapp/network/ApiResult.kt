package com.example.simpleweatherapp.network

sealed class ApiResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : ApiResult<T>()

    data class Error(
        val title: String,
        val message: String,
        val code: Int? = null,
    ) : ApiResult<Nothing>()
}