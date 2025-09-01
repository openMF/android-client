/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientApplyNewApplications

import com.mifos.core.ui.util.BaseViewModel

class ClientApplyNewApplicationsViewModel() : BaseViewModel<ClientApplyNewApplicationsState, ClientApplyNewApplicationsEvent, ClientApplyNewApplicationsAction>(
    initialState = ClientApplyNewApplicationsState(),
) {
    override fun handleAction(action: ClientApplyNewApplicationsAction) {
        when (action) {
            ClientApplyNewApplicationsAction.NavigateBack -> sendEvent(ClientApplyNewApplicationsEvent.NavigateBack)
            is ClientApplyNewApplicationsAction.OnActionClick -> sendEvent(ClientApplyNewApplicationsEvent.OnActionClick(action.action))
        }
    }
}

data class ClientApplyNewApplicationsState(
    val clientId: Int = -1,
)

sealed interface ClientApplyNewApplicationsEvent {
    data object NavigateBack : ClientApplyNewApplicationsEvent
    data class OnActionClick(val action: ClientApplyNewApplicationsItem) : ClientApplyNewApplicationsEvent
}

sealed interface ClientApplyNewApplicationsAction {
    data object NavigateBack : ClientApplyNewApplicationsAction
    data class OnActionClick(val action: ClientApplyNewApplicationsItem) : ClientApplyNewApplicationsAction
}
