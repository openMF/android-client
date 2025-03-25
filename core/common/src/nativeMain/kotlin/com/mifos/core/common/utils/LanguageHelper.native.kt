/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual object LanguageHelper {
    actual fun onAttach(context: Any): Any? {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val storedLanguage = userDefaults.stringForKey("language")
        val systemLanguage = (NSLocale.preferredLanguages.firstOrNull() as? String)
            ?.substringBefore('_')

        val language = storedLanguage ?: systemLanguage ?: "en"
        return setLocale(context, language)
    }

    actual fun onAttach(context: Any, defaultLanguage: String): Any? {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val language = userDefaults.stringForKey("language") ?: defaultLanguage
        return setLocale(context, language)
    }

    actual fun setLocale(context: Any?, language: String): Any? {
        NSUserDefaults.standardUserDefaults.apply {
            setObject(language, forKey = "language")
            synchronize()
        }
        return context
    }
}
