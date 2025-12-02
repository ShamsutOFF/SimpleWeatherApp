package com.example.simpleweatherapp.network

import com.example.simpleweatherapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.takeFrom
import io.ktor.serialization.gson.gson
import timber.log.Timber

class BaseNetworkClient {
    val instance by lazy {
        HttpClient(engineFactory = OkHttp) {

            install(plugin = HttpTimeout) {
                requestTimeoutMillis = 30_000
            }

            install(plugin = ContentNegotiation) {
                gson {
//                    setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    disableHtmlEscaping()
                }
            }

            if (BuildConfig.DEBUG) {
                install(plugin = Logging) {
                    level = LogLevel.ALL
                    logger =
                        object : Logger {
                            override fun log(message: String) {
                                Timber.tag(tag = "KTOR").d(message)
                            }
                        }
                }
            }

            defaultRequest {
                url.takeFrom(urlString = "${BuildConfig.BASE_URL}/v1/")
                header("Accept", "application/json")
            }
        }
    }
}
