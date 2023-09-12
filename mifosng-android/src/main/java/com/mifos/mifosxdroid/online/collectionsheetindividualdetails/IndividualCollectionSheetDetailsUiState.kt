package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class IndividualCollectionSheetDetailsUiState {

    object ShowProgressbar : IndividualCollectionSheetDetailsUiState()

    data class ShowError(val message: String) : IndividualCollectionSheetDetailsUiState()

    object ShowSuccess : IndividualCollectionSheetDetailsUiState()
}