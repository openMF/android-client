package com.mifos.feature.client.createNewClient

actual object PhoneNumberUtil {
    actual fun isGlobalPhoneNumber(phoneNumber: String): Boolean {
        // TODO: Implement the logic to check if the phone number is global or not
        return phoneNumber.isNotBlank() && phoneNumber.all { it.isDigit() || it == '+' }
    }
}