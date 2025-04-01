package com.mifos.core.datastore.model
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val tenant: String,
    val baseUrl: String,
    val passcode: String? = null,
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val language: MifosAppLanguage,
) {
    companion object {
        val DEFAULT = AppSettings(
            tenant = "default",
            baseUrl = "https://tt.mifos.community/",
            appTheme = AppTheme.SYSTEM,
            language = MifosAppLanguage.SYSTEM_LANGUAGE,
        )
    }
}