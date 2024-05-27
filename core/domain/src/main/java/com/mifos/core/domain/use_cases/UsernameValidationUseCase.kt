package com.mifos.core.domain.use_cases

import com.mifos.core.domain.R
import com.mifos.core.domain.utils.ValidationResult


/**
 * Created by Aditya Gupta on 11/02/24.
 */

class UsernameValidationUseCase {

    operator fun invoke(username: String): ValidationResult {
        if (username.isEmpty()) {
            return ValidationResult(
                success = false,
                R.string.core_domain_enter_credentials
            )
        } else if (username.length < 5) {
            return ValidationResult(
                success = false,
                R.string.core_domain_error_username_length
            )
        }
        return ValidationResult(success = true)
    }

}