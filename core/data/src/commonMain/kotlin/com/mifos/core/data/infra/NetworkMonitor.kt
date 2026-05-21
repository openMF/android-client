/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.infra

/**
 * Project-namespaced re-export of the framework `NetworkMonitor` from
 * `io.github.mobilebytelabs.kmptoolkit.networkmonitor`. Consumers (VMs,
 * repositories, Store5 `asScreenStream`) import this `com.mifos.core.data.infra`
 * typealias instead of reaching directly into `template.core.base.*` or
 * `io.github.mobilebytelabs.*` — preserving the project's facade boundary
 * (see C-features.md "feature modules only use projects.core.*").
 *
 * Provides `isOnline: StateFlow<Boolean>`, `networkStatus: StateFlow<NetworkStatus>`,
 * `networkChanges: SharedFlow<NetworkChangeEvent>`, and `close()`. See the
 * canonical [io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor]
 * doc for full semantics.
 */
typealias NetworkMonitor = io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
