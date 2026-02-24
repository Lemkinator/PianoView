import com.android.build.api.dsl.CommonExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

/**
 * Converts a camelCase or mixedCase string to ENV_VAR_STYLE (uppercase with underscores).
 * Example: githubAccessToken -> GITHUB_ACCESS_TOKEN
 */
fun String.toEnvVarStyle(): String = replace(Regex("([a-z])([A-Z])"), "$1_$2").uppercase()

/**
 * Note: To configure GitHub credentials, you have to generate an access token with at least `read:packages` scope at
 * https://github.com/settings/tokens/new and then add it to any of the following:
 *
 * - Add `ghUsername` and `ghAccessToken` to Global Gradle Properties
 * - Set `GH_USERNAME` and `GH_ACCESS_TOKEN` in your environment variables or
 * - Create a `github.properties` file in your project folder with the following content:
 *      ghUsername=&lt;YOUR_GITHUB_USERNAME&gt;
 *      ghAccessToken=&lt;YOUR_GITHUB_ACCESS_TOKEN&gt;
 */
fun getProperty(key: String): String =
    Properties().apply { rootProject.file("github.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }.getProperty(key)
        ?: rootProject.findProperty(key)?.toString()
        ?: System.getenv(key.toEnvVarStyle())
        ?: throw GradleException("Property $key not found")

val githubUsername = getProperty("ghUsername")
val githubAccessToken = getProperty("ghAccessToken")

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {
    plugins.withId("com.android.base") {
        project.extensions.findByType(CommonExtension::class.java)?.apply {
            compileOptions.apply {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
        }
    }
    afterEvaluate {
        if (!project.plugins.hasPlugin(libs.plugins.maven.publish.get().pluginId)) {
            return@afterEvaluate
        }
        val artifact = "piano-view"
        group = "io.github.lemkinator"
        version = libs.versions.pianoview.get()
        println("Evaluated $group:$artifact:$version")
        project.extensions.configure<PublishingExtension>("publishing") {
            publications {
                create<MavenPublication>("mavenJava") {
                    artifactId = artifact
                    afterEvaluate {
                        from(components["release"])
                    }
                    pom {
                        name = artifact
                        description = "A customizable piano view for Android, written in Kotlin."
                        url = "https://github.com/Lemkinator/PianoView"
                        developers {
                            developer {
                                id = "Lemkinator"
                                name = "Leonard Lemke"
                                email = "leo@leonard-lemke.com"
                                url = "https://www.leonard-lemke.com"
                                timezone = "Europe/Berlin"
                            }
                        }
                        scm {
                            connection = "scm:git:git://github.com/Lemkinator/PianoView.git"
                            developerConnection = "scm:git:ssh://github.com/Lemkinator/PianoView.git"
                            url = "https://github.com/Lemkinator/PianoView"
                        }
                        issueManagement {
                            system = "GitHub Issues"
                            url = "https://github.com/Lemkinator/PianoView/issues"
                        }
                        licenses {
                            license {
                                name = "Apache-2.0"
                                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                                distribution = "repo"
                            }
                        }
                    }
                }
            }
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/lemkinator/PianoView")
                    credentials {
                        username = githubUsername
                        password = githubAccessToken
                    }
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}