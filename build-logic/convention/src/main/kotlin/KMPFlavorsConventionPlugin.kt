/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

/* =================================================================================
 *  DO NOT EDIT to add consumer-specific flavors. This file is SYNCED from
 *  kmp-project-template into every downstream consumer app via sync-dirs.sh.
 *  Edits here will be overwritten on the next sync.
 *
 *  To add consumer-specific flavors / dimensions / overrides, create:
 *      build-logic/convention/src/main/kotlin/local/LocalFlavors.kt
 *
 *  That local/ directory is excluded from sync-dirs.sh and survives every sync.
 *  See docs/FLAVORS_EXTENSION.md.
 * ================================================================================= */

import com.android.build.api.dsl.CommonExtension
import com.mobilebytelabs.kmpflavors.KmpFlavorExtension
import com.mobilebytelabs.kmpflavors.KmpFlavorPlugin
import org.convention.configureFlavors
import org.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin that wires `kmp-product-flavors` with the BASE flavor contract
 * every downstream consumer app inherits:
 *
 * - `tier`: `demo` (default — demo credentials for testers / public demo APK) and
 *   `prod` (real customers).
 * - `buildType`: `debug` (default — debuggable), `staging`, `release`.
 *
 * ## AGP-side registration
 *
 * We register the AGP flavors synchronously via [configureFlavors] inside a
 * `pluginManager.withPlugin("com.android.application" | "com.android.library")`
 * callback — this runs at AGP plugin application time, before AGP variant
 * resolution. The plugin's own bridge (`bridgeAgpProductFlavors` / `bridgeAgpBuildTypes`,
 * default `true` in v1.1.5+) is idempotent: it detects the already-registered
 * flavors and no-ops silently. The plugin still handles BuildKonfig codegen +
 * source-set wiring for KMP-side.
 *
 * Consumer apps extend this contract by creating
 * `build-logic/convention/src/main/kotlin/local/LocalFlavors.kt` — see
 * [LocalFlavorsLoader] for the hook and `docs/FLAVORS_EXTENSION.md` for examples.
 */
class KMPFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 1. Apply the upstream plugin (provides KmpFlavorExtension + codegen + source-set wiring)
            pluginManager.apply(KmpFlavorPlugin::class.java)

            // 2. Configure the KMP-side flavor contract.
            //    buildConfigPackage comes from gradle/libs.versions.toml ([versions].appPackage)
            //    so forks change the brand by editing ONE line. buildConfigClassName uses
            //    the plugin default ("BuildKonfig"). bridgeAgp* defaults are safe (idempotent
            //    in v1.1.5+) — no need to touch them.
            extensions.configure<KmpFlavorExtension> {
                // v2.4-rc.0 adoption: active-variant-only path preserved.
                //
                // Matrix mode (autoEnable=true → buildMatrix auto-flips on with our
                // 2 flavors × 3 buildTypes × 6 targets shape) is **not** enabled
                // here because the project's Compose Multiplatform Resources
                // generator emits `expect` declarations in `commonMain` that have
                // no matching `actual` once matrix-mode moves variant-scoped code
                // into per-flavor source sets. This is a documented CMP × kmp-
                // product-flavors limitation — see kmp-product-flavors `docs/
                // REFERENCE.md` "Compatibility windows" + `MIGRATION_v1_to_v2.md`
                // "Common pitfalls" → "Unresolved reference 'BuildKonfig' in
                // commonMain".
                //
                // Once a workaround lands (e.g. CMP exposing a per-flavor resource
                // generator hook, or kmp-product-flavors auto-skipping CMP-
                // generated commonMain expect declarations), this opt-out can drop
                // and matrix mode lights up automatically.
                autoEnable.set(false)
                buildConfigPackage.set(libs.findVersion("appPackage").get().requiredVersion)
                enableBuildTypes.set(true)


                flavorDimensions {
                    register("contentType") { priority.set(0) }
                }

                flavors {
                    register("demo") {
                        dimension.set("contentType")
                        isDefault.set(true)
                        applicationIdSuffix.set(".demo")
                        bundleIdSuffix.set(".demo")
                        buildConfigField("Boolean", "IS_DEMO_BUILD", "true")
                        buildConfigField("String", "BASE_URL", "\"https://demo.openmf.org\"")
                        buildConfigField("String", "DEMO_USERNAME", "\"demo\"")
                        buildConfigField("String", "DEMO_PASSWORD", "\"demo\"")
                    }
                    register("prod") {
                        dimension.set("contentType")
                        buildConfigField("Boolean", "IS_DEMO_BUILD", "false")
                        buildConfigField("String", "BASE_URL", "\"https://api.openmf.org\"")
                        buildConfigField("String", "DEMO_USERNAME", "\"\"")
                        buildConfigField("String", "DEMO_PASSWORD", "\"\"")
                    }
                }

                buildTypes {
                    register("debug") {
                        isDefault.set(true)
                        isDebuggable.set(true)
                        applicationIdSuffix.set(".debug")
                        buildConfigField("Boolean", "ENABLE_LOGGING", "true")
                        buildConfigField("Boolean", "SHOW_DEBUG_OVERLAY", "true")
                        buildConfigField("String", "LOG_TAG", "\"KMPTemplate-DEBUG\"")
                    }
                    register("staging") {
                        isDebuggable.set(false)
                        applicationIdSuffix.set(".staging")
                        buildConfigField("Boolean", "ENABLE_LOGGING", "true")
                        buildConfigField("Boolean", "SHOW_DEBUG_OVERLAY", "false")
                        buildConfigField("String", "LOG_TAG", "\"KMPTemplate-STAGING\"")
                    }
                    register("release") {
                        isDebuggable.set(false)
                        isMinifyEnabled.set(true)
                        buildConfigField("Boolean", "ENABLE_LOGGING", "false")
                        buildConfigField("Boolean", "SHOW_DEBUG_OVERLAY", "false")
                        buildConfigField("String", "LOG_TAG", "\"KMPTemplate\"")
                    }
                }

                // Consumer extension hook — must be the LAST statement so the
                // local file sees the fully-populated extension.
                LocalFlavorsLoader.applyIfPresent(this, target)
            }

            // 3. Synchronous AGP-side flavor registration. Runs as soon as the
            //    Android plugin is applied — BEFORE AGP variant resolution.
            //    Covers both com.android.application (cmp-android) and
            //    com.android.library (every KMP/Android library module).
            listOf("com.android.application", "com.android.library").forEach { agpId ->
                pluginManager.withPlugin(agpId) {
                    val commonExt = extensions.findByType(CommonExtension::class.java)
                    if (commonExt != null) {
                        configureFlavors(commonExt)
                    }
                }
            }
        }
    }
}
