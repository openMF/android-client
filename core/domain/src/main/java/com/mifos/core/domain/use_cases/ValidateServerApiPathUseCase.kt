package com.mifos.core.domain.use_cases

import com.mifos.core.domain.R
import com.mifos.core.domain.utils.ValidationResult

class ValidateServerApiPathUseCase {
    operator fun invoke(apiPath: String): ValidationResult {
        if (apiPath.isBlank()) {
            return ValidationResult(false, R.string.core_domain_error_api_path_blank)
        }

        if (!apiPath.startsWith("/")) {
            return ValidationResult(false, R.string.core_domain_error_api_path_start_with)
        }

        if (!apiPath.endsWith("/")) {
            return ValidationResult(false, R.string.core_domain_error_api_path_end_with)
        }

        return ValidationResult(true)
    }
}