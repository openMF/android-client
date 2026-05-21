/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.data.infra.TimeZoneMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.TimeZone

/**
 * Non-Android `TimeZoneMonitor` — emits the current default timezone once.
 * Desktop / iOS / JS / WasmJS don't expose a system-level "timezone changed" broadcast,
 * so we report a single snapshot at construction time.
 */
class TimeZoneMonitorImpl : TimeZoneMonitor {
    override val currentTimeZone: Flow<TimeZone>
        get() = flowOf(TimeZone.currentSystemDefault())
}
