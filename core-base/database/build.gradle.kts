/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
import org.jetbrains.compose.compose

/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.kmp.core.base.library.convention)
}

android {
    namespace = "template.core.base.database"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.room.runtime)
        }

        desktopMain.dependencies {
            implementation(libs.androidx.room.runtime)
        }

        nativeMain.dependencies {
            implementation(libs.androidx.room.runtime)
        }

        nonJsCommonMain.dependencies {
            implementation(libs.androidx.room.runtime)
        }
    }
}
