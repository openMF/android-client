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

import org.koin.core.module.Module

/**
 * Per-platform Koin module hook for data-layer bindings that can't be expressed
 * in commonMain (e.g. KeyStore-backed crypto, location services).
 *
 * **Currently empty** on every platform — `NetworkMonitor` is bound in commonMain
 * via [com.mifos.core.data.infra.impl.NetworkMonitorImpl] (cmp-network-monitor
 * auto-resolves the platform-correct backend through `NetworkMonitorProvider.install()`),
 * and all other data-layer dependencies live in [RepositoryModule].
 *
 * Kept as a forward-looking hook so future platform-only bindings have a stable
 * insertion point.
 */
expect val platformModule: Module
