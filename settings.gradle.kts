pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

rootProject.name = "AndroidClient"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":mifosng-android")

include(":core:common")
include(":core:data")
include(":core:designsystem")
include(":core:database")
include(":core:domain")
include(":core:datastore")
include(":core:model")
include(":core:network")
include(":core:testing")
include(":core:ui")

// Lint Modules
include(":lint")

// Library Modules
include(":libs:country-code-picker")
include(":libs:pullrefresh")
include(":libs:mifos-passcode")

include(":feature:about")
include(":feature:activate")
include(":feature:auth")
include(":feature:center")
include(":feature:checker-inbox-task")
include(":feature:client")
include(":feature:collection-sheet")
include(":feature:data-table")
include(":feature:document")
include(":feature:groups")
include(":feature:loan")
include(":feature:note")
include(":feature:offline")
include(":feature:path-tracking")
include(":feature:report")
include(":feature:savings")
include(":feature:search")
include(":feature:settings")
include(":feature:splash")
//include(":feature:passcode")
