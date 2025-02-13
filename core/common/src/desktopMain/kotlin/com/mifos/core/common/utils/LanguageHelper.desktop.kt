package com.mifos.core.common.utils

actual object LanguageHelper {
    actual fun onAttach(context: Any): Any? {
        // Desktop-specific implementation
        val language = System.getProperty("user.language")
        return setLocale(context, language)
    }

    actual fun onAttach(context: Any, defaultLanguage: String): Any? {
        // Desktop-specific implementation
        return setLocale(context, defaultLanguage)
    }

    actual fun setLocale(context: Any?, language: String): Any? {
        // Desktop-specific implementation
        System.setProperty("user.language", language)
        return context
    }
}