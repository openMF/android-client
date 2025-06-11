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
    alias(libs.plugins.mifos.kmp.koin)
}

android {
    namespace = "com.mifos.feature.groups"
}

kotlin{
    sourceSets{
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.ui)

            implementation(projects.core.model)
            implementation(projects.core.domain)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.designsystem)

            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.paging.common)
        }

        androidMain.dependencies {
            implementation(libs.androidx.paging.compose)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(libs.androidx.compose.foundation)
        }
    }
}
