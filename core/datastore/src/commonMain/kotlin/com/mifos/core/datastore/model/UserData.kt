package com.mifos.core.datastore.model

import com.mifos.core.model.utils.Parcelable
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val language: MifosAppLanguage,
    val userStatus:Boolean
)  {
    companion object {
        val DEFAULT = UserData(
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            language = MifosAppLanguage.SYSTEM_LANGUAGE,
            userStatus = false
        )
    }
}