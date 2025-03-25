/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package com.mifos.cmp_navigation

import org.koin.dsl.module

object KoinModules {

    private val featureModules = module {
        includes(
            About
        )
    }
    val allModules = listOf(
        featureModules,
    )
}
