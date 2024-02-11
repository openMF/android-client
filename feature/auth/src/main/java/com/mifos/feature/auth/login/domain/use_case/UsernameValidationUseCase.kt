package com.mifos.feature.auth.login.domain.use_case

import com.mifos.feature.auth.R
import com.mifos.feature.auth.login.domain.model.ValidationResult

/**
 * Created by Aditya Gupta on 11/02/24.
 */

class UsernameValidationUseCase {

    operator fun invoke(username: String): ValidationResult {
        if (username.isEmpty()) {
            return ValidationResult(
                success = false,
                R.string.feature_auth_enter_credentials
            )
        } else if (username.length < 5) {
            return ValidationResult(
                success = false,
                R.string.feature_error_username_length
            )
        }
        return ValidationResult(success = true)
    }

}