plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
}

android {
    namespace = "com.noskill.anymeal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.noskill.anymeal"
        minSdk = 26 // Tu minSdk actual
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Asegúrate de que estas versiones sean Java 8 o superior para usar java.time
        sourceCompatibility = JavaVersion.VERSION_1_8 // Cambiado a 1.8
        targetCompatibility = JavaVersion.VERSION_1_8 // Cambiado a 1.8
    }

    kotlinOptions {
        jvmTarget = "1.8" // Cambiado a 1.8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.1")


    // Image loading
    implementation("io.coil-kt:coil-compose:2.4.0")

    // LiveData integration
    implementation("androidx.compose.runtime:runtime-livedata")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Retrofit para las peticiones de red
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Convertidor Gson para serializar JSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp - Se añade explícitamente para controlar la versión
    // La versión 4.12.0 es compatible con el interceptor y con Retrofit 2.9.0
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Interceptor de logging (muy útil para depurar)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.compose.material3:material3:1.3.2") // Or the latest version
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Hilt (Inyección de Dependencias)
    // 2. Añade las librerías de Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // --- ¡AÑADE ESTA DEPENDENCIA PARA EL DESUGARING DE JAVA 8 APIs! ---
    implementation("com.android.tools:desugar_jdk_libs:2.1.5")


}
