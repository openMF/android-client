package com.mifos.core.common.utils

actual object Network {
    fun isOnline(): Boolean =
        js("(typeof window !== 'undefined' && window.navigator.onLine)") as Boolean
}