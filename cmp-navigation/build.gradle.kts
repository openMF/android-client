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
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mifos.kmp.koin)
}

android {
    namespace = "com.mifos.cmp.navigation"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
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
            implementation(projects.feature.document)
            implementation(projects.feature.groups)
            implementation(projects.feature.loan)
            implementation(projects.feature.note)
            implementation(projects.feature.offline)
            implementation(projects.feature.pathTracking)
            implementation(projects.feature.report)
            implementation(projects.feature.savings)
            implementation(projects.feature.search)
            implementation(projects.feature.settings)
            implementation(projects.feature.splash)

//            implementation(project.libs.mifos.passcode)
        }
    }
}