/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.domain.R
import com.mifos.core.domain.utils.ValidationResult

/**
 * Created by Aditya Gupta on 11/02/24.
 */

class UsernameValidationUseCase {

    operator fun invoke(username: String): ValidationResult {
        if (username.isEmpty()) {
            return ValidationResult(
                success = false,
                R.string.core_domain_enter_credentials,
            )
        } else if (username.length < 5) {
            return ValidationResult(
                success = false,
                R.string.core_domain_error_username_length,
            )
        }
        return ValidationResult(success = true)
    }
}
