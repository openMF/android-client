/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.android.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import cmp.shared.utils.initKoin
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.CachePolicy
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import template.core.base.ui.util.getDefaultImageLoader

/**
 * Android application class. Initializes Koin and restores the user's saved language preference.
 */
class AndroidApp : Application(), SingletonImageLoader.Factory, KoinComponent {

    private val userDataRepository: UserPreferencesRepository by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AndroidApp)
            androidLogger()
        }
        restoreSavedLanguage()
    }

    private fun restoreSavedLanguage() {
        runBlocking {
            val userData = userDataRepository.userInfo.first()
            val savedLanguage = userData.language
            val localeName = if (savedLanguage == MifosAppLanguage.SYSTEM_LANGUAGE) null else savedLanguage.code

            val desiredLocales = if (localeName != null) {
                LocaleListCompat.forLanguageTags(localeName)
            } else {
                LocaleListCompat.getEmptyLocaleList()
            }

            val currentLocales = AppCompatDelegate.getApplicationLocales()
            if (currentLocales != desiredLocales) {
                AppCompatDelegate.setApplicationLocales(desiredLocales)
            }
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        getDefaultImageLoader(context)
            .newBuilder()
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.25)
                    .build()
            }
            .build()
}
