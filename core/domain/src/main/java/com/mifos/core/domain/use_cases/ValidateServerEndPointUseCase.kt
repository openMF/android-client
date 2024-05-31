package com.mifos.core.domain.use_cases

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