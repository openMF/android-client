package com.mifos.feature.about

sealed class AboutUiState {

    data object Loading : AboutUiState()

    data class Error(val message: Int) : AboutUiState()

    data class AboutOptions(val aboutOptions: List<AboutItem>) : AboutUiState()
}