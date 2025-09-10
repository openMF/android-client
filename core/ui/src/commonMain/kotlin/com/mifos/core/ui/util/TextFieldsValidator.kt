/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.util

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.error_digits_only
import androidclient.core.ui.generated.resources.error_field_empty
import androidclient.core.ui.generated.resources.error_invalid_characters
import androidclient.core.ui.generated.resources.error_invalid_number
import androidclient.core.ui.generated.resources.error_number_zero
import org.jetbrains.compose.resources.StringResource

object TextFieldsValidator {

    fun stringValidator(input: String): StringResource? {
        return when {
            input.isBlank() -> Res.string.error_field_empty
            input.any { !it.isLetterOrDigit() && !it.isWhitespace() } -> Res.string.error_invalid_characters
            else -> null // valid
        }
    }

    fun optionalStringValidator(input: String): StringResource? {
        return when {
            input.any { !it.isLetterOrDigit() && !it.isWhitespace() } -> Res.string.error_invalid_characters
            else -> null // valid
        }
    }

    fun numberValidator(input: String): StringResource? {
        return when {
            input.isBlank() -> Res.string.error_field_empty
            input.any { !it.isDigit() } -> Res.string.error_digits_only
            input.toIntOrNull() == null -> Res.string.error_invalid_number
            input.toInt() == 0 -> Res.string.error_number_zero
            else -> null
        }
    }
}
