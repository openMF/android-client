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

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(compose.uiTooling)
        }

        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(projects.core.datastore)
            api(projects.core.domain)
        }
    }
}


compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.feature.offline.generated.resources"
}
