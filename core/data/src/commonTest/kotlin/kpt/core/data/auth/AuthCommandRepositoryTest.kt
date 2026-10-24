/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.auth

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kpt.core.base.common.manager.DispatcherManager
import kpt.core.base.store.infra.StoreCacheManager
import kpt.core.base.store.mutation.MutationResult
import kpt.core.data.auth.impl.AuthCommandRepositoryImpl
import kpt.core.data.infra.testMutationGateway
import kpt.core.datastore.prefs.ProjectPreferencesRepositoryImpl
import kpt.core.datastore.prefs.UserPreferencesRepositoryImpl
import kpt.core.network.mifos.auth.api.AuthApi
import kpt.core.model.auth.PostAuthenticationRequest
import kpt.core.model.auth.PostAuthenticationResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Sign-in writes the session into `core/datastore` as the command's own local effect — the REAL
 * preferences over in-memory settings, so what is asserted is what the app persists.
 *
 * These assertions moved here from `SignInViewModelTest` when the write moved out of the ViewModel.
 */
class AuthCommandRepositoryTest {

    private val dispatchers = object : DispatcherManager {
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val main: MainCoroutineDispatcher = Dispatchers.Main
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
        override val appScope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined)
    }

    private val preferences = run {
        val plain = MapSettings()
        val secure = MapSettings()
        ProjectPreferencesRepositoryImpl(
            delegate = UserPreferencesRepositoryImpl(plain, secure, dispatchers),
            plainSettings = plain,
            secureSettings = secure,
            dispatcher = dispatchers,
        )
    }

    private fun api(authenticated: Boolean) = object : AuthApi {
        override suspend fun authenticate(postAuthenticationRequest: PostAuthenticationRequest) =
            PostAuthenticationResponse(
                authenticated = authenticated,
                username = postAuthenticationRequest.username,
                userId = 7,
                base64EncodedAuthenticationKey = "a2V5",
                officeId = 1,
                officeName = "Head Office",
            )
    }

    // Records what sign-out purged. The real one clears every registered store through its
    // SourceOfTruth `deleteAll` — that half is store-level and covered by each store's DAO tests.
    private class FakeCacheManager(private val fail: Boolean = false) : StoreCacheManager {
        var cleared = 0
        override suspend fun clearAll() {
            if (fail) error("disk full")
            cleared++
        }
        override suspend fun pruneExpiredDrafts(maxAgeMs: Long) = Unit
    }

    private val cache = FakeCacheManager()

    private fun repository(authenticated: Boolean = true, online: Boolean = true, cacheManager: StoreCacheManager = cache) =
        AuthCommandRepositoryImpl(api(authenticated), testMutationGateway(isOnline = online), preferences, cacheManager)

    private val request = PostAuthenticationRequest(username = "officer", password = "s3cret!")

    @Test
    fun aGrantedSignInIsPersistedWhenTheCommandReturns() = runTest {
        assertIs<MutationResult.Applied<PostAuthenticationResponse>>(repository().authenticate(request))

        val user = preferences.fineractUser.value
        assertEquals("officer", user.username)
        assertEquals("a2V5", user.base64EncodedAuthenticationKey)
        assertTrue(user.isAuthenticated)
        // The token the network layer reads is the RAW credential, not a pre-formatted header —
        // a stored "Basic …" produced `Authorization: Basic Basic <token>` and 401'd every call.
        assertEquals("a2V5", preferences.authToken)
    }

    @Test
    fun theRawPasswordIsNeverPersisted() = runTest {
        // Cited as the lock in the original port's comment, but it never existed until now.
        repository().authenticate(request)
        assertNull(preferences.fineractUser.value.password)
    }

    @Test
    fun aRefusedCredentialPersistsNoSession() = runTest {
        // Fineract answers 200 with authenticated=false for a bad credential.
        repository(authenticated = false).authenticate(request)
        assertFalse(preferences.fineractUser.value.isAuthenticated)
        assertNull(preferences.fineractUser.value.base64EncodedAuthenticationKey)
    }

    @Test
    fun offlineSignInIsBlockedAndPersistsNothing() = runTest {
        assertIs<MutationResult.Blocked>(repository(online = false).authenticate(request))
        assertNull(preferences.fineractUser.value.base64EncodedAuthenticationKey)
    }

    @Test
    fun resumingTheStoredSessionMarksItAuthenticated() = runTest {
        repository().authenticate(request)
        preferences.updateFineractUser(preferences.fineractUser.value.copy(isAuthenticated = false))

        repository().resumeStoredSession()

        assertTrue(preferences.fineractUser.value.isAuthenticated)
        assertEquals("a2V5", preferences.fineractUser.value.base64EncodedAuthenticationKey)
    }

    // ---- sign-out ----

    @Test
    fun signOutClearsBothHalvesOfTheSession() = runTest {
        // `RootNavViewModel` gates on the framework's `isAuthenticated`; clearing only the Fineract
        // half would leave the shell believing the officer is signed in.
        repository().authenticate(request)

        assertIs<MutationResult.Applied<Unit>>(repository().signOut(clearLocalData = false))

        assertNull(preferences.fineractUser.value.base64EncodedAuthenticationKey)
        assertFalse(preferences.userData.value.isAuthenticated)
    }

    @Test
    fun signOutOnlyKeepsTheData() = runTest {
        repository().signOut(clearLocalData = false)
        assertEquals(0, cache.cleared)
    }

    @Test
    fun signOutAndClearPurgesEveryStore() = runTest {
        repository().signOut(clearLocalData = true)
        assertEquals(1, cache.cleared)
    }

    @Test
    fun signOutNeedsNoNetwork() = runTest {
        // An officer who cannot sign out without signal is stranded.
        repository().authenticate(request)
        assertIs<MutationResult.Applied<Unit>>(repository(online = false).signOut(clearLocalData = true))
        assertNull(preferences.fineractUser.value.base64EncodedAuthenticationKey)
    }

    @Test
    fun aFailedPurgeLeavesTheOfficerSignedIn() = runTest {
        // Data first, then the session: never "signed out, but the data you asked to remove is
        // still here".
        repository().authenticate(request)

        val result = repository(cacheManager = FakeCacheManager(fail = true)).signOut(clearLocalData = true)

        assertIs<MutationResult.Failed>(result)
        assertEquals("a2V5", preferences.fineractUser.value.base64EncodedAuthenticationKey)
    }
}
