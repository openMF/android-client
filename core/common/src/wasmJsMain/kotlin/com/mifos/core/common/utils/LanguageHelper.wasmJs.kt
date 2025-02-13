package com.mifos.core.common.utils

actual object LanguageHelper {
    actual fun onAttach(context: Any): Any? {
        // WASM-specific implementation
        val language = js("navigator.language") as String
        return setLocale(context, language)
    }

    actual fun onAttach(context: Any, defaultLanguage: String): Any? {
        // WASM-specific implementation
        return setLocale(context, defaultLanguage)
    }

    actual fun setLocale(context: Any?, language: String): Any? {
        // WASM-specific implementation
        js("document.documentElement.lang = language")
        return context
    }
}