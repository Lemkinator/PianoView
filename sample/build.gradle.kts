plugins {
    alias(libs.plugins.android.application)
}
fun com.android.build.api.dsl.ApplicationBuildType.addConstant(name: String, value: String) {
    manifestPlaceholders += mapOf(name to value)
    buildConfigField("String", name, "\"$value\"")
}

android {
    namespace = "de.lemke.pianoviewsample"
    compileSdk = 36
    defaultConfig {
        applicationId = "de.lemke.pianoviewsample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            addConstant("APP_NAME", "Virtual Piano")
            ndk { debugSymbolLevel = "FULL" }
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            addConstant("APP_NAME", "Virtual Piano (Debug)")
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.icons)
    implementation(libs.core.splashscreen)
    implementation(project(":libpianoview"))
}
