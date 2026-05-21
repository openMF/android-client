/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.auth.ui

import org.jetbrains.compose.resources.StringResource

/**
 * MVI state for the Login screen. Holds pre-submit validation errors that bypass
 * `SubmitHandler` (validation runs before any network call). `SubmitState` from the
 * VM tracks the network submission lifecycle separately.
 */
data class LoginState(
    val usernameError: StringResource? = null,
    val passwordError: StringResource? = null,
)
