/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.kmp.koin.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mifos.core.store"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Foundation: state model + UI defaults the seam customizes.
            // `api` so consumers get LocalScreenStateDefaults, ScreenState*, etc.
            // by depending on `core/store` alone.
            api(projects.coreBase.store)
            api(projects.coreBase.ui)

            // Compose runtime — needed for the @Composable appScreenStateDefaults() factory.
            implementation(compose.runtime)

            implementation(projects.core.database)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit.logging)
        }
    }
}
