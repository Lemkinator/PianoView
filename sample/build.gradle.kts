plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.lemke.pianoviewsample"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.lemke.pianoviewsample"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}


configurations.configureEach {
    exclude("androidx.appcompat", "appcompat")
    exclude("androidx.fragment", "fragment")
    exclude("androidx.core", "core")
    exclude("androidx.drawerlayout", "drawerlayout")
    exclude("androidx.viewpager", "viewpager")
    exclude("androidx.viewpager2", "viewpager2")
    exclude("androidx.coordinatorlayout", "coordinatorlayout")
    exclude("androidx.recyclerview", "recyclerview")
}

dependencies {
    implementation("io.github.oneuiproject:design:1.2.6")
    implementation("io.github.oneuiproject.sesl:appcompat:1.4.0")
    implementation("io.github.oneuiproject.sesl:material:1.5.0")
    implementation("io.github.oneuiproject:icons:1.1.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
    //noinspection GradleDependency
    implementation("androidx.core:core-ktx:1.9.0")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation(project(":libpianoview"))
}
