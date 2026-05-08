plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.davidread.habittracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.davidread.habittracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.davidread.habittracker.HabitTrackerTestRunner"
        manifestPlaceholders["CLEAR_TEXT_TRAFFIC"] = "false"
    }

    buildTypes {
        create("localHostDebug") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
            manifestPlaceholders["CLEAR_TEXT_TRAFFIC"] = "true"
        }

        create("mockInAppDebug") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
        }

        release {
            isMinifyEnabled = false
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

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE-notice.md"
            )
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.google.material)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.compose.material)
    implementation(libs.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.tink.android)
    implementation(libs.security.crypto)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    kapt(libs.hilt.android.compiler)
    kapt(libs.room.compiler)

    val debugLikeBuildTypes = listOf("debug", "localHostDebug", "mockInAppDebug")
    val debugOnlyDeps = listOf(
        libs.ui.tooling,
        libs.leakcanary.android,
        libs.compose.ui.test.manifest
    )
    debugLikeBuildTypes.forEach { buildType ->
        debugOnlyDeps.forEach { dep ->
            add("${buildType}Implementation", dep)
        }
    }

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.compose.ui.test)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
}
