/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

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
