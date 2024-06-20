import com.javernaut.whatthecodec.buildlogic.configureKotlinJvm
import com.javernaut.whatthecodec.buildlogic.extension.apply
import com.javernaut.whatthecodec.buildlogic.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.jetbrains.kotlin.jvm)
            }
            configureKotlinJvm()
        }
    }
}
