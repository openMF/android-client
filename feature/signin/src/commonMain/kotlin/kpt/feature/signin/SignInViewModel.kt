/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.signin

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import kpt.core.base.security.BiometricAuthenticator
import kpt.core.base.security.BiometricResult
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.auth.AuthCommandRepository
import kpt.core.datastore.prefs.ProjectPreferencesRepository
import kpt.core.domain.validation.CredentialRules
import kpt.core.domain.validation.ValidationResult
import kpt.core.model.objects.users.User
import kpt.core.network.mifos.auth.dto.PostAuthenticationRequest
import kpt.core.network.mifos.auth.dto.PostAuthenticationResponse

/**
 * Sign-in against the configured Fineract instance.
 *
 * Authentication is the one write that CANNOT be optimistic — there is no local credential store to
 * check against, so [AuthCommandRepository] runs it `OnlineRequired` and an offline attempt comes
 * back [MutationResult.Blocked]. That is surfaced as its own message rather than a generic failure:
 * "wrong password" and "no signal" are different problems and a field officer acts on them
 * differently.
 */
class SignInViewModel(
    private val auth: AuthCommandRepository,
    private val preferences: ProjectPreferencesRepository,
    private val biometrics: BiometricAuthenticator,
) : BaseViewModel<SignInState, SignInEvent, SignInAction>(SignInState()) {

    init {
        // Biometric unlock is offered ONLY when there is a stored session to unlock INTO and the
        // device can actually perform it. Both halves matter: offering it with no stored credential
        // would dead-end, and offering it on hardware without enrolment is a button that cannot work.
        //
        // `isAvailable()` is queried once at composition rather than per-frame: enrolment does not
        // change while a login screen is open, and polling it would hit the platform API on every
        // recomposition.
        viewModelScope.launch {
            preferences.fineractUser.collect { user ->
                val hasSession = !user.base64EncodedAuthenticationKey.isNullOrBlank()
                mutableStateFlow.value = state.copy(
                    hasStoredSession = hasSession,
                    biometricAvailable = hasSession && runCatching { biometrics.isAvailable() }.getOrDefault(false),
                )
            }
        }

        // The instance chip states WHERE this sign-in goes. Collected rather than read once:
        // `serverConfig` is a StateFlow, and a user who leaves for serverConfig and comes back must
        // see the value they just changed, not the one captured when the screen was first composed.
        viewModelScope.launch {
            preferences.serverConfig.collect { config ->
                mutableStateFlow.value = state.copy(
                    instanceHost = config.endPoint,
                    instanceTenant = config.tenant,
                )
            }
        }
    }

    override fun handleAction(action: SignInAction) {
        when (action) {
            is SignInAction.UsernameChanged ->
                mutableStateFlow.value = state.copy(username = action.value, error = null)

            is SignInAction.PasswordChanged ->
                mutableStateFlow.value = state.copy(password = action.value, error = null)

            SignInAction.Submit -> submit()

            SignInAction.UnlockWithBiometrics -> unlockWithBiometrics()
        }
    }

    private fun submit() {
        // Disabled in the UI too, but guarded here as well: the ViewModel owns the invariant, and a
        // programmatic dispatch must not slip past a control's disabled state.
        if (state.isLockedOut) return
        val username = state.username.trim()
        val password = state.password
        // Validated here rather than on the server: a round trip to be told the field is too short
        // is a round trip a field officer often cannot afford.
        val validation = validate(username, password)
        if (validation != null) {
            mutableStateFlow.value = state.copy(error = validation)
            return
        }
        mutableStateFlow.value = state.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            when (val result = auth.authenticate(PostAuthenticationRequest(username = username, password = password))) {
                is MutationResult.Applied -> onAuthenticated(result.value, username, password)
                is MutationResult.Blocked ->
                    fail(if (result.reason == BlockReason.OFFLINE) SignInError.Offline else SignInError.Rejected)
                is MutationResult.Failed -> {
                    // The cause was being DISCARDED. A sign-in that fails for a transport reason —
                    // a wrong base path, a serialization mismatch, a TLS error — looks identical on
                    // screen to a wrong password, and with nothing in the log there is no way to
                    // tell them apart from a device. Diagnosing the 2026-10-01 "Login failed"
                    // (a 404 from a wrong `base_path`) took a packet-level probe for exactly this
                    // reason. The message the user sees is unchanged.
                    Logger.e(tag = TAG, throwable = result.cause) { "authenticate failed" }
                    fail(SignInError.Rejected)
                }
                is MutationResult.Conflicted -> fail(SignInError.Rejected)
            }
        }
    }

    /**
     * Unlock a STORED session with biometrics instead of retyping the credential.
     *
     * This is an ACCELERATOR, never a replacement. Every non-success result returns the user to the
     * credential form rather than dead-ending, because a field officer may hand the device to a
     * colleague whose biometrics are not enrolled on it.
     *
     * Note what this does NOT do: it does not re-authenticate against Fineract. The stored
     * `base64EncodedAuthenticationKey` is what every subsequent call already uses, so unlocking
     * re-affirms a session the server previously granted. It cannot mint one.
     */
    private fun unlockWithBiometrics() {
        if (!state.biometricAvailable || state.isLockedOut) return
        mutableStateFlow.value = state.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            val stored = preferences.fineractUser.value
            when (biometrics.authenticate(BIOMETRIC_REASON)) {
                is BiometricResult.Success -> {
                    // Re-affirming `isAuthenticated` is what also sets `isUnlocked` downstream.
                    // Without it the root state machine sends an authenticated-but-locked user
                    // straight back here, which is the bounce observed on device 2026-10-02.
                    preferences.updateFineractUser(stored.copy(isAuthenticated = true))
                    mutableStateFlow.value = state.copy(isSubmitting = false, failedAttempts = 0, isLockedOut = false)
                    sendEvent(SignInEvent.LoggedIn)
                }
                // A cancel is a CHOICE, not a failure. Surfacing an error for it would punish the
                // user for changing their mind, and it must not count toward lockout.
                is BiometricResult.Cancelled ->
                    mutableStateFlow.value = state.copy(isSubmitting = false)

                is BiometricResult.Failure ->
                    fail(SignInError.BiometricFailed, countsTowardLockout = false)

                // Hardware or enrolment went away since composition. Retract the affordance rather
                // than leaving a control that cannot work.
                is BiometricResult.Unavailable ->
                    mutableStateFlow.value = state.copy(isSubmitting = false, biometricAvailable = false)
            }
        }
    }

    /**
     * Fineract answers 200 with `authenticated = false` for a bad credential rather than a 4xx, so
     * a successful CALL is not a successful LOGIN — the flag has to be checked explicitly.
     */
    private suspend fun onAuthenticated(
        response: PostAuthenticationResponse,
        username: String,
        password: String,
    ) {
        if (response.authenticated != true) {
            fail(SignInError.Rejected)
            return
        }
        preferences.updateFineractUser(
            User(
                username = username,
                // password DELIBERATELY NOT PERSISTED.
                //
                // The original wrote the raw password into preferences and this port carried it
                // over; S1's ledger flagged it as requiring a written justification or removal and
                // it was never closed. Measured 2026-10-02: NOTHING reads `User.password` —
                // every consumer of `fineractUser` reads `username` or
                // `base64EncodedAuthenticationKey`, and `authHeader` derives from the encoded key.
                // Fineract's subsequent Basic auth needs that key, not the password, so storing
                // the password bought nothing and kept a credential on disk for the life of the
                // session. Locked by `theRawPasswordIsNeverPersisted`.
                userId = response.userId ?: 0,
                base64EncodedAuthenticationKey = response.base64EncodedAuthenticationKey,
                isAuthenticated = true,
                officeId = response.officeId ?: 0,
                officeName = response.officeName,
                permissions = response.permissions.orEmpty(),
            ),
        )
        // A success clears the counter. Lockout throttles CONSECUTIVE failures; a user who gets it
        // right on the fourth try should not stay one mistake from being locked out.
        mutableStateFlow.value = state.copy(isSubmitting = false, failedAttempts = 0, isLockedOut = false)
        sendEvent(SignInEvent.LoggedIn)
    }

    /**
     * A failure that reached the server (or failed to) counts toward lockout. A LOCAL validation
     * failure does NOT: mistyping a short password is not an authentication attempt, and counting
     * it would lock a user out without a single credential ever being checked.
     */
    private fun fail(error: SignInError, countsTowardLockout: Boolean = true) {
        val attempts = if (countsTowardLockout) state.failedAttempts + 1 else state.failedAttempts
        val locked = attempts >= LOCK_AFTER_ATTEMPTS
        mutableStateFlow.value = state.copy(
            isSubmitting = false,
            error = if (locked) SignInError.LockedOut else error,
            failedAttempts = attempts,
            isLockedOut = locked,
        )
    }

    /**
     * Credential rules come from [CredentialRules], never from a local constant.
     *
     * This ViewModel previously declared its own `MIN_USERNAME = 5` while the original app's
     * `UsernameValidationUseCase` rejected `length < 4` (`897ffdac1`). Nothing related the two, so a
     * four-character username that signed in on the original was refused here — a regression
     * introduced by re-authoring the screen rather than porting it, and then baked into the
     * user-facing copy ("at least 5 characters"). Reading through the shared rules is what makes the
     * thresholds impossible to diverge again.
     */
    internal companion object {
        const val TAG = "SignInViewModel"

        /**
         * Mirrors `core-base/security/SecurityPolicy.lockAfterFailedAttempts` (default 5). Named
         * here rather than hardcoded inline so the relationship is greppable; when the policy
         * becomes DI-injected this constant is the single place to replace.
         */
        const val LOCK_AFTER_ATTEMPTS = 5

        /** Shown by the OS prompt. States WHY, because a bare biometric dialog is alarming. */
        const val BIOMETRIC_REASON = "Unlock your field officer session"

    }

    private fun validate(username: String, password: String): SignInError? = when {
        CredentialRules.username(username) is ValidationResult.Invalid -> SignInError.UsernameTooShort
        CredentialRules.password(password) is ValidationResult.Invalid -> SignInError.PasswordTooShort
        else -> null
    }
}

data class SignInState(
    val username: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val error: SignInError? = null,
    /**
     * The instance this sign-in will authenticate against, shown BEFORE the user types.
     *
     * `ui.yaml#components.instance_chip`. Empty until the first `serverConfig` emission, which is
     * why the chip renders only when [instanceHost] is non-blank rather than showing a placeholder
     * that would briefly state the wrong target.
     *
     * This is the screen's answer to the 2026-10-01 incident: every call 404'd against a wrong base
     * path, sign-in reported "Login failed. Check your credentials and try again", and diagnosing it
     * needed a packet-level probe — precisely because the screen displayed nothing about where it
     * was connecting.
     */
    val instanceHost: String = "",
    val instanceTenant: String = "",

    /**
     * Consecutive failed credential attempts, and whether sign-in is currently paused.
     *
     * `ui.yaml#components.lockout_notice`. Counted here rather than through
     * `core-base/security/FailedAttemptTracker` on purpose: that component bundles count + lock
     * with a THIRD behaviour, an irreversible `SecureWiper` data wipe once failures reach
     * `SecurityPolicy.wipeAfterFailedAttempts` (default 10). Throttling a login screen should not
     * silently arm data destruction, so the threshold is taken from the shipped policy while the
     * wipe path is deliberately not wired. Enabling it is a product decision, not an
     * implementation detail.
     */
    val failedAttempts: Int = 0,
    val isLockedOut: Boolean = false,

    /**
     * `ui.yaml#states.biometric_ready`. Backed by `core-base/security/BiometricAuthenticator`,
     * which ships android/ios/desktop/js actuals and was never wired until now.
     */
    val hasStoredSession: Boolean = false,
    val biometricAvailable: Boolean = false,
) {
    /** The accelerator is offered only when it can actually succeed. */
    val canUseBiometrics: Boolean get() = hasStoredSession && biometricAvailable && !isLockedOut
    /** How many attempts remain before sign-in pauses. Drives the warn-before-lock banner. */
    val attemptsRemaining: Int get() = (SignInViewModel.LOCK_AFTER_ATTEMPTS - failedAttempts).coerceAtLeast(0)

    /** Warn only as the limit approaches; a counter shown from attempt one is noise. */
    val isApproachingLockout: Boolean get() = !isLockedOut && failedAttempts > 0 && attemptsRemaining <= 2
}

/** Distinct causes, because the officer's next step differs for each. */
enum class SignInError { UsernameTooShort, PasswordTooShort, Rejected, Offline, LockedOut, BiometricFailed }

sealed interface SignInAction {
    data class UsernameChanged(val value: String) : SignInAction
    data class PasswordChanged(val value: String) : SignInAction
    data object Submit : SignInAction
    data object UnlockWithBiometrics : SignInAction
}

sealed interface SignInEvent {
    data object LoggedIn : SignInEvent
}
