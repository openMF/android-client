package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class IndividualCollectionSheetDetailsUiState {

    data object ShowProgressbar : IndividualCollectionSheetDetailsUiState()

    data class ShowError(val message: String) : IndividualCollectionSheetDetailsUiState()

    data object ShowSuccess : IndividualCollectionSheetDetailsUiState()
}