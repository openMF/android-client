/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.generateCollectionSheet

import androidclient.feature.collectionsheet.generated.resources.Res
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_center
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_center_details
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_collection_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_group
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_office
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_productive_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_load_staff
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_submit_collection_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_failed_to_submit_productive_sheet
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.domain.useCases.FetchCenterDetailsUseCase
import com.mifos.core.domain.useCases.FetchCollectionSheetUseCase
import com.mifos.core.domain.useCases.FetchProductiveCollectionSheetUseCase
import com.mifos.core.domain.useCases.GetCentersInOfficeUseCase
import com.mifos.core.domain.useCases.GetGroupsByOfficeUseCase
import com.mifos.core.domain.useCases.GetStaffInOfficeUseCase
import com.mifos.core.domain.useCases.SubmitCollectionSheetUseCase
import com.mifos.core.domain.useCases.SubmitProductiveSheetUseCase
import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.room.entities.collectionsheet.CenterDetail
import com.mifos.room.entities.collectionsheet.CollectionSheetPayload
import com.mifos.room.entities.collectionsheet.CollectionSheetResponse
import com.mifos.room.entities.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

class GenerateCollectionSheetViewModel(
    private val newIndividualCollectionSheetRepository: NewIndividualCollectionSheetRepository,
    private val getStaffInOfficeUseCase: GetStaffInOfficeUseCase,
    private val getCentersInOfficeUseCase: GetCentersInOfficeUseCase,
    private val getGroupsByOfficeUseCase: GetGroupsByOfficeUseCase,
    private val fetchCenterDetailsUseCase: FetchCenterDetailsUseCase,
    private val fetchProductiveCollectionSheetUseCase: FetchProductiveCollectionSheetUseCase,
    private val fetchCollectionSheetUseCase: FetchCollectionSheetUseCase,
    private val submitProductiveSheetUseCase: SubmitProductiveSheetUseCase,
    private val submitCollectionSheetUseCase: SubmitCollectionSheetUseCase,
) : ViewModel() {

    private val uiStateInternal = MutableStateFlow<GenerateCollectionSheetUiState>(GenerateCollectionSheetUiState.Loading)
    val uiState = uiStateInternal.asStateFlow()

    private val _officeList = MutableStateFlow<List<OfficeEntity>>(emptyList())
    val officeList = _officeList.asStateFlow()

    private val _staffList = MutableStateFlow<List<StaffEntity>>(emptyList())
    val staffList = _staffList.asStateFlow()

    private val _centerList = MutableStateFlow<List<CenterEntity>>(emptyList())
    val centerList = _centerList.asStateFlow()

    private val _groupList = MutableStateFlow<List<GroupEntity>>(emptyList())
    val groupList = _groupList.asStateFlow()

    private val _collectionSheet = MutableStateFlow<CollectionSheetResponse?>(null)
    val collectionSheet = _collectionSheet.asStateFlow()

    private val _centerDetails = MutableStateFlow<List<CenterDetail>?>(null)
    val centerDetails = _centerDetails.asStateFlow()

    fun loadOffices() = launchWithStateHandling(
        loadingState = GenerateCollectionSheetUiState.Loading,
        errorMessage = Res.string.feature_collection_sheet_failed_to_load_office,
    ) {
        newIndividualCollectionSheetRepository.offices().collect { result ->
            when (result) {
                is DataState.Success -> {
                    _officeList.value = result.data ?: emptyList()
                    result.data?.firstOrNull()?.id?.let { officeId ->
                        loadStaffInOffice(officeId)
                    }
                }
                is DataState.Error -> emitError(Res.string.feature_collection_sheet_failed_to_load_office)
                else -> Unit
            }
        }
    }

    fun loadStaffInOffice(officeId: Int) = launchWithStateHandling(
        errorMessage = Res.string.feature_collection_sheet_failed_to_load_staff,
    ) {
        getStaffInOfficeUseCase(officeId).collect { result ->
            when (result) {
                is DataState.Success -> {
                    _staffList.value = result.data ?: emptyList()
                    val staffId = result.data?.firstOrNull()?.id ?: return@collect
                    loadCentersInOffice(officeId, staffId)
                    loadGroupsInOffice(officeId, staffId)
                }
                is DataState.Error -> emitError(Res.string.feature_collection_sheet_failed_to_load_staff)
                else -> Unit
            }
        }
    }

    fun loadCentersInOffice(officeId: Int, staffId: Int) = launchWithStateHandling(
        errorMessage = Res.string.feature_collection_sheet_failed_to_load_center,
    ) {
        getCentersInOfficeUseCase(officeId, buildParams(staffId)).collect { result ->
            if (result is DataState.Success) {
                _centerList.value = result.data
            } else if (result is DataState.Error) emitError(Res.string.feature_collection_sheet_failed_to_load_center)
        }
    }

    fun loadGroupsInOffice(officeId: Int, staffId: Int) = launchWithStateHandling(
        errorMessage = Res.string.feature_collection_sheet_failed_to_load_group,
    ) {
        getGroupsByOfficeUseCase(officeId, buildParams(staffId)).collect { result ->
            if (result is DataState.Success) {
                _groupList.value = result.data ?: emptyList()
                uiStateInternal.value = GenerateCollectionSheetUiState.Success
            } else if (result is DataState.Error) {
                emitError(Res.string.feature_collection_sheet_failed_to_load_group)
            }
        }
    }

    fun loadCenterDetails(meetingDate: String?, officeId: Int, staffId: Int) = launchWithStateHandling(
        errorMessage = Res.string.feature_collection_sheet_failed_to_load_center_details,
    ) {
        fetchCenterDetailsUseCase(
            Constants.DATE_FORMAT_LONG,
            Constants.LOCALE_EN,
            meetingDate,
            officeId,
            staffId,
        ).collect { result ->
            if (result is DataState.Success) {
                _centerDetails.value = result.data
            } else if (result is DataState.Error) emitError(Res.string.feature_collection_sheet_failed_to_load_center_details)
        }
    }

    fun loadProductiveCollectionSheet(centerId: Int, payload: CollectionSheetRequestPayload?) =
        launchWithStateHandling(Res.string.feature_collection_sheet_failed_to_load_productive_sheet) {
            fetchProductiveCollectionSheetUseCase(centerId, payload).collect { result ->
                if (result is DataState.Success) {
                    _collectionSheet.value = result.data
                } else if (result is DataState.Error) emitError(Res.string.feature_collection_sheet_failed_to_load_productive_sheet)
            }
        }

    fun loadCollectionSheet(groupId: Int, payload: CollectionSheetRequestPayload?) =
        launchWithStateHandling(Res.string.feature_collection_sheet_failed_to_load_collection_sheet) {
            fetchCollectionSheetUseCase(groupId, payload).collect { result ->
                if (result is DataState.Success) {
                    _collectionSheet.value = result.data
                } else if (result is DataState.Error) emitError(Res.string.feature_collection_sheet_failed_to_load_collection_sheet)
            }
        }

    fun submitProductiveSheet(centerId: Int, payload: ProductiveCollectionSheetPayload?) =
        launchWithStateHandling(Res.string.feature_collection_sheet_failed_to_submit_productive_sheet) {
            submitProductiveSheetUseCase(centerId, payload).collect { result ->
                uiStateInternal.value = when (result) {
                    is DataState.Success -> GenerateCollectionSheetUiState.ProductiveSheetSuccess
                    is DataState.Loading -> GenerateCollectionSheetUiState.Loading
                    is DataState.Error -> GenerateCollectionSheetUiState.Error(
                        getString(Res.string.feature_collection_sheet_failed_to_submit_productive_sheet),
                    )
                }
            }
        }

    fun submitCollectionSheet(groupId: Int, payload: CollectionSheetPayload?) =
        launchWithStateHandling(Res.string.feature_collection_sheet_failed_to_submit_collection_sheet) {
            submitCollectionSheetUseCase(groupId, payload).collect { result ->
                uiStateInternal.value = when (result) {
                    is DataState.Success -> GenerateCollectionSheetUiState.CollectionSheetSuccess
                    is DataState.Loading -> GenerateCollectionSheetUiState.Loading
                    is DataState.Error -> GenerateCollectionSheetUiState.Error(
                        getString(Res.string.feature_collection_sheet_failed_to_submit_collection_sheet),
                    )
                }
            }
        }

    private fun buildParams(staffId: Int): Map<String, String> = buildMap {
        put(LIMIT, "-1")
        put(ORDER_BY, ORDER_BY_FIELD_NAME)
        put(SORT_ORDER, ASCENDING)
        if (staffId >= 0) put(STAFF_ID, staffId.toString())
    }

    private suspend fun emitError(resId: StringResource) {
        uiStateInternal.value = GenerateCollectionSheetUiState.Error(getString(resId))
    }

    private fun launchWithStateHandling(
        errorMessage: StringResource? = null,
        loadingState: GenerateCollectionSheetUiState? = null,
        block: suspend CoroutineScope.() -> Unit,
    ) = viewModelScope.launch {
        loadingState?.let { uiStateInternal.value = it }
        try {
            block()
        } catch (e: Exception) {
            errorMessage?.let { uiStateInternal.value = GenerateCollectionSheetUiState.Error(getString(it)) }
        }
    }

    companion object {
        const val LIMIT = "limit"
        const val ORDER_BY = "orderBy"
        const val SORT_ORDER = "sortOrder"
        const val ASCENDING = "ASC"
        const val ORDER_BY_FIELD_NAME = "name"
        const val STAFF_ID = "staffId"
    }
}
