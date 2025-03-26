plugins {
    alias(libs.plugins.mifos.kmp.library)
//    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mifos.kmp.koin)
}

android {
    namespace = "com.mifos.cmp.navigation"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.network)

            implementation(projects.feature.about)
            implementation(projects.feature.activate)
            implementation(projects.feature.auth)
            implementation(projects.feature.center)
            implementation(projects.feature.checkerInboxTask)
            implementation(projects.feature.client)
            implementation(projects.feature.collectionSheet)
            implementation(projects.feature.dataTable)
            implementation(projects.feature.document)
            implementation(projects.feature.groups)
            implementation(projects.feature.loan)
            implementation(projects.feature.note)
            implementation(projects.feature.offline)
            implementation(projects.feature.pathTracking)
            implementation(projects.feature.report)
            implementation(projects.feature.savings)
            implementation(projects.feature.search)
            implementation(projects.feature.settings)
            implementation(projects.feature.splash)

        }
    }
}