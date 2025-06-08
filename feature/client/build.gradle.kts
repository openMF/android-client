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
}

android {
    namespace = "com.mifos.feature.client"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.datastore)
//            implementation(project(":feature:data-table"))

            implementation(libs.kotlinx.serialization.json)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.paging.common)
        }
        
        androidMain.dependencies {
            implementation(libs.maps.compose)
            implementation(libs.androidx.material)
            implementation(libs.accompanist.permission)
            implementation(libs.ktor.client.android)

            implementation(libs.androidx.paging.compose)
        }
    }
}
