# Convention Plugins

The `build-logic` folder defines project-specific convention plugins, used to keep a single
source of truth for common module configurations.

This approach is heavily based on
[https://developer.squareup.com/blog/herding-elephants/](https://developer.squareup.com/blog/herding-elephants/)
and
[https://github.com/jjohannes/idiomatic-gradle](https://github.com/jjohannes/idiomatic-gradle).

By setting up convention plugins in `build-logic`, we can avoid duplicated build script setup,
messy `subproject` configurations, without the pitfalls of the `buildSrc` directory.

`build-logic` is an included build, as configured in the root
[`settings.gradle.kts`](../settings.gradle.kts).

Inside `build-logic` is a `convention` module, which defines a set of plugins that all normal
modules can use to configure themselves.

`build-logic` also includes a set of `Kotlin` files used to share logic between plugins themselves,
which is most useful for configuring Android components (libraries vs applications) with shared
code.

These plugins are *additive* and *composable*, and try to only accomplish a single responsibility.
Modules can then pick and choose the configurations they need.
If there is one-off logic for a module without shared code, it's preferable to define that directly
in the module's `build.gradle`, as opposed to creating a convention plugin with module-specific
setup.

Current list of convention plugins:

- [`mifos.android.application`](convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt),
  Configures common Android and Kotlin options.

- [`mifos.android.application.compose`](convention/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt),
  Configures Jetpack Compose options

- [`android.application.firebase`](convention/src/main/kotlin/AndroidApplicationFirebaseConventionPlugin.kt):
  Configures Firebase setup for Android application modules.

- [`mifos.android.application.flavors`](convention/src/main/kotlin/AndroidApplicationFlavorsConventionPlugin.kt):
  Configures product flavors and related build variant settings for Android application modules.

- [`android.lint`](convention/src/main/kotlin/AndroidLintConventionPlugin.kt):
  Configures shared Android lint rules and lint behavior across modules.

- [`cmp.feature.convention`](convention/src/main/kotlin/CMPFeatureConventionPlugin.kt):
  Configures Compose Multiplatform feature modules with shared feature-level setup.

- [`kmp.core.base.library.convention`](convention/src/main/kotlin/KMPCoreBaseLibraryConventionPlugin.kt):
  Configures base conventions for core Kotlin Multiplatform library modules.

- [`kmp.koin.convention`](convention/src/main/kotlin/KMPKoinConventionPlugin.kt):
  Configures Koin dependency injection setup for Kotlin Multiplatform modules.

- [`kmp.library.convention`](convention/src/main/kotlin/KMPLibraryConventionPlugin.kt):
  Configures common Kotlin Multiplatform library settings and shared KMP options.

- [`mifos.kmp.room`](convention/src/main/kotlin/KMPRoomConventionPlugin.kt):
  Configures Room database support and related settings for Kotlin Multiplatform modules.

- [`mifos.android.koin`](convention/src/main/kotlin/KoinAndroidConventionPlugin.kt):
  Configures Koin integration for Android-specific modules.

- [`mifos.detekt.plugin`](convention/src/main/kotlin/MifosDetektConventionPlugin.kt):
  Configures Detekt static analysis rules and quality checks.

- [`mifos.git.hooks`](convention/src/main/kotlin/MifosGitHooksConventionPlugin.kt):
  Configures Git hooks to enforce project checks and development workflow standards.

- [`mifos.spotless.plugin`](convention/src/main/kotlin/MifosSpotlessConventionPlugin.kt):
  Configures Spotless code formatting for consistent project-wide style.
