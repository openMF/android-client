package com.mifos.feature.individual_collection_sheet.new_individual_collection_sheet.ui

import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff

/**
 * Created by Aditya Gupta on 10/08/23.
 */
data class NewIndividualCollectionSheetUiState(

    val isLoading: Boolean = false,

    val error: String? = null,

    val officeList: List<Office> = emptyList(),

    val staffList: List<Staff> = emptyList(),

    val individualCollectionSheet: IndividualCollectionSheet? = null
)