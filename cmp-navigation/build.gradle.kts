/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kmp.koin.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core Modules
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.network) // E1: FeatureRegistry wires the relocated DemoNetworkModule (core/network/demo/di)
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.datastore)
            // core/domain — fork use-cases (UseCaseModule) consumed by the fork feature ViewModels
            // (loan et al). Restored to KoinModules.allModules after a template sync dropped the fork's
            // own DI modules; the use-cases sit above core/data repositories.
            implementation(projects.core.domain)
            // Firebase analytics (firebaseModule + AnalyticsHelper + Compose helpers) via core/firebase.
            implementation(projects.core.firebase)
            // core/platform re-exports core-base/platform (platformModule, GarbageCollectionManager) —
            // the app-shell reaches those through core/ per G-CORE-BASE-ENCAP.
            implementation(projects.core.platform)
            // StoreCacheManager (D7 logout cache-clear, offline-first-template-migration
            // 02-store-infra-screenstate T5) — RootNavViewModel's real fork logout sequence
            // clears every registered Store5 cache. Fork-added dep; owner:merge protects it
            // across future syncs (customization-surface.yaml).
            implementation(projects.core.store)
            // core-base/security is the ONE sanctioned app-shell exception: cmp-navigation is the DI
            // aggregator (KoinModules wires SecurityModule) and reads isReleaseBuild; no core/ wrapper
            // is warranted for a security module the shell itself assembles. Feature modules NEVER
            // depend on core-base — enforced by the encapsulation gate (Phase A).
            implementation(projects.coreBase.security)

            // Backbone shell features (template-owned) — always present in every fork.
            implementation(projects.feature.home)
            implementation(projects.feature.profile)
            implementation(projects.feature.settings)
            // loan feature — first business feature wired via FeatureRegistry (pilot 04/04b).
            implementation(projects.feature.loan)
            // passcode/auth feature — MifosAuthenticatorModule binds PasscodeManager +
            // Passcode/Biometric adapters that RootNavViewModel's fork logout/auth sequence needs.
            implementation(projects.feature.passcode)
            // auth feature — the fork login flow (LoginScreen + authNavGraph) wired into RootNav's
            // AuthenticateUser state. The template sync left the auth graph commented out.
            implementation(projects.feature.auth)
            // Fork feature-module deps come from the fork-owned `feature-deps.gradle.kts` seam
            // (applied at the bottom of this file, S7/F4). A fork adds a feature there, never here.
            implementation(projects.sync)

            // Passcode/biometric authenticator libs — RootNavViewModel's real fork logout sequence
            // consumes org.mifos.authenticator.passcode.{PasscodeManager,PasscodeStorageAdapter} and
            // org.mifos.authenticator.biometrics.BiometricStorageAdapter (same artifacts core/data and
            // feature/passcode depend on). Restored after a template sync dropped them from this module.
            implementation(libs.mifos.authenticator.passcode)
            implementation(libs.mifos.authenticator.biometrics)

            // put your multiplatform dependencies here
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            // Phase 3 (store5-screen-state-persistence 03-vm-scoping) — enables
            // koinNavViewModel() so nav destinations acquire ViewModels scoped to
            // NavBackStackEntry (cleared on pop) instead of Activity (cleared on
            // Activity death). Resolves io.insert-koin:koin-compose-viewmodel-navigation
            // via gradle/libs.versions.toml:259; version is the shared Koin ref.
            implementation(libs.koin.compose.navigation)
            // Provides `com.russhwolf.settings.Settings` referenced by
            // `saveable/PersistentSaveableStateRegistry.kt` at the app root.
            // The `named("plain")` binding itself is contributed by
            // `core-base/datastore/DatastoreBaseModule` (transitively wired in
            // via `core/datastore/DatastoreModule` in `KoinModules.allModules`).
            implementation(libs.multiplatform.settings)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "cmp.navigation.generated.resources"
}

// Fork-owned feature-module dependencies (S7/F4 white-label seam). Applied AFTER the `kotlin { }` block
// above so the `commonMainImplementation` configuration it contributes to already exists. A fork edits
// `feature-deps.gradle.kts`, never this template-owned build file — a template sync full-copies this file.
//
// Apply ONLY when the seam file is present. `feature-deps.gradle.kts` is `owner: fork` (never synced), so a
// fork that adopted the template BEFORE this seam existed — or is mid-adoption — may not have it yet; an
// unconditional `apply(from = …)` then fails the whole configuration ("Could not read script …feature-deps
// .gradle.kts as it does not exist"), which blocks even `syncForkConfig`. Guarding the apply keeps
// cmp-navigation configurable in that window; the fork wires its features by creating the seam file.
rootProject.file("feature-deps.gradle.kts").takeIf { it.exists() }?.let { apply(from = it) }
