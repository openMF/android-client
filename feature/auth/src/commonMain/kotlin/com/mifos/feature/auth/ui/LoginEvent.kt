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
 * One-shot events emitted by [LoginViewModel]. Navigation-class events flow here
 * (kept off of `LoginState` since they fire once and aren't replayable).
 */
sealed interface LoginEvent {
    data object NavigateToPasscode : LoginEvent
    data class ShowError(val message: StringResource) : LoginEvent
}
