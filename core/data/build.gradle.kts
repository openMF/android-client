/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
}

androidComponents {
    finalizeDsl { ext ->
        ext.withHostTest {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.database)
            implementation(projects.coreBase.database)
            implementation(projects.coreBase.datastore)
            implementation(projects.coreBase.store)
            // api: re-export the relocated sync/monitor infra (NetworkMonitor, Synchronizer,
            // SyncManager, TimeZoneMonitor) so existing core/data consumers (features, sync,
            // cmp-android) keep the transitive visibility they had when it lived in core/data.
            api(projects.coreBase.data)
            implementation(projects.core.datastore)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(projects.core.firebase)

            implementation(projects.coreBase.common)
            implementation(projects.coreBase.network)
            api(projects.core.store)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            api(libs.cmp.network.monitor)

            // fork-preserved (offline-first-template-migration 02-store-infra-screenstate
            // T5-merge): dropped by the full core/data/build.gradle.kts template overwrite,
            // but the fork's real source (paging repositories, RootNavViewModel's passcode/
            // biometric adapters bound in RepositoryModule.kt, Settings-backed storage
            // adapters) still needs them.
            implementation(libs.androidx.paging.common)
            implementation(libs.mifos.authenticator.passcode)
            implementation(libs.mifos.authenticator.biometrics)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.multiplatform.settings.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.tracing.ktx)
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
        }
    }
}
