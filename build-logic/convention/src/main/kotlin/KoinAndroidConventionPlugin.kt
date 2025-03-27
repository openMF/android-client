import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.mifos.libs

class KoinAndroidConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            dependencies {
                add("implementation", platform(libs.findLibrary("koin-bom").get()))
                add("implementation", libs.findLibrary("koin-android").get())
                add("implementation", libs.findLibrary("koin.androidx.compose").get())

                add("implementation", libs.findLibrary("koin.core").get())
                add("implementation", libs.findLibrary("koin.androidx.navigation").get())
                add("implementation", libs.findLibrary("koin.androidx.compose").get())
                add("implementation", libs.findLibrary("koin.core.viewmodel").get())
            }
        }
    }
}
