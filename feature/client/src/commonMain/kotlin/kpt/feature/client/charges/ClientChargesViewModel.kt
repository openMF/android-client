/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.client.charges

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.charge.ClientChargeRepository
import kpt.core.database.charge.entity.ChargesEntity

/**
 * Charges levied on one client — ported from
 * `6b66e8a43:feature/client/clientUpcomingCharges/ClientUpcomingChargesViewmodel.kt`.
 *
 * The original injected NOTHING: no repository, no use-case. Its state carried a `chargesFlow` that
 * some other screen was expected to have populated, plus a hand-rolled `isLoading` flag and a
 * `dialogState` for errors. That is three separate encodings of what `ScreenState` expresses once,
 * and it is why the screen could render "loading" forever with nothing fetching.
 *
 * Here the stream IS the state machine: `clientChargesStream` decides Loading / Content / Empty /
 * NoNetwork / Error from the Store, and the screen renders whatever it says.
 */
internal class ClientChargesViewModel(
    private val repository: ClientChargeRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<Unit, ClientChargesEvent, ClientChargesAction>(Unit) {

    /**
     * Read once from the route argument.
     *
     * The original took `resourceId: Int` + `resourceType: String` so one screen could serve
     * clients, loans and savings — but every caller passed `"clients"`, so the type parameter was a
     * hole rather than flexibility. Loan and savings charges get their own routes when those slices
     * land (docs.yaml#route.note).
     */
    private val clientId: Int = savedStateHandle.get<Int>(CLIENT_ID_KEY) ?: 0

    /**
     * Held, not collected inline — the error state's Retry has to reach the stream that failed.
     * A Retry wired to `{}` is the dead-clickable defect in its most misleading form: the control
     * looks like it works.
     */
    private val chargesStream = repository.clientChargesStream(clientId, viewModelScope)

    /**
     * The charges levied on this client. `stateIn(… WhileSubscribed …)`, not an `init{}` hand-lift
     * — see `ClientListViewModel.screenState` for why that kept dead subscriptions alive.
     */
    val charges: StateFlow<ScreenState<List<ChargesEntity>>> = chargesStream.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    override fun handleAction(action: ClientChargesAction) {
        when (action) {
            ClientChargesAction.Retry -> chargesStream.retry()
            ClientChargesAction.BackClicked -> sendEvent(ClientChargesEvent.NavigateBack)
        }
    }

    private companion object {
        /** Matches the route argument name in `ClientNavigation`. */
        const val CLIENT_ID_KEY = "clientId"
        const val TAG = "ClientChargesViewModel"

        /** Template default — long enough to survive a rotation or a brief tab switch. */
        const val STATE_TIMEOUT_MS = 5_000L
    }
}

internal sealed interface ClientChargesAction {
    data object Retry : ClientChargesAction
    data object BackClicked : ClientChargesAction
}

internal sealed interface ClientChargesEvent {
    data object NavigateBack : ClientChargesEvent
}
