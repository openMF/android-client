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

class ValidateServerEndPointUseCase {
    operator fun invoke(endPoint: String): ValidationResult {
        val regex =
            Regex("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$")

        if (endPoint.isBlank()) {
            return ValidationResult(false, R.string.core_domain_error_endpoint_blank)
        }

        if (!regex.matches(endPoint)) {
            return ValidationResult(false, R.string.core_domain_error_endpoint_invalid)
        }

        return ValidationResult(true)
    }
}
