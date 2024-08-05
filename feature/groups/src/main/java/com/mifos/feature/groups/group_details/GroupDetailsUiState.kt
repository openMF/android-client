package com.mifos.feature.groups.group_details

import com.mifos.core.objects.group.Group

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class GroupDetailsUiState {

    data object Loading : GroupDetailsUiState()

    data class Error(val message: Int) : GroupDetailsUiState()

    data class ShowGroup(val group: Group) : GroupDetailsUiState()
}