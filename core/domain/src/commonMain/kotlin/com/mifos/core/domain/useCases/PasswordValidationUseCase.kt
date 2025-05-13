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

import com.mifos.core.domain.utils.ValidationResult
import core.domain.generated.resources.Res
import core.domain.generated.resources.core_domain_enter_credentials
import core.domain.generated.resources.core_domain_error_password_length
import org.jetbrains.compose.resources.getString

/**
 * Created by Aditya Gupta on 11/02/24.
 */

class PasswordValidationUseCase {

    suspend operator fun invoke(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(
                success = false,
                getString(Res.string.core_domain_enter_credentials),
            )
        } else if (password.length < 6) {
            return ValidationResult(
                success = false,
                getString(Res.string.core_domain_error_password_length),
            )
        }
        return ValidationResult(success = true)
    }
}
