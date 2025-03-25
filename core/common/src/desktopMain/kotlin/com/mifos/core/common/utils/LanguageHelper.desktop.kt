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

actual object LanguageHelper {
    actual fun onAttach(context: Any): Any? {
        val language = System.getProperty("user.language")
        return setLocale(context, language)
    }

    actual fun onAttach(context: Any, defaultLanguage: String): Any? {
        return setLocale(context, defaultLanguage)
    }

    actual fun setLocale(context: Any?, language: String): Any? {
        System.setProperty("user.language", language)
        return context
    }
}
