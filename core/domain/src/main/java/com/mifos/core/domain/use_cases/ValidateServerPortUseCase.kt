package com.mifos.core.domain.use_cases

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