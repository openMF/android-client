/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.navigation.rootnav

import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppSettings
import com.mifos.core.model.objects.users.User
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import template.core.base.ui.BaseViewModel
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.passcode.PasscodeManager
import com.mifos.core.data.repository.AppLockRepository

class RootNavViewModel(
    private val userDataRepository: UserPreferencesRepository,
    private val appLockRepository: AppLockRepository,
    private val passcodeManager: PasscodeManager,
    private val biometricStorageAdapter: BiometricStorageAdapter,
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
            RootNavAction.LogOutUser -> logOut()
        }
    }

    private fun handleUserStateUpdateReceive(
        action: RootNavAction.Internal.UserStateUpdateReceive,
    ) {
        val userData = action.userData

        when(userData.isAuthenticated){
            true -> {
                if(passcodeManager.state.value.loadedPasscode.isNullOrBlank()) {
                    logOut()
                } else {
                    mutableStateFlow.update { RootNavState.UserAuthenticated }
                }
            }
            false -> {
                mutableStateFlow.update { RootNavState.AuthenticateUser }
            }
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            biometricStorageAdapter.deleteRegistrationData()
            passcodeManager.logOut()
            userDataRepository.logOut()
            appLockRepository.deleteLock()

            mutableStateFlow.update { RootNavState.AuthenticateUser }
        }
    }
}

sealed class RootNavState {
    data object AuthenticateUser : RootNavState()

    data object SetLanguage : RootNavState()

    data object Splash : RootNavState()

    data object UserAuthenticated : RootNavState()
}

sealed interface RootNavAction {
    data object LogOutUser: RootNavAction

    sealed interface Internal {

        data class UserStateUpdateReceive(
            val userData: User,
            val settingsData: AppSettings,
        ): RootNavAction
    }
}
