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
import core.domain.generated.resources.core_domain_error_tenant_blank
import core.domain.generated.resources.core_domain_error_tenant_invalid
import org.jetbrains.compose.resources.getString

class ValidateServerTenantUseCase {
    suspend operator fun invoke(tenant: String): ValidationResult {
        val regex = Regex("^[a-zA-Z0-9]+$")

        if (tenant.isBlank()) {
            return ValidationResult(false, getString(Res.string.core_domain_error_tenant_blank))
        }

        if (!regex.matches(tenant)) {
            return ValidationResult(false, getString(Res.string.core_domain_error_tenant_invalid))
        }

        return ValidationResult(true)
    }
}
