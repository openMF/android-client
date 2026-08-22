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
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kermit.logging)
            api(libs.kotlinx.datetime)
            // Re-export core-base/common (CommonModule DI, base utilities) so app-shell + feature
            // modules depend on core/common, never core-base/common directly (encapsulation, Phase A).
            api(projects.coreBase.common)
            // --- fork-preserved (offline-first-template-migration 01-template-adoption T-AC3-merge) ---
            // core/common/**/utils/{FileKitUtil,MFErrorParser,ServerConfig}.kt are pre-existing
            // field-officer business-logic files the template's own core/common has no equivalent
            // for. `customization-surface.yaml`'s `core/**` → owner:template rule correctly blind-
            // overwrites this build.gradle.kts (matching the fork's rest of core/**), but that drops
            // these fork-only deps the untouched src/ still references. Restored additively (not a
            // full fork-original revert — the template's `coreBase.common` re-export above stays) so
            // AC-3 (`:core:network:compileCommonMainKotlinMetadata` exit 0) holds. The actual
            // architectural migration of these utils onto the template's ScreenState/AppErrorMapper
            // pattern is explicitly Out of scope for this sub-plan (owned by 03-core-datastate-removal).
            api(projects.core.model)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.filekit.core)
            implementation(libs.filekit.coil)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.dialog.compose)
            implementation(libs.filekit.dialogs)
            implementation(libs.ktor.client.core)
        }
    }
}