plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.appturcoespanol"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.appturcoespanol"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navegacion entre pantallas
    implementation(libs.androidx.navigation.compose)

    // Arquitectura MVVM
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Manejo de estados con ciclo de vida
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Persistencia local
    implementation(libs.androidx.datastore.preferences)

    // Corrutinas
    implementation(libs.kotlinx.coroutines.android)

    // Animaciones
    implementation(libs.androidx.compose.animation)


        // Kotest
        testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
        testImplementation("io.kotest:kotest-assertions-core:5.8.0")

        // JUnit 5
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

        // MockK
        testImplementation("io.mockk:mockk:1.13.10")

        // Compose UI Test
        androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")
        debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

// Obligatorio para usar JUnit 5
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()}

}