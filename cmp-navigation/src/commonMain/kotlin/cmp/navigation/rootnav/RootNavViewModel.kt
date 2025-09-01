/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.rootnav

import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppSettings
import com.mifos.core.model.objects.users.User
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class RootNavViewModel(
    userDataRepository: UserPreferencesRepository,
) : BaseViewModel<RootNavState, Unit, RootNavAction>(
    initialState = RootNavState.Splash,
) {

    init {
        combine(
            userDataRepository.userData,
            userDataRepository.settingsInfo,
        ) { authState, settingsData ->
            RootNavAction.Internal.UserStateUpdateReceive(
                userData = authState,
                settingsData = settingsData,
            )
        }.onEach(::handleAction)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: RootNavAction) {
        when (action) {
            is RootNavAction.Internal.UserStateUpdateReceive -> handleUserStateUpdateReceive(action)
        }
    }

    private fun handleUserStateUpdateReceive(
        action: RootNavAction.Internal.UserStateUpdateReceive,
    ) {
        val settingsData = action.settingsData
        val userData = action.userData
        val updatedRootNavState = when {
            !userData.isAuthenticated -> RootNavState.Auth

            settingsData.passcode?.isEmpty() ?: false -> RootNavState.UserLocked

            else -> RootNavState.UserUnlocked
        }
        mutableStateFlow.update { updatedRootNavState }
    }
}

sealed class RootNavState {
    data object Auth : RootNavState()

    data object SetLanguage : RootNavState()

    data object Splash : RootNavState()

    data object UserLocked : RootNavState()

    data object UserUnlocked : RootNavState()
}

sealed class RootNavAction {

    sealed class Internal {

        data class UserStateUpdateReceive(
            val userData: User,
            val settingsData: AppSettings,
        ) : RootNavAction()
    }
}
