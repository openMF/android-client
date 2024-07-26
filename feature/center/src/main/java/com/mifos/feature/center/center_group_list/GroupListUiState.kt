package com.mifos.feature.center.center_group_list

import com.mifos.core.objects.group.CenterWithAssociations

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class GroupListUiState {

    data object Loading : GroupListUiState()

    data class Error(val message: Int) : GroupListUiState()

    data class GroupList(val centerWithAssociations: CenterWithAssociations) : GroupListUiState()
}