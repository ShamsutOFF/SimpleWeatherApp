package com.example.simpleweatherapp.vm

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.network.ApiResult
import com.example.simpleweatherapp.network.WeatherApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainScreenViewModel(
    private val weatherApi: WeatherApiService,
) : ViewModel() {
    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Idle)
    val state: StateFlow<MainScreenState> = _state

    init {
        processIntent(intent = MainScreenIntent.GetWeather)
    }

    @Stable
    sealed interface MainScreenState {
        object Idle : MainScreenState

        object Loading : MainScreenState

        data class WeatherLoaded(
            val weather: String,
        ) : MainScreenState

        data class Error(
            val title: String,
            val message: String,
        ) : MainScreenState
    }

    sealed class MainScreenIntent {
        object GetWeather : MainScreenIntent()
    }

    fun processIntent(intent: MainScreenIntent) {
        when (intent) {
            MainScreenIntent.GetWeather -> {
                getWeather()
            }
        }
    }

    private fun getWeather() {
        Timber.d("@@@ getWeather()")
        _state.value = MainScreenState.Loading
        viewModelScope.launch {
            val result = weatherApi.getWeather()
            Timber.d("@@@ getWeather() result: $result")
            _state.value =
                when (result) {
                    is ApiResult.Success -> MainScreenState.WeatherLoaded(result.data)
                    is ApiResult.Error ->
                        MainScreenState.Error(
                            title = result.title,
                            message = result.message,
                        )
                }
        }
    }
}