/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.auth.login

/**
 * Created by Aditya Gupta on 06/08/23.
 */

sealed class LoginUiState {

    data object Empty : LoginUiState()

    data object ShowProgress : LoginUiState()

    data class ShowError(val message: Int) : LoginUiState()

    data class ShowValidationError(val usernameError: Int? = null, val passwordError: Int? = null) :
        LoginUiState()

    data object HomeActivityIntent : LoginUiState()

    data object PassCodeActivityIntent : LoginUiState()
}
