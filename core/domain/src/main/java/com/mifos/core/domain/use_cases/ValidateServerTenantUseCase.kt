package com.mifos.core.domain.use_cases

import com.mifos.core.domain.R
import com.mifos.core.domain.utils.ValidationResult

class ValidateServerTenantUseCase {
    operator fun invoke(tenant: String): ValidationResult {
        val regex = Regex("^[a-zA-Z0-9]+$")

        if (tenant.isBlank()) {
            return ValidationResult(false, R.string.core_domain_error_tenant_blank)
        }

        if (!regex.matches(tenant)) {
            return ValidationResult(false, R.string.core_domain_error_tenant_invalid)
        }

        return ValidationResult(true)
    }
}