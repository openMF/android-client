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
import core.domain.generated.resources.core_domain_error_protocol_blank
import core.domain.generated.resources.core_domain_error_protocol_invalid

class ValidateServerProtocolUseCase {

    operator fun invoke(protocol: String): ValidationResult {
        val protocolRegex = Regex("^(http://|https://)$")

        return when {
            protocol.isBlank() -> ValidationResult(false, Res.string.core_domain_error_protocol_blank)
            !protocolRegex.matches(protocol) -> ValidationResult(false, Res.string.core_domain_error_protocol_invalid)
            else -> ValidationResult(true)
        }
    }
}
