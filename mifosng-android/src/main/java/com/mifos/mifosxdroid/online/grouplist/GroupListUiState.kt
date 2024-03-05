package com.mifos.mifosxdroid.online.grouplist

import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.GroupWithAssociations

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class GroupListUiState {

    data class ShowProgress(val state: Boolean) : GroupListUiState()

    data class ShowFetchingError(val message: String) : GroupListUiState()

    data class ShowGroups(val groupWithAssociations: GroupWithAssociations?) : GroupListUiState()

    data class ShowGroupList(val centerWithAssociations: CenterWithAssociations?) :
        GroupListUiState()

}