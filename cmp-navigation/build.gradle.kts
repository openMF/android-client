/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.mifos.kmp.library)
    alias(libs.plugins.mifos.cmp.feature)
    alias(libs.plugins.mifos.kmp.koin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "cmp.navigation"
}

kotlin {
    sourceSets {
        commonMain.dependencies{
            implementation(libs.kotlinx.serialization.json)

            implementation(projects.core.domain)
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.datastore)
            implementation(projects.core.database)
            implementation(projects.core.network)

            implementation(projects.feature.about)
            implementation(projects.feature.activate)
            implementation(projects.feature.auth)
            implementation(projects.feature.center)
            implementation(projects.feature.checkerInboxTask)
            implementation(projects.feature.client)
            implementation(projects.feature.collectionSheet)
            implementation(projects.feature.dataTable)
            implementation(projects.feature.groups)
            implementation(projects.feature.document)
            implementation(projects.feature.loan)
            implementation(projects.feature.note)
            implementation(projects.feature.offline)
            implementation(projects.feature.pathTracking)
            implementation(projects.feature.report)
            implementation(projects.feature.savings)
            implementation(projects.feature.settings)
            implementation(projects.feature.search)

//            implementation(project.libs.mifos.passcode)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.window.size)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.tracing.ktx)
            implementation(libs.koin.android)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "org.mifos.navigation.generated.resources"
}
