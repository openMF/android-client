package com.mifos.core.domain.use_cases

import com.mifos.core.domain.R
import com.mifos.core.domain.utils.ValidationResult


/**
 * Created by Aditya Gupta on 11/02/24.
 */

class PasswordValidationUseCase {

    operator fun invoke(password: String): ValidationResult {

        if (password.isEmpty()) {
            return ValidationResult(
                success = false,
                R.string.core_domain_enter_credentials
            )
        } else if (password.length < 6) {
            return ValidationResult(
                success = false,
                R.string.core_domain_error_password_length
            )
        }
        return ValidationResult(success = true)
    }

}