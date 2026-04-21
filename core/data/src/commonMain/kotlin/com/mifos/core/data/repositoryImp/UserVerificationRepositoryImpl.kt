/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.UserVerificationRepository
import com.russhwolf.settings.Settings
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val VERIFICATION_TIMESTAMP_KEY = "org.mifospay.user_verification_timestamp"

/** Validity window for a recorded verification token, in milliseconds. */
private const val VERIFICATION_EXPIRY_MS = 30_000L

/**
 * [UserVerificationRepository] impl backed by [Settings] (multiplatform key-value).
 *
 * Persists only the timestamp of the last [recordVerification] call; validity is
 * judged against [VERIFICATION_EXPIRY_MS] (30 seconds) during
 * [consumeVerification]. The timestamp is always cleared on consume — even if
 * it had expired — enforcing single-use semantics regardless of outcome.
 */
@OptIn(ExperimentalTime::class)
class UserVerificationRepositoryImpl(
    private val settings: Settings,
) : UserVerificationRepository {

    override fun recordVerification() {
        settings.putLong(VERIFICATION_TIMESTAMP_KEY, Clock.System.now().toEpochMilliseconds())
    }

    override fun consumeVerification(): Boolean {
        val timestamp = settings.getLongOrNull(VERIFICATION_TIMESTAMP_KEY)
        settings.remove(VERIFICATION_TIMESTAMP_KEY)

        if (timestamp == null) return false

        val elapsed = Clock.System.now().toEpochMilliseconds() - timestamp
        return elapsed in 0..VERIFICATION_EXPIRY_MS
    }
}
