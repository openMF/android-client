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
    // fork-preserved (offline-first-template-migration 02-store-infra-screenstate
    // T5-merge): dropped by the full core/domain/build.gradle.kts template overwrite,
    // but the fork's real use-case validation messages (compose.resources below)
    // still need the Compose plugin applied at this module.
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            api(projects.core.data)
            api(projects.core.model)
            // Fork use-cases (paging data sources + data-access use-cases) reference these
            // directly in their public signatures; core.data exposes them only via
            // `implementation`, so core/domain must depend on them itself. (Restores deps the
            // template-sync's state-free core/domain build.gradle dropped — offline-first 04b.)
            api(projects.core.database)
            api(projects.core.network)
            api(libs.androidx.paging.common)
            implementation(libs.ktor.client.core)

            implementation(compose.runtime)
            implementation(compose.components.resources)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "core.domain.generated.resources"
}