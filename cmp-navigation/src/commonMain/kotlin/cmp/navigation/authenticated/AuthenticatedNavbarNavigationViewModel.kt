/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.navigation.authenticated

import androidx.lifecycle.viewModelScope
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class AuthenticatedNavbarNavigationViewModel(
    networkMonitor: NetworkMonitor,
) : BaseViewModel<AuthenticatedNavbarState, AuthenticatedNavBarEvent, AuthenticatedNavBarAction>(
    initialState = AuthenticatedNavbarState(),
) {

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    override fun handleAction(action: AuthenticatedNavBarAction) {
        when (action) {
            AuthenticatedNavBarAction.SearchTabClick -> handleSearchTabClicked()

            AuthenticatedNavBarAction.ClientTabClick -> handleClientTabClicked()

            AuthenticatedNavBarAction.CenterTabClick -> handleCenterTabClicked()

            AuthenticatedNavBarAction.GroupTabClick -> handleGroupsTabClicked()

            is AuthenticatedNavBarAction.OnRouteChanged -> updateRouteState(action.route)
        }
    }

    private fun updateRouteState(route: String?) {
        val safeRoute = route ?: ""
        val shouldShowTopBar = !safeRoute.contains("SearchRecord")
        val searchType = when {
            safeRoute.contains("ClientIdentifiersList") -> RecordType.IDENTIFIER.name
            else -> RecordType.ADDRESS.name
        }
        mutableStateFlow.update { currentState ->
            currentState.copy(
                isTopBarVisible = shouldShowTopBar,
                searchRecordType = searchType,
            )
        }
    }

    private fun handleSearchTabClicked() {
        sendEvent(AuthenticatedNavBarEvent.NavigateToSearchScreen)
    }

    private fun handleClientTabClicked() {
        sendEvent(AuthenticatedNavBarEvent.NavigateToClientScreen)
    }

    private fun handleCenterTabClicked() {
        sendEvent(AuthenticatedNavBarEvent.NavigateToCenterScreen)
    }

    private fun handleGroupsTabClicked() {
        sendEvent(AuthenticatedNavBarEvent.NavigateToGroupScreen)
    }
}

internal data class AuthenticatedNavbarState(
    val isTopBarVisible: Boolean = true,
    val searchRecordType: String = RecordType.ADDRESS.name,
)

internal sealed class AuthenticatedNavBarAction {

    data object SearchTabClick : AuthenticatedNavBarAction()

    data object ClientTabClick : AuthenticatedNavBarAction()

    data object CenterTabClick : AuthenticatedNavBarAction()

    data object GroupTabClick : AuthenticatedNavBarAction()

    data class OnRouteChanged(val route: String?) : AuthenticatedNavBarAction()
}

internal sealed class AuthenticatedNavBarEvent {

    abstract val tab: AuthenticatedNavBarTabItem

    data object NavigateToSearchScreen : AuthenticatedNavBarEvent() {
        override val tab: AuthenticatedNavBarTabItem = AuthenticatedNavBarTabItem.SearchTab
    }

    data object NavigateToClientScreen : AuthenticatedNavBarEvent() {
        override val tab: AuthenticatedNavBarTabItem = AuthenticatedNavBarTabItem.ClientTab
    }

    data object NavigateToCenterScreen : AuthenticatedNavBarEvent() {
        override val tab: AuthenticatedNavBarTabItem = AuthenticatedNavBarTabItem.CentersTab
    }

    data object NavigateToGroupScreen : AuthenticatedNavBarEvent() {
        override val tab: AuthenticatedNavBarTabItem = AuthenticatedNavBarTabItem.GroupsTab
    }
}
