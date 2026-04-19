/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

/**
 * Short-lived verification-token store shared between the passcode-verify flow
 * and any consumer that needs to confirm "the user just re-authenticated."
 *
 * Carries no identity — only attests that a verification happened within the
 * implementation-defined validity window. Consumers [recordVerification] after
 * a successful passcode entry and [consumeVerification] (one-shot) before
 * acting on a protected mutation.
 */
interface UserVerificationRepository {

    /**
     * Stamps a new verification token. Overwrites any existing token.
     *
     * Called from the internal-passcode-screen success path only when the
     * route carries a non-null verification key (i.e. an external flow
     * explicitly requested re-authentication).
     */
    fun recordVerification()

    /**
     * Reads and clears the stored token. One-shot — subsequent calls without a
     * new [recordVerification] return `false`.
     *
     * @return `true` if a token exists and is within the validity window;
     *         `false` otherwise.
     */
    fun consumeVerification(): Boolean
}
