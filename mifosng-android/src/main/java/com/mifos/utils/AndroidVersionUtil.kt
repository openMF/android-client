package com.mifos.utils

import android.os.Build

/**
 *
 * Created by Rajan Maurya on 04/09/16.
 */
object AndroidVersionUtil {
    val apiVersion: Int
        get() = Build.VERSION.SDK_INT

    @JvmStatic
    fun isApiVersionGreaterOrEqual(thisVersion: Int): Boolean {
        return Build.VERSION.SDK_INT >= thisVersion
    }
}