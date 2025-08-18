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
    namespace = "com.mifos.feature.note"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.ui)
            implementation(projects.core.common)
            implementation(projects.core.model)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

