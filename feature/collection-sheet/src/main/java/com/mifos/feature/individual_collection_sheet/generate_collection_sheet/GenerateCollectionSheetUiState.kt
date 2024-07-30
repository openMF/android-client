package com.mifos.feature.individual_collection_sheet.generate_collection_sheet

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class GenerateCollectionSheetUiState {

    data object Loading : GenerateCollectionSheetUiState()

    data class Error(val message: Int) : GenerateCollectionSheetUiState()

    data object Success : GenerateCollectionSheetUiState()

    data object CollectionSheetSuccess : GenerateCollectionSheetUiState()

    data object ProductiveSheetSuccess : GenerateCollectionSheetUiState()
}