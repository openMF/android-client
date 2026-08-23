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
    id("kotlinx-serialization")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // NOTE (offline-first-template-migration 01-template-adoption T-AC3-merge): the
            // template's own core/model demo content depends on core/common, but the fork's real
            // core/model source (verified: zero `import com.mifos.core.common.*` references) does
            // NOT — and core/common's fork-preserved utils (MFErrorParser etc., same sub-plan) need
            // `com.mifos.core.model.objects.error.MifosError` + `.utils.{Parcelable,Parcelize}`,
            // which is the OPPOSITE direction. Keeping both would be a common<->model cycle. Dropped
            // to restore the fork's original (working) common -> model direction.
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
    }
}