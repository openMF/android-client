package com.mifos.feature.individual_collection_sheet.generate_collection_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.FetchCenterDetailsUseCase
import com.mifos.core.domain.use_cases.FetchCollectionSheetUseCase
import com.mifos.core.domain.use_cases.FetchProductiveCollectionSheetUseCase
import com.mifos.core.domain.use_cases.GetCentersInOfficeUseCase
import com.mifos.core.domain.use_cases.GetGroupsByOfficeUseCase
import com.mifos.core.domain.use_cases.GetOfficeListUseCase
import com.mifos.core.domain.use_cases.GetStaffInOfficeUseCase
import com.mifos.core.domain.use_cases.SubmitCollectionSheetUseCase
import com.mifos.core.domain.use_cases.SubmitProductiveSheetUseCase
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.feature.collection_sheet.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateCollectionSheetViewModel @Inject constructor(
    private val getOfficeListUseCase: GetOfficeListUseCase,
    private val getStaffInOfficeUseCase: GetStaffInOfficeUseCase,
    private val getCentersInOfficeUseCase: GetCentersInOfficeUseCase,
    private val getGroupsByOfficeUseCase: GetGroupsByOfficeUseCase,
    private val fetchCenterDetailsUseCase: FetchCenterDetailsUseCase,
    private val fetchProductiveCollectionSheetUseCase: FetchProductiveCollectionSheetUseCase,
    private val fetchCollectionSheetUseCase: FetchCollectionSheetUseCase,
    private val submitProductiveSheetUseCase: SubmitProductiveSheetUseCase,
    private val submitCollectionSheetUseCase: SubmitCollectionSheetUseCase
) : ViewModel() {

    private val _generateCollectionSheetUiState =
        MutableStateFlow<GenerateCollectionSheetUiState>(GenerateCollectionSheetUiState.Loading)
    val generateCollectionSheetUiState = _generateCollectionSheetUiState.asStateFlow()

    private val _officeListState = MutableStateFlow<List<Office>>(emptyList())
    val officeListState = _officeListState.asStateFlow()

    private val _staffListState = MutableStateFlow<List<Staff>>(emptyList())
    val staffListState = _staffListState.asStateFlow()

    private val _centerListState = MutableStateFlow<List<Center>>(emptyList())
    val centerListState = _centerListState.asStateFlow()

    private val _groupListState = MutableStateFlow<List<Group>>(emptyList())
    val groupListState = _groupListState.asStateFlow()

    private val _collectionSheetState = MutableStateFlow<CollectionSheetResponse?>(null)
    val collectionSheetState = _collectionSheetState.asStateFlow()

    private val _centerDetailsState = MutableStateFlow<List<CenterDetail>?>(null)
    val centerDetailsState = _centerDetailsState.asStateFlow()

    fun loadOffices() = viewModelScope.launch(Dispatchers.IO) {
        getOfficeListUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _generateCollectionSheetUiState.value =
                    GenerateCollectionSheetUiState.Error(
                        R.string.feature_collection_sheet_failed_to_load_office
                    )

                is Resource.Loading -> _generateCollectionSheetUiState.value =
                    GenerateCollectionSheetUiState.Loading

                is Resource.Success -> {
                    _officeListState.value = result.data ?: emptyList()
                    if (result.data?.isNotEmpty() == true) loadStaffInOffice(
                        result.data?.get(0)?.id ?: -1
                    )
                }
            }
        }
    }

    fun loadStaffInOffice(officeId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getStaffInOfficeUseCase(officeId).collect { result ->
            when (result) {
                is Resource.Error -> _generateCollectionSheetUiState.value =
                    GenerateCollectionSheetUiState.Error(
                        R.string.feature_collection_sheet_failed_to_load_staff
                    )

                is Resource.Loading -> Unit

                is Resource.Success -> {
                    _staffListState.value = result.data ?: emptyList()
                    if (result.data?.isNotEmpty() == true) {
                        loadCentersInOffice(
                            officeId,
                            result.data?.get(0)?.id ?: -1
                        )
                        loadGroupsInOffice(
                            officeId,
                            result.data?.get(0)?.id ?: -1
                        )
                    }
                }
            }
        }
    }

    fun loadCentersInOffice(id: Int, staffId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val params: MutableMap<String, String> = HashMap()
            params[LIMIT] = "-1"
            params[ORDER_BY] = ORDER_BY_FIELD_NAME
            params[SORT_ORDER] = ASCENDING
            if (staffId >= 0) {
                params[STAFF_ID] = staffId.toString()
            }
            getCentersInOfficeUseCase(id, params).collect { result ->
                when (result) {
                    is Resource.Error -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.Error(
                            R.string.feature_collection_sheet_failed_to_load_center
                        )

                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        _centerListState.value = result.data ?: emptyList()
                    }
                }
            }
        }

    fun loadGroupsInOffice(office: Int, staffId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            val params: MutableMap<String, String> = HashMap()
            params[LIMIT] = "-1"
            params[ORDER_BY] = ORDER_BY_FIELD_NAME
            params[SORT_ORDER] = ASCENDING
            if (staffId >= 0) params[STAFF_ID] = staffId.toString()

            getGroupsByOfficeUseCase(office, params).collect { result ->
                when (result) {
                    is Resource.Error -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.Error(
                            R.string.feature_collection_sheet_failed_to_load_group
                        )

                    is Resource.Loading -> Unit

                    is Resource.Success -> {
                        _groupListState.value = result.data ?: emptyList()
                        _generateCollectionSheetUiState.value =
                            GenerateCollectionSheetUiState.Success
                    }
                }
            }
        }

    fun loadCenterDetails(
        meetingDate: String?,
        officeId: Int, staffId: Int
    ) = viewModelScope.launch(Dispatchers.IO) {
        fetchCenterDetailsUseCase(
            Constants.DATE_FORMAT_LONG, Constants.LOCALE_EN,
            meetingDate,
            officeId,
            staffId
        ).collect { result ->
            when (result) {
                is Resource.Error -> _generateCollectionSheetUiState.value =
                    GenerateCollectionSheetUiState.Error(
                        R.string.feature_collection_sheet_failed_to_load_center_details
                    )

                is Resource.Loading -> Unit

                is Resource.Success -> _centerDetailsState.value = result.data
            }
        }
    }

    fun loadProductiveCollectionSheet(
        centerId: Int,
        payload: CollectionSheetRequestPayload?
    ) = viewModelScope.launch(Dispatchers.IO) {
        fetchProductiveCollectionSheetUseCase(centerId, payload).collect {
            when (it) {
                is Resource.Error -> _generateCollectionSheetUiState.value =
                    GenerateCollectionSheetUiState.Error(
                        R.string.feature_collection_sheet_failed_to_load_productive_sheet
                    )

                is Resource.Loading -> Unit

                is Resource.Success -> _collectionSheetState.value = it.data
            }
        }
    }

    fun loadCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?
    ) = viewModelScope.launch(Dispatchers.IO) {
        fetchCollectionSheetUseCase(groupId, payload).collect {
            when (it) {
                is Resource.Error -> _generateCollectionSheetUiState.value =
                    GenerateCollectionSheetUiState.Error(
                        R.string.feature_collection_sheet_failed_to_load_collection_sheet
                    )

                is Resource.Loading -> Unit

                is Resource.Success -> _collectionSheetState.value = it.data
            }
        }
    }


    fun submitProductiveSheet(centerId: Int, payload: ProductiveCollectionSheetPayload?) =
        viewModelScope.launch(Dispatchers.IO) {
            submitProductiveSheetUseCase(centerId, payload).collect {
                when (it) {
                    is Resource.Error -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.Error(
                            R.string.feature_collection_sheet_failed_to_submit_productive_sheet
                        )

                    is Resource.Loading -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.Loading

                    is Resource.Success -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ProductiveSheetSuccess
                }
            }
        }

    fun submitCollectionSheet(groupId: Int, payload: CollectionSheetPayload?) =
        viewModelScope.launch(Dispatchers.IO) {
            submitCollectionSheetUseCase(groupId, payload).collect {
                when (it) {
                    is Resource.Error -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.Error(
                            R.string.feature_collection_sheet_failed_to_submit_collection_sheet
                        )

                    is Resource.Loading -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.Loading

                    is Resource.Success -> _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.CollectionSheetSuccess
                }
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