/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.di

import com.mifos.core.common.utils.Constants
import com.mifos.room.MifosDatabase
import kpt.core.base.database.DatabaseNaming
import kpt.core.base.database.platformDatabaseModule
import org.koin.core.module.Module

// offline-first-template-migration 01-template-adoption T-AC3-merge: the fork's own
// `template.core.base.database.AppDatabaseFactory` bridge (early-template-snapshot artifact)
// no longer exists upstream. The current template's `core-base/database` owns database
// construction entirely via `platformDatabaseModule<T>` (driver/dispatcher/fallback boilerplate
// centralized there) — a consumer only supplies its concrete [MifosDatabase] type + naming.
actual val PlatformSpecificDatabaseModule: Module = platformDatabaseModule<MifosDatabase>(
    DatabaseNaming(fileName = Constants.DATABASE_NAME, desktopDirName = "MifosFieldOfficer"),
)
