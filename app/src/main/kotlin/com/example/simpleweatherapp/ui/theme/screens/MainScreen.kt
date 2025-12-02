package com.example.simpleweatherapp.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.simpleweatherapp.models.WeatherResponse
import com.example.simpleweatherapp.ui.theme.ErrorDialog
import com.example.simpleweatherapp.ui.theme.FullScreenLoader
import com.example.simpleweatherapp.vm.MainScreenViewModel
import com.example.simpleweatherapp.vm.MainScreenViewModel.MainScreenIntent
import com.example.simpleweatherapp.vm.MainScreenViewModel.MainScreenState
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<MainScreenViewModel>()
    val state by viewModel.state.collectAsState()

    when (val current = state) {

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
    weather: WeatherResponse
) {
    val current = weather.current
    val forecast = weather.forecast.forecastDays

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Текущая погода
        WeatherCard(
            modifier = Modifier.fillMaxWidth(),
            location = "${weather.location.name}, ${weather.location.country}",
            temperature = "${current.tempC.toInt()}°",
            condition = current.condition.text,
            feelsLike = "${current.feelslikeC.toInt()}°",
            humidity = "${current.humidity}%",
            windSpeed = "${current.windKph.toInt()} км/ч",
            iconUrl = "https:${current.condition.icon}"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Прогноз на 3 дня
        Text(
            text = "Прогноз на 3 дня",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(forecast) { day ->
                ForecastDayCard(
                    date = day.date,
                    maxTemp = day.day.maxTempC.toInt(),
                    minTemp = day.day.minTempC.toInt(),
                    condition = day.day.condition.text,
                    rainChance = day.day.rainChance,
                    iconUrl = "https:${day.day.condition.icon}"
                )
            }
        }

        // Почасовой прогноз (первые 8 часов)
        if (forecast.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Почасовой прогноз",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val todayHours = forecast[0].hours.take(8)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(todayHours) { hour ->
                    HourlyForecastCard(
                        time = hour.time.substring(11, 16), // "14:00"
                        temp = hour.tempC.toInt(),
                        iconUrl = "https:${hour.condition.icon}"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    modifier: Modifier = Modifier,
    location: String,
    temperature: String,
    condition: String,
    feelsLike: String,
    humidity: String,
    windSpeed: String,
    iconUrl: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Местоположение
            Text(
                text = location,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Температура
                Text(
                    text = temperature,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Иконка погоды
                AsyncImage(
                    model = iconUrl,
                    contentDescription = condition,
                    modifier = Modifier.size(64.dp)
                )
            }

            // Состояние
            Text(
                text = condition,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Детали
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(
                    icon = Icons.Default.Thermostat,
                    title = "Ощущается",
                    value = feelsLike
                )
                WeatherDetail(
                    icon = Icons.Default.WaterDrop,
                    title = "Влажность",
                    value = humidity
                )
                WeatherDetail(
                    icon = Icons.Default.Air,
                    title = "Ветер",
                    value = windSpeed
                )
            }
        }
    }
}

@Composable
fun ForecastDayCard(
    date: String,
    maxTemp: Int,
    minTemp: Int,
    condition: String,
    rainChance: Int,
    iconUrl: String
) {
    val formattedDate = remember {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("E, d MMM", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate ?: date)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.width(120.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = iconUrl,
                contentDescription = condition,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = condition,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$maxTemp° / $minTemp°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            if (rainChance > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$rainChance%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyForecastCard(
    time: String,
    temp: Int,
    iconUrl: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${temp}°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun WeatherDetail(
    icon: ImageVector,
    title: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}