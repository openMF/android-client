/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.utils

actual object PhoneNumberUtil {
    actual fun isGlobalPhoneNumber(phoneNumber: String): Boolean {
        // TODO: Implement the logic to check if the phone number is global or not
        return phoneNumber.isNotBlank() && phoneNumber.all { it.isDigit() || it == '+' }
    }
}
