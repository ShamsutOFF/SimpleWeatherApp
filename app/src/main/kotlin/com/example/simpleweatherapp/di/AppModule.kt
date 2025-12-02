package com.example.simpleweatherapp.di

import com.example.simpleweatherapp.network.BaseNetworkClient
import com.example.simpleweatherapp.network.WeatherApiService
import com.example.simpleweatherapp.vm.MainScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule =
    module {
        // Network
        single { BaseNetworkClient() }
        single { WeatherApiService(client = get()) }

        // ViewModels
        viewModel { MainScreenViewModel(weatherApi = get()) }
    }
