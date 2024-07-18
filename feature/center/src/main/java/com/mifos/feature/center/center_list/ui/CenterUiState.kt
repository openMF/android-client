package com.mifos.feature.center.center_list.ui

import androidx.paging.PagingData
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class CenterListUiState {

    data object Loading : CenterListUiState()

    data class Error(val message: Int) : CenterListUiState()

    data class CenterList(val centers: Flow<PagingData<Center>>) : CenterListUiState()

    data class CenterListDb(val centers: List<Center>) : CenterListUiState()
}