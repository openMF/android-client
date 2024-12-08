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

class ValidateServerPortUseCase {
    operator fun invoke(port: String): ValidationResult {
        return if (port.isBlank()) {
            ValidationResult(false, R.string.core_domain_error_port_blank)
        } else {
            val convertedPort = port.toIntOrNull()
            if (convertedPort == null || convertedPort !in 1..65535) {
                ValidationResult(false, R.string.core_domain_error_port_invalid)
            } else {
                ValidationResult(true)
            }
        }
    }
}
