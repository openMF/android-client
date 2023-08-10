package com.mifos.states

import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class NewIndividualCollectionSheetUiState {

    object ShowProgressbar : NewIndividualCollectionSheetUiState()

    object ShowSuccess : NewIndividualCollectionSheetUiState()

    data class ShowError(val errorMessage: String?) : NewIndividualCollectionSheetUiState()

    data class ShowSheet(val individualCollectionSheet: IndividualCollectionSheet?) :
        NewIndividualCollectionSheetUiState()

    object ShowNoSheetFound : NewIndividualCollectionSheetUiState()

    data class SetOfficeSpinner(val officeList: List<Office>) :
        NewIndividualCollectionSheetUiState()

    data class SetStaffSpinner(val staffList: List<Staff>) : NewIndividualCollectionSheetUiState()
}