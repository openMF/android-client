package com.mifos.feature.loan.ClientCollateral

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository

import com.mifos.core.network.model.CollateralItem
import com.mifos.feature.loan.ClientCollateral.ClientCollateralUiState.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClientCollateralViewModel(
    private val savedStateHandle: SavedStateHandle, // Assuming we might need clientId/groupId from nav args
    private val clientDetailsRepository: ClientDetailsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClientCollateralUiState>(ClientCollateralUiState.Loading)
    val uiState: StateFlow<ClientCollateralUiState> = _uiState.asStateFlow()


    private val clientId: Int? = savedStateHandle.get<Int>("clientIdKey")

    init {
        loadCollateralItems()
    }

    fun loadCollateralItems() {
        if (clientId == null) {
            _uiState.value = ClientCollateralUiState.Error("Client ID not found")
            return
        }

        viewModelScope.launch {
            _uiState.value = ClientCollateralUiState.Loading
            when (val result = clientDetailsRepository.getCollateralItems()) {
                is DataState.Success -> {
                    val networkItems = result.data
                    if (networkItems.isEmpty()) {
                        _uiState.value = ClientCollateralUiState.Empty
                    } else {
                        val displayItems = networkItems.mapNotNull { transformToDisplayItem(it) }
                        if (displayItems.isEmpty() && networkItems.isNotEmpty()) {
                            // This case means all items failed to parse quantity, which is an error
                             _uiState.value = Error("Error parsing collateral data")
                        } else {
                             _uiState.value = Success(displayItems, displayItems.size)
                        }
                    }
                }
                is DataState.Error -> {
                    _uiState.value = Error(result.exception.message ?: "Unknown error")
                }

                DataState.Loading -> TODO()
            }
        }
    }

    private fun transformToDisplayItem(networkItem: CollateralItem): CollateralDisplayItem? {
        val quantity = networkItem.quality.toIntOrNull()
        return if (quantity != null) {
            CollateralDisplayItem(
                id = networkItem.id,
                typeName = networkItem.name,
                quantity = quantity,
                unitValue = networkItem.basePrice,
                totalCollateralValue = quantity * networkItem.basePrice
            )
        } else {

            null
        }
    }
}
