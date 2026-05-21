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

import com.mifos.core.data.infra.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitorProvider

/**
 * Singleton [NetworkMonitor] backed by cmp-network-monitor.
 *
 * Auto-initializes on first access via [NetworkMonitorProvider.install] — zero-config:
 * Android resolves via ContentProvider, JVM/Desktop via polling, Apple via
 * NWPathMonitor, Web via navigator.onLine.
 */
class NetworkMonitorImpl : NetworkMonitor by NetworkMonitorProvider.install()
