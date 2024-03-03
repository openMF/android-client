pluginManagement {
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
        jcenter()
    }
}

rootProject.name = "AndroidClient"
include(":mifosng-android")
include(":core:designsystem")
include(":core:datastore")
include(":core:common")
include(":feature:auth")
include(":core:network")
include(":core:data")
