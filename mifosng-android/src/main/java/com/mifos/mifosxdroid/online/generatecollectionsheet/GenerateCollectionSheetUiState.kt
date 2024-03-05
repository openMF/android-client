package com.mifos.mifosxdroid.online.generatecollectionsheet

import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff

/**
 * Created by Aditya Gupta on 12/08/23.
 */
sealed class GenerateCollectionSheetUiState {

    data object ShowProgressbar : GenerateCollectionSheetUiState()

    data class ShowError(val message: String) : GenerateCollectionSheetUiState()

    data class ShowOffices(val offices: List<Office>) : GenerateCollectionSheetUiState()

    data class ShowStaffInOffice(val staffs: List<Staff>, val officeId: Int) :
        GenerateCollectionSheetUiState()

    data class ShowCentersInOffice(val centers: List<Center>) : GenerateCollectionSheetUiState()

    data class ShowGroupsInOffice(val groups: List<Group>) : GenerateCollectionSheetUiState()

    data class ShowGroupByCenter(val centerWithAssociations: CenterWithAssociations) :
        GenerateCollectionSheetUiState()

    data class OnCenterLoadSuccess(val centerDetails: List<CenterDetail>) :
        GenerateCollectionSheetUiState()

    data class ShowProductive(val collectionSheetResponse: CollectionSheetResponse) :
        GenerateCollectionSheetUiState()

    data class ShowCollection(val collectionSheetResponse: CollectionSheetResponse) :
        GenerateCollectionSheetUiState()
}