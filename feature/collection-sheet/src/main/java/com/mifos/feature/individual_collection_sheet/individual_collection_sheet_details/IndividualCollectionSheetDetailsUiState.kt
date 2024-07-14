package com.mifos.feature.individual_collection_sheet.individual_collection_sheet_details

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class IndividualCollectionSheetDetailsUiState {

    data object Empty : IndividualCollectionSheetDetailsUiState()

    data object Loading : IndividualCollectionSheetDetailsUiState()

    data class Error(val message: Int) : IndividualCollectionSheetDetailsUiState()

    data object SavedSuccessfully : IndividualCollectionSheetDetailsUiState()
}