package com.mifos.core.data.repositoryImp

import com.russhwolf.settings.Settings
import org.mifos.authenticator.biometrics.BiometricStorageAdapter


const val REGISTRATION_DATA_KEY = "com.mifos.registration_data"
const val BIOMETRIC_REGISTERED_KEY = "com.mifos.biometric_registered"


class BiometricStorageAdapterImpl(
    private val settings: Settings
): BiometricStorageAdapter {
    override fun saveRegistrationData(registrationData: String) {
        settings.putBoolean(BIOMETRIC_REGISTERED_KEY, true)
        settings.putString(REGISTRATION_DATA_KEY, registrationData)
    }

    override fun loadRegistrationData(): String? {
        if (!settings.getBoolean(BIOMETRIC_REGISTERED_KEY, false)) return null
        return settings.getString(REGISTRATION_DATA_KEY, "")
    }

    override fun deleteRegistrationData() {
        settings.remove(BIOMETRIC_REGISTERED_KEY)
        settings.remove(REGISTRATION_DATA_KEY)
    }
}