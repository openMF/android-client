/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.profile.identity

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.auth.AuthCommandRepository
import kpt.core.datastore.prefs.ProjectPreferencesRepository

/**
 * Signed-in officer identity and sign-out — the Profile tab's content.
 *
 * There is no original to port: the field-officer app had no Profile tab. The template backbone
 * always renders one (spec §6), so this is a deliberate NEW surface, kept to identity plus sign-out
 * so "new surface inside a migration" stays defensible (`feature/profile/MIGRATION.md` §7).
 *
 * Identity is read from the session persisted at login; sign-out goes through
 * `AuthCommandRepository.signOut`, which owns what is removed.
 */
class ProfileViewModel(
    private val project: ProjectPreferencesRepository,
    private val auth: AuthCommandRepository,
) : BaseViewModel<ProfileState, ProfileEvent, ProfileAction>(ProfileState()) {

    init {
        // fineractUser is a StateFlow persisted at sign-in — readable with no network.
        viewModelScope.launch {
            project.fineractUser.collect { fineract ->
                mutableStateFlow.value = state.copy(
                    username = fineract.username.orEmpty(),
                    officeName = fineract.officeName.orEmpty(),
                    instanceUrl = project.instanceUrl,
                )
            }
        }
    }

    override fun handleAction(action: ProfileAction) {
        when (action) {
            // Every opening starts on the safe choice — a previous "clear data" selection must not
            // carry over into a later sign-out the officer did not mean to be destructive.
            ProfileAction.SignOutRequested ->
                mutableStateFlow.value = state.copy(
                    confirmingSignOut = true,
                    signOutMode = SignOutMode.SessionOnly,
                    signOutFailed = false,
                )

            ProfileAction.SignOutCancelled ->
                mutableStateFlow.value = state.copy(confirmingSignOut = false, signOutFailed = false)

            is ProfileAction.SignOutModeSelected ->
                mutableStateFlow.value = state.copy(signOutMode = action.mode)

            ProfileAction.SignOutConfirmed -> signOut()
        }
    }

    /**
     * Sign out through `core/data` — this ViewModel used to clear the datastore itself.
     *
     * Local only, so it works offline (a field officer who cannot sign out without signal is
     * stranded, `MIGRATION.md` §4). What goes is the officer's choice in the dialog: the session
     * only, or the session and everything stored on the device — see [SignOutMode].
     */
    private fun signOut() {
        if (state.isSigningOut) return
        mutableStateFlow.value = state.copy(isSigningOut = true, signOutFailed = false)
        viewModelScope.launch {
            val result = auth.signOut(clearLocalData = state.signOutMode == SignOutMode.SessionAndData)
            if (result is MutationResult.Applied) {
                mutableStateFlow.value = state.copy(confirmingSignOut = false, isSigningOut = false)
                sendEvent(ProfileEvent.SignedOut)
            } else {
                // Still signed in, data intact: the purge runs before the session is cleared.
                mutableStateFlow.value = state.copy(isSigningOut = false, signOutFailed = true)
            }
        }
    }
}

/**
 * What sign-out removes. [SessionOnly] is the default and the safe choice: the next sign-in on this
 * device finds the cached clients and any unsynced work. [SessionAndData] is for a device being
 * handed to someone else — it deletes everything the app stored, unsynced work included.
 */
enum class SignOutMode { SessionOnly, SessionAndData }

data class ProfileState(
    val username: String = "",
    val officeName: String = "",
    val instanceUrl: String = "",
    val confirmingSignOut: Boolean = false,
    val signOutMode: SignOutMode = SignOutMode.SessionOnly,
    val isSigningOut: Boolean = false,
    val signOutFailed: Boolean = false,
)

sealed interface ProfileAction {
    data object SignOutRequested : ProfileAction
    data object SignOutCancelled : ProfileAction
    data class SignOutModeSelected(val mode: SignOutMode) : ProfileAction
    data object SignOutConfirmed : ProfileAction
}

sealed interface ProfileEvent {
    data object SignedOut : ProfileEvent
}
