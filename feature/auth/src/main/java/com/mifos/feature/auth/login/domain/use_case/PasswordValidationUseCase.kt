package com.mifos.feature.auth.login.domain.use_case

import com.mifos.feature.auth.R
import com.mifos.feature.auth.login.domain.model.ValidationResult

/**
 * Created by Aditya Gupta on 11/02/24.
 */

class PasswordValidationUseCase {

    operator fun invoke(password: String): ValidationResult {

        if (password.isEmpty()) {
            return ValidationResult(
                success = false,
                R.string.feature_auth_enter_credentials
            )
        } else if (password.length < 6) {
            return ValidationResult(
                success = false,
                R.string.feature_error_password_length
            )
        }
        return ValidationResult(success = true)
    }

}