package com.mifos.feature.client.createNewClient

import android.telephony.PhoneNumberUtils

actual object PhoneNumberUtil {
    actual fun isGlobalPhoneNumber(phoneNumber: String): Boolean {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)
    }
}