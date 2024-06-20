import com.javernaut.whatthecodec.buildlogic.extension.apply
import com.javernaut.whatthecodec.buildlogic.extension.implementation
import com.javernaut.whatthecodec.buildlogic.extension.ksp
import com.javernaut.whatthecodec.buildlogic.extension.libs
import dagger.hilt.android.plugin.HiltExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.google.ksp)
                apply(libs.plugins.google.hilt)
            }

            dependencies {
                implementation(libs.dagger.hilt.android)
                ksp(libs.dagger.hilt.compiler)
            }

            extensions.configure<HiltExtension> {
                enableAggregatingTask = true
            }
        }
    }
}
