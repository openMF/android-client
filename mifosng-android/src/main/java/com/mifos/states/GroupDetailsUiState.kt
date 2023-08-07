package com.mifos.states

import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.client.Client
import com.mifos.objects.group.Group

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class GroupDetailsUiState {

    data class ShowProgressbar(val state: Boolean) : GroupDetailsUiState()

    data class ShowFetchingError(val message: Int) : GroupDetailsUiState()

    data class ShowGroup(val group: Group?) : GroupDetailsUiState()

    data class ShowGroupAccounts(val groupAccounts: GroupAccounts?) : GroupDetailsUiState()

    data class ShowGroupClients(val clientMembers: List<Client>) : GroupDetailsUiState()
}