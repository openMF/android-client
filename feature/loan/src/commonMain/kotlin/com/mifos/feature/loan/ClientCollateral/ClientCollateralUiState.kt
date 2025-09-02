package com.mifos.feature.loan.ClientCollateral



data class CollateralDisplayItem(
    val id: Int,
    val typeName: String,
    val quantity: Int,
    val unitValue: Double,
    val totalCollateralValue: Double
)
sealed interface ClientCollateralUiState {
    data object Loading : ClientCollateralUiState
    data class Error(val message: String) : ClientCollateralUiState
    data object Empty : ClientCollateralUiState
    data class Success(val items: List<CollateralDisplayItem>, val totalItems: Int) : ClientCollateralUiState
}

