/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.auth.impl

import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.infra.StoreCacheManager
import kpt.core.base.store.mutation.CommandSpec
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.base.store.mutation.MutationPolicy
import kpt.core.data.auth.AuthCommandRepository
import kpt.core.datastore.prefs.ProjectPreferencesRepository
import kpt.core.model.objects.users.User
import kpt.core.network.mifos.auth.api.AuthApi
import kpt.core.model.auth.PostAuthenticationRequest
import kpt.core.model.auth.PostAuthenticationResponse

/**
 * Sign-in, through the single write door: network (`AuthApi`) → `core/datastore` (the session).
 *
 * The session used to be written by `SignInViewModel` itself, AFTER this returned — a ViewModel
 * writing straight into `core/datastore`, skipping this layer. It is the command's local effect now
 * ([CommandSpec.localApply]), so the session exists the moment `Applied` comes back.
 */
@RepositoryBinding(binds = AuthCommandRepository::class)
internal class AuthCommandRepositoryImpl(
    private val authApi: AuthApi,
    private val gateway: MutationGateway,
    private val preferences: ProjectPreferencesRepository,
    private val storeCacheManager: StoreCacheManager,
) : AuthCommandRepository {

    override suspend fun authenticate(postAuthenticationRequest: PostAuthenticationRequest) =
        gateway.command(
            CommandSpec(
                payload = postAuthenticationRequest,
                endpoint = { authApi.authenticate(postAuthenticationRequest = it) },
                localApply = { response ->
                    if (response?.authenticated == true) {
                        preferences.updateFineractUser(response.toSessionUser(postAuthenticationRequest.username))
                    }
                },
            ),
            // Credentials are checked by the server; there is nothing to apply offline.
            policy = MutationPolicy.OnlineRequired,
        )

    /**
     * Re-affirm the STORED session after a biometric unlock — a local write, so it goes through
     * [MutationGateway.localMutation] rather than the ViewModel writing the datastore itself.
     */
    override suspend fun resumeStoredSession() =
        gateway.localMutation(table = SESSION) {
            preferences.updateFineractUser(preferences.fineractUser.value.copy(isAuthenticated = true))
        }

    /**
     * Data first, then the session: if the purge throws, the officer is still signed in and sees
     * the failure, rather than signed out with the data they asked to remove still on the device.
     *
     * `StoreCacheManager.clearAll()` clears every registered store through its SourceOfTruth
     * `deleteAll` (cached reads AND the offline queues' `pending*` stores), plus drafts and sync
     * bookkeeping. Before 2026-10-23 nothing called it at all — sign-out cleared only the session,
     * so every cached client, loan and document stayed for whoever signed in next.
     */
    override suspend fun signOut(clearLocalData: Boolean) =
        gateway.localMutation(table = SESSION) {
            if (clearLocalData) storeCacheManager.clearAll()
            preferences.clearFineractUser()
            // The framework's auth flags: `RootNavViewModel` gates on `isAuthenticated`, so clearing
            // only the Fineract user would leave the shell believing the officer is signed in.
            preferences.clearUserData()
        }

    private companion object {
        const val SESSION = "fineract_session"
    }
}

/**
 * The session [User] for a granted sign-in.
 *
 * The password is DELIBERATELY NOT PERSISTED. The original wrote the raw password into preferences;
 * measured 2026-10-02, nothing reads `User.password` — every consumer reads `username` or
 * `base64EncodedAuthenticationKey`, and Fineract's subsequent Basic auth needs the key, not the
 * password. Locked by `AuthCommandRepositoryTest.theRawPasswordIsNeverPersisted`.
 */
internal fun PostAuthenticationResponse.toSessionUser(username: String?): User = User(
    username = username,
    userId = userId ?: 0,
    base64EncodedAuthenticationKey = base64EncodedAuthenticationKey,
    isAuthenticated = true,
    officeId = officeId ?: 0,
    officeName = officeName,
    permissions = permissions.orEmpty(),
)
