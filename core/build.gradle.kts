import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

fun getApiKey(): String {
    val properties = Properties()
    val file = File(rootProject.projectDir, "local.properties")
    if (file.exists()) {
        FileInputStream(file).use { properties.load(it) }
    }
    return properties.getProperty("TMDB_API_KEY", "")
}

android {
    namespace = "com.saefulrdevs.core"
    compileSdk = 35


    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "TMDB_API_KEY", "\"${getApiKey()}\"")
        buildConfigField("String", "BASE_URL", "\"https://api.themoviedb.org/3/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.espresso.idling.resource)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    api(libs.material)
    api(libs.glide)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    androidTestImplementation(libs.room.testing)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.koin.core)
    implementation(libs.koin.android)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.lifecycle.extensions)
    ksp(libs.lifecycle.compiler)

    // Dependensi SQLCipher untuk Android
    implementation(libs.zetetic.android.database.sqlcipher)

    // Dependensi AndroidX SQLite
    implementation(libs.androidx.sqlite)

    implementation(libs.materialsearchbar)

    // env
}