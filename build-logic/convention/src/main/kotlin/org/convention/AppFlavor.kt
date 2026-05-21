/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension

/**
 * AGP-side flavor registration helper shared by every Android-bearing module
 * (com.android.application + com.android.library).
 *
 * `kmp-product-flavors v1.1.2`'s `bridgeAgpProductFlavors` runs in afterEvaluate
 * which is too late for AGP variant resolution. We register flavors here
 * synchronously inside KMPFlavorsConventionPlugin's pluginManager.withPlugin
 * callback — same lifecycle moment as a hand-written `android { productFlavors {} }`
 * block.
 *
 * Both [ApplicationExtension] and [com.android.build.api.dsl.LibraryExtension]
 * extend [CommonExtension] and expose a writable productFlavors container.
 */
@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType,
}

@Suppress("EnumEntryName")
enum class AppFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(FlavorDimension.contentType),
}

/**
 * Configure base `demo`/`prod` AGP product flavors on a [CommonExtension]
 * (works for both `com.android.application` and `com.android.library`).
 *
 * Idempotent — `productFlavors.create()` would error on duplicate, but in
 * practice this helper is invoked once per module through
 * `KMPFlavorsConventionPlugin`.
 */
fun configureFlavors(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        if (FlavorDimension.contentType.name !in flavorDimensions) {
            flavorDimensions += FlavorDimension.contentType.name
        }
        productFlavors {
            AppFlavor.values().forEach { flv ->
                if (findByName(flv.name) == null) {
                    create(flv.name) {
                        dimension = flv.dimension.name
                        if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                            flv.applicationIdSuffix?.let { applicationIdSuffix = it }
                        }
                    }
                }
            }
        }
    }
}
