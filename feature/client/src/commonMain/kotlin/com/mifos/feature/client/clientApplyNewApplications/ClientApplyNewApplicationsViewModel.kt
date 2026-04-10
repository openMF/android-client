/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientApplyNewApplications

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mifos.core.ui.util.BaseViewModel

class ClientApplyNewApplicationsViewModel(
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClientApplyNewApplicationsState, ClientApplyNewApplicationsEvent, ClientApplyNewApplicationsAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<ClientApplyNewApplicationRoute>()
        ClientApplyNewApplicationsState(
            clientId = route.clientId,
            status = route.status,
            accountNo = route.accountNo,
        )
    },
) {
    override fun handleAction(action: ClientApplyNewApplicationsAction) {
        when (action) {
            ClientApplyNewApplicationsAction.NavigateBack -> sendEvent(
                ClientApplyNewApplicationsEvent.NavigateBack,
            )

            is ClientApplyNewApplicationsAction.OnActionClick -> sendEvent(
                ClientApplyNewApplicationsEvent.OnActionClick(action.action),
            )
        }
    }
}

data class ClientApplyNewApplicationsState(
    val clientId: Int,
    val accountNo: String,
    val status: String,
)

sealed interface ClientApplyNewApplicationsEvent {
    data object NavigateBack : ClientApplyNewApplicationsEvent
    data class OnActionClick(val action: ClientApplyNewApplicationsItem) :
        ClientApplyNewApplicationsEvent
}

sealed interface ClientApplyNewApplicationsAction {
    data object NavigateBack : ClientApplyNewApplicationsAction
    data class OnActionClick(val action: ClientApplyNewApplicationsItem) :
        ClientApplyNewApplicationsAction
}
