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
import com.mifos.core.data.repository.AppLockRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppSettings
import com.mifos.core.model.objects.users.User
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.authenticator.passcode.PasscodeStorageAdapter

class RootNavViewModel(
    private val userDataRepository: UserPreferencesRepository,
    private val appLockRepository: AppLockRepository,
    private val passcodeManager: PasscodeManager,
    private val biometricStorageAdapter: BiometricStorageAdapter,
    private val passcodeStorageAdapter: PasscodeStorageAdapter,
) : BaseViewModel<RootNavState, Unit, RootNavAction>(
    initialState = RootNavState.Splash,
) {

    init {
        // One-time check: user reopened app without a passcode → force logout
        viewModelScope.launch {
            val userData = userDataRepository.userData.first()
            if (userData.isAuthenticated && passcodeStorageAdapter.loadPasscode().isNullOrBlank()) {
                logOut()
            }
        }

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
            RootNavAction.UnlockApp -> unlockApp()
        }
    }

    private fun handleUserStateUpdateReceive(
        action: RootNavAction.Internal.UserStateUpdateReceive,
    ) {
        val userData = action.userData

        when (userData.isAuthenticated) {
            true -> {
                if (!passcodeStorageAdapter.loadPasscode().isNullOrBlank()) {
                    mutableStateFlow.update { RootNavState.UserAuthenticated }
                }
            }
            false -> {
                mutableStateFlow.update { RootNavState.AuthenticateUser }
            }
        }
    }

    /**
     * Full-wipe logout. Runs the following in order:
     *  1. Clears the biometric registration blob (defense-in-depth; the
     *     library's `isRegistered` flow isn't updated via this path — fine
     *     because we're logging out and will re-init on next user session).
     *  2. Clears the saved passcode via [PasscodeManager.logOut].
     *  3. Clears the user session via `UserPreferencesRepository`.
     *  4. Clears the app-lock state via [AppLockRepository].
     *  5. Transitions the root nav back to [RootNavState.AuthenticateUser].
     *
     * Fires either from the explicit logout button (via
     * [RootNavAction.LogOutUser]) or from the init-time check that detects an
     * authenticated user with no passcode set.
     */
    private fun logOut() {
        viewModelScope.launch {
            biometricStorageAdapter.deleteRegistrationData()
            passcodeManager.logOut()
            userDataRepository.logOut()
            appLockRepository.deleteLock()

            mutableStateFlow.update { RootNavState.AuthenticateUser }
        }
    }

    private fun unlockApp() {
        appLockRepository.unlockApp()
    }
}

sealed class RootNavState {
    data object Splash : RootNavState()

    data object AuthenticateUser : RootNavState()

    data object UserAuthenticated : RootNavState()
}

sealed interface RootNavAction {
    /** Full-wipe logout; runs the sequence documented on [RootNavViewModel.logOut]. */
    data object LogOutUser : RootNavAction

    /** Marks the app as unlocked via [AppLockRepository.unlockApp]. Fired after biometric setup or skip. */
    data object UnlockApp : RootNavAction

    sealed interface Internal {

        data class UserStateUpdateReceive(
            val userData: User,
            val settingsData: AppSettings,
        ) : RootNavAction
    }
}
