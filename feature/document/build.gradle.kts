/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mifos.feature.document"
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.ui)
            // RULE-FEATURE-USES-ONLY-CORE — feature modules only depend on
            // projects.core.*, never projects.coreBase.* or projects.core.network.
            // Wave 9 (Phase C of store5-adoption) dropped projects.core.domain:
            // the surviving call sites use DocumentRepository (core.data) directly
            // via SubmitHandler / ScreenState instead of pure-delegator use cases.
            implementation(projects.core.common)
            implementation(projects.core.model)
            implementation(projects.core.data)
            implementation(projects.core.ui)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.filekit.core)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.dialog.compose)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
