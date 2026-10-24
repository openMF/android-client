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

import kpt.core.base.store.mutation.MutationResult
import kpt.core.model.auth.PostAuthenticationRequest
import kpt.core.model.auth.PostAuthenticationResponse

/**
 * Every auth write, as an explicit outcome.
 *
 * Each returns [MutationResult] rather than `Unit`: a field officer has to be told whether the
 * change reached the server, was refused because the device is offline, or failed — a distinction
 * that cannot be reconstructed above this layer.
 *
 * The session these produce lives in `core/datastore`; writing it is this layer's job, never the
 * ViewModel's.
 */
interface AuthCommandRepository {

    /** Sign in. On a granted sign-in the session is already persisted when this returns `Applied`. */
    suspend fun authenticate(postAuthenticationRequest: PostAuthenticationRequest): MutationResult<PostAuthenticationResponse>

    /** Mark the stored session unlocked again (after a biometric check). */
    suspend fun resumeStoredSession(): MutationResult<Unit>

    /**
     * End the session. With [clearLocalData] false only the session goes — cached data and unsynced
     * work stay for the next sign-in. With it true, EVERYTHING this app stored on the device goes
     * too, unsynced work included; the officer chose that explicitly in the sign-out dialog.
     *
     * Local only, so it works offline: an officer who cannot sign out without signal is stranded.
     */
    suspend fun signOut(clearLocalData: Boolean): MutationResult<Unit>
}
