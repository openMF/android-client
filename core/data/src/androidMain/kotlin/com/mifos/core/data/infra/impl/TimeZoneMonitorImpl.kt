/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.infra.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.tracing.trace
import com.mifos.core.data.infra.TimeZoneMonitor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import template.core.base.common.manager.DispatcherManager
import java.time.ZoneId

internal class TimeZoneMonitorImpl(
    private val context: Context,
    dispatchManager: DispatcherManager,
) : TimeZoneMonitor {

    override val currentTimeZone: SharedFlow<TimeZone> = callbackFlow {
        // Send the default time zone first.
        trySend(TimeZone.currentSystemDefault())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                val zoneIdFromIntent = if (VERSION.SDK_INT < VERSION_CODES.R) {
                    null
                } else {
                    intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.let { timeZoneId ->
                        val zoneId = ZoneId.of(timeZoneId, ZoneId.SHORT_IDS)
                        zoneId.toKotlinTimeZone()
                    }
                }

                trySend(zoneIdFromIntent ?: TimeZone.currentSystemDefault())
            }
        }

        trace("TimeZoneBroadcastReceiver.register") {
            context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
        }

        // Send again after registration — registration can take several ms; this reduces
        // the chance of missing an early TZ change.
        trySend(TimeZone.currentSystemDefault())

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(dispatchManager.io)
        .shareIn(
            scope = dispatchManager.appScope,
            started = SharingStarted.WhileSubscribed(5_000),
            replay = 1,
        )
}
