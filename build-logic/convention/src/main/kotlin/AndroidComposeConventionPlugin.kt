import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.javernaut.whatthecodec.buildlogic.configureAndroidCompose
import com.javernaut.whatthecodec.buildlogic.extension.apply
import com.javernaut.whatthecodec.buildlogic.extension.hasPlugin
import com.javernaut.whatthecodec.buildlogic.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.plugins.jetbrains.kotlin.compose)

                when {
                    hasPlugin(libs.plugins.android.application) ->
                        configure<ApplicationExtension> {
                            configureAndroidCompose(this)
                        }

                    hasPlugin(libs.plugins.android.library) ->
                        configure<LibraryExtension> {
                            configureAndroidCompose(this)
                        }
                }
            }
        }
    }
}
