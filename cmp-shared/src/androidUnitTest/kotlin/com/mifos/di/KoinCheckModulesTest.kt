/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.di

import cmp.navigation.di.KoinModules
import kotlin.test.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

/**
 * offline-first-template-migration 02-store-infra-screenstate T8 — static Koin verification
 * asserting every constructor-injected dependency across the ASSEMBLED app graph
 * ([KoinModules.allModules]) resolves to an existing definition. A missing binding surfaces
 * here as a concrete `MissingKoinDefinitionException` naming the unresolved `KClass`, instead
 * of at cold-start on a device.
 *
 * `koin-test`'s `verify()` operates on a single [org.koin.core.module.Module] and does NOT walk
 * a flat `List<Module>` together (`List<Module>.verifyAll()` checks each module in isolation,
 * which would falsely flag every cross-module dependency `KoinModules.allModules`'s sibling
 * top-level modules resolve at real Koin start). Wrapping the whole list in one combining
 * `module { includes(...) }` merges their definitions the same way Koin's own
 * `koinApplication { modules(allModules) }` does at runtime, so `verify()` sees the full graph.
 *
 * NO `authProviderModule` is included (template REMOVED `core/auth`) — the fork's auth stack is
 * `feature/auth` + `feature/passcode` + the external `mifos-authenticator-passcode`/`-biometrics`
 * libraries, already Koin-wired via `MifosAuthenticatorModule` (reached through
 * `FeatureRegistry.featureKoinModules` once `feature/auth`+`feature/passcode` land their own
 * migration sub-plans — currently empty, so this test only covers the shell + backbone graph
 * until those sub-plans populate it).
 */
class KoinCheckModulesTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `all modules verify`() {
        val combined = module { includes(KoinModules.allModules) }
        combined.verify()
    }
}
