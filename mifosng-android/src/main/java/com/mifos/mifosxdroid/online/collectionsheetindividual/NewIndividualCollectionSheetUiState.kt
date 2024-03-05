package com.mifos.mifosxdroid.online.collectionsheetindividual

import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class NewIndividualCollectionSheetUiState {

    data object ShowProgressbar : NewIndividualCollectionSheetUiState()

    data object ShowSuccess : NewIndividualCollectionSheetUiState()

    data class ShowError(val errorMessage: String?) : NewIndividualCollectionSheetUiState()

    data class ShowSheet(val individualCollectionSheet: IndividualCollectionSheet?) :
        NewIndividualCollectionSheetUiState()

    data object ShowNoSheetFound : NewIndividualCollectionSheetUiState()

    data class SetOfficeSpinner(val officeList: List<Office>) :
        NewIndividualCollectionSheetUiState()

    data class SetStaffSpinner(val staffList: List<Staff>) : NewIndividualCollectionSheetUiState()
}