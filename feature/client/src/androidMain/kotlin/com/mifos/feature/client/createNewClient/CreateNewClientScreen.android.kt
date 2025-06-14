package com.mifos.feature.client.createNewClient

import android.content.Context
import android.telephony.PhoneNumberUtils
import java.io.File
import androidx.compose.ui.platform.LocalContext

actual object PhoneNumberUtil {
    actual fun isGlobalPhoneNumber(phoneNumber: String): Boolean {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)
    }
}

actual suspend fun createTempImageFile(): File {
    // Needs Android context
    val context = LocalContext.current
    val imageFileName = "clients_image"
    return File.createTempFile(
        imageFileName,
        ".png",
        context.externalCacheDir,
    )
}