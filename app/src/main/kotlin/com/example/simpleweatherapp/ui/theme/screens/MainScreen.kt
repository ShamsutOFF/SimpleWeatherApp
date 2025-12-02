package com.example.simpleweatherapp.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.simpleweatherapp.ui.theme.ErrorDialog
import com.example.simpleweatherapp.ui.theme.FullScreenLoader
import com.example.simpleweatherapp.vm.MainScreenViewModel
import com.example.simpleweatherapp.vm.MainScreenViewModel.MainScreenIntent
import com.example.simpleweatherapp.vm.MainScreenViewModel.MainScreenState
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<MainScreenViewModel>()
    val state by viewModel.state.collectAsState()

    when (val current = state) {
        is MainScreenState.Idle -> MainScreenContent(modifier = modifier)

        is MainScreenState.Loading -> FullScreenLoader()

        is MainScreenState.WeatherLoaded -> MainScreenContent(
            modifier = modifier,
            weather = current.weather
        )

        is MainScreenState.Error -> ErrorDialog(
            title = current.title,
            text = current.message,
            onConfirm = {
                viewModel.processIntent(intent = MainScreenIntent.GetWeather)
            },
        )
    }

}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    weather: String = ""
) {
    Column(modifier = modifier) {
        if (weather.isNotEmpty()) Text(weather) else Text("Погода еще не загружена")
    }
}