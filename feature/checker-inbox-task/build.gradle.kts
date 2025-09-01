/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.mifos.cmp.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mifos.feature.checker.inbox.task"
}

kotlin {
    sourceSets{
        commonMain.dependencies {
            implementation(projects.core.domain)

            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit.logging)
            implementation(libs.ui.backhandler)
            implementation(libs.kotlin.reflect)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}