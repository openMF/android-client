/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.datastore.model
import com.mifos.core.model.objects.LanguageConfig
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val tenant: String,
    val baseUrl: String,
    val passcode: String? = null,
    val appTheme: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val language: LanguageConfig,
) {
    companion object {
        val DEFAULT = AppSettings(
            tenant = "default",
            baseUrl = "https://tt.mifos.community/",
            appTheme = DarkThemeConfig.FOLLOW_SYSTEM,
            language = LanguageConfig.DEFAULT,
        )
    }
}
