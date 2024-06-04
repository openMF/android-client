package com.mifos.core.domain.use_cases

import com.mifos.core.domain.R.string
import com.mifos.core.domain.utils.ValidationResult
import java.util.regex.Pattern

class ValidateServerProtocolUseCase {

    operator fun invoke(protocol: String): ValidationResult {
        val protocolRegex = Pattern.compile("^(http://|https://)$")

        return when {
            protocol.isBlank() -> ValidationResult(false, string.core_domain_error_protocol_blank)
            !protocolRegex.matcher(protocol).matches() -> ValidationResult(false, string.core_domain_error_protocol_invalid)
            else -> ValidationResult(true)
        }
    }
}