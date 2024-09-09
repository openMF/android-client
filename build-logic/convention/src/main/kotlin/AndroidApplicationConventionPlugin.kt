import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.mifos.configureBadgingTasks
import org.mifos.configureGradleManagedDevices
import org.mifos.configureKotlinAndroid
import org.mifos.configurePrintApksTask

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("mifos.android.lint")
                apply("mifos.android.application.jacoco")
                apply("com.dropbox.dependency-guard")
                apply("mifos.detekt.plugin")
                apply("mifos.spotless.plugin")
                apply("mifos.ktlint.plugin")
//                apply("mifos.git.hooks")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }
            extensions.configure<ApplicationAndroidComponentsExtension> {
                configurePrintApksTask(this)
                configureBadgingTasks(extensions.getByType<BaseExtension>(), this)
            }
        }
    }
}
