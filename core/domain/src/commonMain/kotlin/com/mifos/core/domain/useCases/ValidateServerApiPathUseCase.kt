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
import core.domain.generated.resources.core_domain_error_api_path_blank
import core.domain.generated.resources.core_domain_error_api_path_end_with
import core.domain.generated.resources.core_domain_error_api_path_start_with
import org.jetbrains.compose.resources.getString

class ValidateServerApiPathUseCase {
    suspend operator fun invoke(apiPath: String): ValidationResult {
        if (apiPath.isBlank()) {
            return ValidationResult(false, getString(Res.string.core_domain_error_api_path_blank))
        }

        if (!apiPath.startsWith("/")) {
            return ValidationResult(false, getString(Res.string.core_domain_error_api_path_start_with))
        }

        if (!apiPath.endsWith("/")) {
            return ValidationResult(false, getString(Res.string.core_domain_error_api_path_end_with))
        }

        return ValidationResult(true)
    }
}
